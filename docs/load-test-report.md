# LumaFeed Redis Read Path Load Test

Test date: 2026-11-20

Environment:

- Single local Spring Boot instance on `127.0.0.1:8081`
- MySQL 8, Redis 5.0.14 and Nginx running on the same Windows host
- 64 concurrent Python worker threads
- Alternating `/blog/hot?current=1` and `/blog/of/follow` requests

Result:

| Metric | Value |
|---|---:|
| Duration | 15 seconds |
| Total requests | 5,448 |
| Successful requests | 5,448 |
| Success rate | 100% |
| Throughput | 360.10 requests/s |
| Mean latency | 176.98 ms |
| P50 latency | 164.67 ms |
| P95 latency | 299.15 ms |
| P99 latency | 321.08 ms |
| Max latency | 357.24 ms |

Endpoint distribution:

- Hot ranking: 2,733 requests
- Following feed: 2,715 requests

Rate limiter verification:

- Scope: one user on the like endpoint
- Limit: 120 permits per 60 seconds
- 126 sequential requests: 120 succeeded and 6 were rejected by Redisson

Reproduce:

```powershell
python scripts\load_test.py `
  --concurrency 64 `
  --duration 15 `
  --redis-cli "<path-to-redis-cli.exe>" `
  --output docs/load-test-result-64.json
```

This result measures a single-host development deployment. It is intended for
regression and capacity comparison, not as a production capacity guarantee.
