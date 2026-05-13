# AI Briefing · AI 简报

> 每天早上 8:00，自动抓取全球 AI 资讯，由大模型生成摘要与重要性评分，一站式掌握 AI 大事件。
> Every morning at 8:00 AM, automatically fetch global AI news, summarize and score importance via LLM — your one-stop AI news briefing.

---

## 功能特性 · Features

- **定时生成**：每天 8:00 自动触发，整合多源 AI 新闻
- **多源抓取**：支持 RSS 订阅源 + HackerNews API，可动态管理数据源
- **AI 摘要**：调用通义千问 (Qwen) 大模型，对每条新闻生成中文摘要和重要性评分 (1–5)
- **容错与幂等**：LLM 输出异常时降级处理不阻塞流程；同一天不重复生成
- **手动触发**：提供受 Token 保护的 `POST /api/briefings/generate` 端点
- **状态追踪**：`GENERATING → DONE / PARTIAL / FAILED`，前端实时反馈
- **历史回溯**：分页查看历史简报及详情

---

## 技术栈 · Tech Stack

| 层 Layer | 技术 Technology |
|-----------|-----------------|
| **后端 Backend** | Java 17 · Spring Boot 3.2.5 · MyBatis 3.0 · Flyway · MySQL |
| **前端 Frontend** | Vue 3.4 · Vite 5 · Element Plus 2.6 · Vue Router 4 · Axios |
| **AI / LLM** | 阿里云 DashScope (Qwen) |
| **调度 Scheduler** | Spring `@Scheduled` |
| **抓取 Fetcher** | Rome (RSS) · OkHttp 4.12 |
| **数据库 Database** | MySQL 8 (生产) / H2 (可切换) |

---

## 项目结构 · Project Structure

```
ai-briefing/
├── backend/                         # Spring Boot 后端
│   ├── pom.xml
│   └── src/main/java/com/aibrief/
│       ├── AiBriefingApplication.java   # 应用入口
│       ├── config/
│       │   ├── GlobalExceptionHandler.java  # 全局异常处理
│       │   └── WebConfig.java              # CORS 配置
│       ├── controller/
│       │   ├── BriefingController.java     # 简报 API
│       │   ├── HealthController.java       # 健康检查
│       │   └── NewsSourceController.java   # 数据源管理 API
│       ├── mapper/                         # MyBatis Mapper 接口
│       ├── model/                          # 实体类 (Briefing, BriefingItem, NewsSource)
│       ├── scheduler/
│       │   └── BriefingScheduler.java      # 定时任务入口 (8:00 AM)
│       ├── service/
│       │   ├── BriefingService.java        # 编排服务
│       │   ├── NewsFetcherService.java     # RSS + HN API 抓取
│       │   ├── AISummarizerService.java    # LLM 摘要调用
│       │   └── NewsSourceService.java      # 数据源管理
│       └── resources/
│           ├── application.yml             # 应用配置
│           ├── db/migration/V1__init.sql   # 数据库初始化
│           └── mapper/                     # MyBatis XML
├── frontend/                        # Vue 3 前端
│   ├── package.json
│   ├── vite.config.js                     # Vite 配置 + API 代理
│   └── src/
│       ├── main.js                        # 入口
│       ├── App.vue                        # 根组件
│       ├── api/briefing.js                # 简报 API 封装
│       ├── router/index.js                # 路由 (今日 / 历史 / 数据源)
│       └── views/
│           ├── TodayBriefing.vue          # 今日简报页
│           ├── History.vue                # 历史简报页
│           └── Sources.vue                # 数据源管理页 (开发中)
└── openspec/                        # 规格与设计文档
    ├── project.md                         # 项目定义 & 架构决策
    ├── specs/                             # 能力规格
    └── changes/                           # 变更记录
```

---

## 快速开始 · Quick Start

### 前置条件 · Prerequisites

- **JDK 17**+
- **Maven 3.8**+
- **Node.js 18**+
- **MySQL 8** (默认端口 3306)

### 1. 数据库 · Database

创建数据库并确保 MySQL 运行中：

```sql
CREATE DATABASE aibrief DEFAULT CHARACTER SET utf8mb4;
```

应用启动时 Flyway 自动执行迁移脚本 `V1__init.sql`，创建表结构并插入初始数据源。

### 2. 后端 · Backend

```bash
cd backend

# 配置环境变量（按需修改）
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=aibrief
export DB_USERNAME=root
export DB_PASSWORD=your_password
export AI_API_KEY=your_dashscope_api_key
export BRIEFING_TRIGGER_TOKEN=your_secret_token

# 启动
./mvnw spring-boot:run
```

后端运行在 **http://localhost:8090**

### 3. 前端 · Frontend

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端运行在 **http://localhost:5173**，API 请求通过 Vite 代理转发到后端 8090 端口。

---

