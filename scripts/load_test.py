"""Concurrent load test for LumaFeed hot ranking and following-feed pagination."""

import argparse
import json
import statistics
import subprocess
import sys
import threading
import time
import urllib.parse
import urllib.request
from concurrent.futures import ThreadPoolExecutor
from datetime import datetime
from pathlib import Path


def request(method: str, url: str, token: str = "", body: dict | None = None) -> dict:
    """Call one JSON endpoint and raise on non-success application responses."""
    data = None if body is None else json.dumps(body, ensure_ascii=False).encode("utf-8")
    headers = {"Content-Type": "application/json"}
    if token:
        headers["authorization"] = token
    req = urllib.request.Request(url, data=data, method=method, headers=headers)
    with urllib.request.urlopen(req, timeout=15) as response:
        payload = json.loads(response.read().decode("utf-8"))
    if payload.get("success") is False:
        raise RuntimeError(payload.get("errorMsg") or "request failed")
    return payload


def login(base_url: str, phone: str, redis_cli: str) -> str:
    """Request a local OTP and exchange it for a LumaFeed Redis token."""
    request("POST", f"{base_url}/user/code?phone={urllib.parse.quote(phone)}")
    code = subprocess.check_output(
        [redis_cli, "GET", f"login:code:{phone}"],
        text=True,
    ).strip()
    if not code:
        raise RuntimeError(f"OTP not found for {phone}")
    payload = request(
        "POST",
        f"{base_url}/user/login",
        body={"phone": phone, "code": code},
    )
    return str(payload["data"])


def prepare_feed(base_url: str, user_token: str, author_phone: str, redis_cli: str) -> None:
    """Ensure the test user follows an author and the author has one fresh post."""
    request("PUT", f"{base_url}/follow/2/true", user_token)
    author_token = login(base_url, author_phone, redis_cli)
    request(
        "POST",
        f"{base_url}/blog",
        author_token,
        {
            "shopId": 1,
            "title": f"Feed 压测数据 {datetime.now().strftime('%H:%M:%S')}",
            "images": "/imgs/blogs/blog1.jpg",
            "content": "用于关注 Feed 分页并发读取性能验证。",
            "liked": 0,
            "comments": 0,
        },
    )


def percentile(values: list[float], ratio: float) -> float:
    """Return a nearest-rank percentile."""
    if not values:
        return 0.0
    ordered = sorted(values)
    index = min(len(ordered) - 1, max(0, int(len(ordered) * ratio) - 1))
    return ordered[index]


def main() -> None:
    """Run alternating hot-ranking and feed requests under a fixed concurrency."""
    parser = argparse.ArgumentParser()
    parser.add_argument("--base-url", default="http://127.0.0.1:8081")
    parser.add_argument("--phone", default="13686869696")
    parser.add_argument("--author-phone", default="13838411438")
    parser.add_argument("--redis-cli", default="redis-cli")
    parser.add_argument("--concurrency", type=int, default=32)
    parser.add_argument("--duration", type=int, default=10)
    parser.add_argument("--output", default="docs/load-test-result.json")
    args = parser.parse_args()

    token = login(args.base_url, args.phone, args.redis_cli)
    prepare_feed(args.base_url, token, args.author_phone, args.redis_cli)
    deadline = time.perf_counter() + args.duration
    lock = threading.Lock()
    latencies: list[float] = []
    successes = 0
    failures = 0
    status_counts: dict[str, int] = {"hot": 0, "feed": 0}

    def worker(worker_id: int) -> None:
        nonlocal successes, failures
        sequence = 0
        while time.perf_counter() < deadline:
            sequence += 1
            endpoint = "feed" if (worker_id + sequence) % 2 == 0 else "hot"
            path = (
                "/blog/of/follow?lastId=9999999999999&offset=0"
                if endpoint == "feed"
                else "/blog/hot?current=1"
            )
            started = time.perf_counter()
            try:
                request("GET", args.base_url + path, token)
                ok = True
            except Exception:
                ok = False
            elapsed = (time.perf_counter() - started) * 1000
            with lock:
                latencies.append(elapsed)
                status_counts[endpoint] += 1
                if ok:
                    successes += 1
                else:
                    failures += 1

    wall_start = time.perf_counter()
    with ThreadPoolExecutor(max_workers=max(1, args.concurrency)) as executor:
        list(executor.map(worker, range(max(1, args.concurrency))))
    wall_seconds = max(0.001, time.perf_counter() - wall_start)

    result = {
        "timestamp": datetime.now().isoformat(timespec="seconds"),
        "baseUrl": args.base_url,
        "concurrency": args.concurrency,
        "durationSeconds": args.duration,
        "totalRequests": len(latencies),
        "successes": successes,
        "failures": failures,
        "successRate": round(successes * 100 / max(1, len(latencies)), 2),
        "requestsPerSecond": round(len(latencies) / wall_seconds, 2),
        "latencyMs": {
            "mean": round(statistics.fmean(latencies), 2) if latencies else 0,
            "p50": round(percentile(latencies, 0.50), 2),
            "p95": round(percentile(latencies, 0.95), 2),
            "p99": round(percentile(latencies, 0.99), 2),
            "max": round(max(latencies), 2) if latencies else 0,
        },
        "endpointCounts": status_counts,
    }
    output = Path(args.output)
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(json.dumps(result, ensure_ascii=False, indent=2), encoding="utf-8")
    print(json.dumps(result, ensure_ascii=False, indent=2))


if __name__ == "__main__":
    try:
        main()
    except Exception as error:
        print(f"load test failed: {error}", file=sys.stderr)
        raise
