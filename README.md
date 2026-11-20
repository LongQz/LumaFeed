# LumaFeed

[![Java](https://img.shields.io/badge/Java-17-1f6feb)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-2.3-6db33f)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479a1)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-5.0-dc382d)](https://redis.io/)

LumaFeed 是一个本地生活内容社区，支持探店笔记、话题讨论、关注 Feed、点赞、
签到、附近商户和优惠券秒杀。项目使用独立品牌、包名和数据库结构。

## 项目矩阵

- LumaFeed：本地生活内容社区与 Redis 高并发实践
- [LumenPilot](https://github.com/LongQz/LumenPilot)：LangGraph 运营分析 Agent
- [NovaCart](https://github.com/LongQz/NovaCart)：商城、闪购与 Spring AI 全栈项目

## 技术栈

- Java 17
- Spring Boot
- Spring Security
- MyBatis-Plus
- MySQL
- Redis、Redisson、Lua
- Elasticsearch
- Nginx 静态页面与 API 代理

## 目录

```text
LumaFeed/
├─ backend/        Java 后端
├─ frontend/       用户端网页
├─ deploy/nginx/   本地 Nginx 配置和程序
├─ README.md
└─ NOTICE.md
```

## 主要接口

```text
普通用户端：http://127.0.0.1:8080
Java API：  http://127.0.0.1:8081
```

运营 Agent 使用的内部接口：

```text
GET  /admin/analytics/overview
GET  /admin/analytics/merchants
POST /admin/ops/tasks
```

内部接口使用：

```text
X-LumaFeed-Agent-Token
```

## Redis 并发与限流

- `blog:hot:zset` 维护热门笔记排名，启动时按数据库点赞数预热。
- 点赞状态使用 Redis Set，点赞切换、计数和热门分数由 Lua 原子更新。
- `RRateLimiter` 对发布、点赞、Feed、热门读取和秒杀接口做分布式限流。
- GEO 商户索引会在应用启动时从 MySQL 自动重建。

### 热门榜与 Feed 并发压测

压测使用 Python 标准库启动固定数量并发线程，同时交替请求：

```text
GET /blog/hot?current=1
GET /blog/of/follow?lastId=9999999999999&offset=0
```

测试前会通过验证码登录、关注关系和一篇新笔记准备真实的 Feed 数据，随后按固定持续时间持续读取，并统计吞吐、成功率、P50、P95、P99 和最大延迟。

64 并发、持续 15 秒的本机结果：

| 指标 | 结果 |
|---|---:|
| 总请求数 | 5,448 |
| 成功请求数 | 5,448 |
| 成功率 | 100% |
| 吞吐量 | 360.10 requests/s |
| 平均延迟 | 176.98 ms |
| P50 | 164.67 ms |
| P95 | 299.15 ms |
| P99 | 321.08 ms |
| 最大延迟 | 357.24 ms |

Redisson 分布式限流同时做了独立验证：同一用户持续请求点赞接口，限额为每分钟 120 次；126 次请求中 120 次成功，后 6 次被限流拒绝。

复现：

```powershell
python scripts\load_test.py `
  --concurrency 64 `
  --duration 15 `
  --redis-cli "<path-to-redis-cli.exe>" `
  --output docs/load-test-result-64.json
```

测试环境为单机 Spring Boot、MySQL、Redis 和 Nginx。结果用于同环境回归和优化对比，不代表生产集群容量。完整记录见 [docs/load-test-report.md](docs/load-test-report.md)。

## 数据库

```text
数据库名：lumafeed
初始化脚本：backend/src/main/resources/db/lumafeed.sql
```

本地环境变量：

```dotenv
复制 `.env.example` 中的配置到 IDE 运行环境，禁止把真实密码和令牌提交到仓库。
```

## 启动

1. 启动 MySQL 和 Redis。
2. 导入 `lumafeed.sql`。
3. 运行：

```text
com.lumafeed.LumaFeedApplication
```

4. 启动 Nginx：

```powershell
cd deploy\nginx
.\nginx.exe -p .\
```

## 来源说明

项目的业务思路来源于早期学习用的本地生活课程项目。当前公开目录使用新的
LumaFeed 品牌、`com.lumafeed` 包名、新的数据库名和独立页面结构。
完整来源信息仅保留在 [NOTICE.md](NOTICE.md)。

课程附带的演示图片、商户数据和素材在使用前应确认授权；`.gitignore` 已排除
Nginx 二进制、日志和重复部署页面，避免上传无关运行文件。

## Author

LongQz