## API 概览 · API Overview

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/briefings?page=&size=` | 简报列表（分页）· Paginated list |
| `GET` | `/api/briefings/today` | 今日简报 · Today's briefing |
| `GET` | `/api/briefings/{id}` | 简报详情 · Briefing detail |
| `POST` | `/api/briefings/generate` | 手动触发生成（需 Token）· Manual trigger |
| `GET` | `/api/sources` | 数据源列表 · Source list |
| `POST` | `/api/sources` | 新增数据源 · Add source |
| `PUT` | `/api/sources/{id}` | 更新数据源 · Update source |
| `DELETE` | `/api/sources/{id}` | 删除数据源 · Delete source |
| `GET` | `/api/health` | 健康检查 · Health check |

> `POST /api/briefings/generate` 需携带 `X-Trigger-Token` header，默认值 `dev-token`。

---

## 架构设计 · Architecture

```
┌─────────────────────────────────────────────────────┐
│                    Frontend (Vue 3)                  │
│   TodayBriefing  │  History  │  Sources (WIP)        │
└────────────────────────┬────────────────────────────┘
                         │ HTTP (Axios)
┌────────────────────────▼────────────────────────────┐
│                 Controller Layer                     │
│  BriefingController  │  NewsSourceController          │
│  HealthController                                   │
└────────────────────────┬────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────┐
│                  Service Layer                       │
│                                                     │
│  BriefingScheduler (8:00 AM trigger)                 │
│       │                                             │
│       ▼                                             │
│  BriefingService ──── 编排 Orchestrator              │
│       │           │                                  │
│       ▼           ▼                                  │
│  NewsFetcher    AISummarizer                         │
│  (RSS + HN)     (LLM Qwen)                          │
└────────────────────────┬────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────┐
│              Persistence Layer                       │
│  MyBatis Mapper → MySQL (briefing / briefing_item    │
│                           / news_source)             │
└─────────────────────────────────────────────────────┘
```

### 关键设计决策 · Key Design Decisions

**1. 幂等性保护 · Idempotency**
生成前先检查当天是否已有 `DONE` 或 `GENERATING` 状态的简报，避免重复调用 LLM 产生费用。数据库层面 `briefing.date` 设有唯一约束 `uk_briefing_date`，防止并发竞争。

**2. LLM 输出容错 · LLM Fault Tolerance**
大模型可能返回非 JSON 格式（纯文本、markdown 代码块等）。当解析失败时，`ai_summary` 回退为原始标题，`importance_score` 设为默认值 3，不阻塞整体流程。

**3. importance_score 范围校验**
LLM 返回的分数 clamp 到 [1, 5]：`Math.max(1, Math.min(5, score))`。超出范围不抛异常。

**4. HTTP 超时**
AISummarizerService 显式配置 `connectTimeout` (5s) 和 `readTimeout` (30s)，防止 LLM API 无响应时调度器线程永久阻塞。

**5. LLM 限流**
每次 LLM 调用之间插入 200ms 延迟 (`ai.request-delay-ms`)，防止触发 API 频率限制 (HTTP 429)。

**6. 触发保护 · Trigger Protection**
`POST /api/briefings/generate` 受静态 Token 保护（`X-Trigger-Token` header），防止意外暴露后产生费用。

**7. 状态流转 · Status Lifecycle**

```
GENERATING ──┬── 全部成功 ──────▶ DONE
             ├── 部分成功 ──────▶ PARTIAL
             └── 全部失败 ──────▶ FAILED

PARTIAL / FAILED ── 可重新触发生成
DONE / GENERATING ── 跳过（幂等保护）
```

---

## 配置说明 · Configuration

关键配置项见 `backend/src/main/resources/application.yml`：

| 配置项 | 环境变量 | 默认值 | 说明 |
|--------|----------|--------|------|
| `spring.datasource.url` | `DB_HOST` / `DB_PORT` / `DB_NAME` | `localhost:3306/aibrief` | MySQL 连接 |
| `spring.datasource.username` | `DB_USERNAME` | `root` | 数据库用户 |
| `spring.datasource.password` | `DB_PASSWORD` | `123456` | 数据库密码 |
| `ai.api-key` | `AI_API_KEY` | — | DashScope API Key |
| `ai.api-url` | `AI_API_URL` | `dashscope.aliyuncs.com` | LLM API 地址 |
| `ai.model` | `AI_MODEL` | `qvq-max-2025-03-25` | 模型名称 |
| `ai.connect-timeout-ms` | — | `5000` | LLM 连接超时 (ms) |
| `ai.read-timeout-ms` | — | `30000` | LLM 读取超时 (ms) |
| `ai.request-delay-ms` | — | `200` | LLM 调用间隔 (ms) |
| `briefing.trigger-token` | `BRIEFING_TRIGGER_TOKEN` | `dev-token` | 手动触发鉴权 Token |
| `server.port` | — | `8090` | 后端端口 |

> ⚠️ **生产环境务必修改**：`DB_PASSWORD`、`AI_API_KEY`、`BRIEFING_TRIGGER_TOKEN` 不应使用默认值。

---

## 开发指南 · Development

### 数据源格式 · News Source Format

`news_source` 表支持两种类型：
- **RSS**: `type = 'RSS'`，`url` 为 RSS feed 地址（如 MIT Technology Review）
- **API**: `type = 'API'`，`url` 为 API base URL（如 HackerNews Firebase API）

### 简报状态 · Briefing Status

| Status | 含义 Meaning |
|--------|-------------|
| `GENERATING` | 生成中 · In progress |
| `DONE` | 全部成功 · All succeeded |
| `PARTIAL` | 部分成功 · Partial success |
| `FAILED` | 全部失败 · All failed |
