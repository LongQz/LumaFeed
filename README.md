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
