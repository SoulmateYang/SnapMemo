# 项目目的
这是一个AI简报项目，每天早上八点会调用大模型生产当天的简报信息
## 功能需求
- 提供定时任务调用大模型获取当天AI大事件简报信息

## 核心 API 设计
```
GET  /api/briefings              # 简报列表（分页）
GET  /api/briefings/today        # 今日简报
GET  /api/briefings/{id}         # 简报详情
POST /api/briefings/generate     # 手动触发生成

GET  /api/sources                # 数据源列表
POST /api/sources                # 新增数据源
PUT  /api/sources/{id}           # 更新数据源
DELETE /api/sources/{id}         # 删除数据源
```

## 技术栈
### 后端
- Spring Boot 3.x
- MyBatis + MySQL（生产）
- Spring Scheduler（定时任务）
- Rome（RSS 解析库）
- OkHttp / RestTemplate（HTTP 请求）
- OpenAI Java SDK 或直接调用 HTTP API
- CORS 配置：在 `WebMvcConfigurer` 中允许 `http://localhost:5173`（前端开发地址）
### 前端
- Vue 3 + Vite
- Element Plus（Element UI for Vue 3）
- Vue Router（路由：今日简报 / 历史 / 配置）
- Axios（HTTP 请求）

## 服务层职责划分（CEO Review 更新）

```
BriefingScheduler      → 触发入口（Spring Scheduler）
BriefingService        → 编排：调用 Fetcher + Summarizer + 持久化
NewsFetcherService     → 抓取：RSS 解析 + HackerNews API
AISummarizerService    → AI 摘要：调用 LLM API，处理格式容错
BriefingRepository     → 持久化：JPA
NewsSourceRepository   → 数据源配置持久化
```

## 关键实现约束（Eng Review 更新）

### HTTP 超时配置
AISummarizerService 的 HTTP 客户端必须配置显式超时，否则 SocketTimeoutException 不会触发，Scheduler 线程将永久阻塞：
```yaml
ai:
  connect-timeout-ms: 5000   # 5秒连接超时
  read-timeout-ms: 30000     # 30秒读取超时
  request-delay-ms: 200      # LLM 调用间隔，防止 429
```

### LLM 限流处理
AISummarizerService 每次调用之间插入 `ai.request-delay-ms` 延迟。HTTP 429 响应降级处理：`ai_summary` = 原始标题，`importance_score` = 3，记录 WARN 日志。

### importance_score 范围校验
LLM 返回的 importance 值必须 clamp 到 [1, 5]：`Math.max(1, Math.min(5, importance))`。超出范围不抛异常，不阻塞流程。

### 触发端点保护
`POST /api/briefings/generate` 需要静态 Token 校验，防止意外暴露后产生 LLM 费用：
```yaml
briefing:
  trigger-token: ${BRIEFING_TRIGGER_TOKEN}
```
前端 POST 时携带 `X-Trigger-Token` header。无 Token 或 Token 错误返回 401。

### 幂等性（更新）
`POST /api/briefings/generate` 和 Scheduler 触发前，先检查当天是否已有 `DONE` 或 `GENERATING` 状态的简报，有则跳过。`PARTIAL` 和 `FAILED` 状态允许重新生成。
数据库层面增加唯一约束防止并发竞争：
```sql
ALTER TABLE briefing ADD CONSTRAINT uk_briefing_date UNIQUE (date);
```

### LLM 输出容错
LLM 返回非 JSON 格式时（纯文本、markdown 代码块等），降级处理：
- `ai_summary` = 原始标题
- `importance_score` = 3（默认中等）
- 不抛异常，不阻塞整体流程

### 服务层方法拆分（BriefingService）
`BriefingService#generate()` 必须拆分为以下私有方法，确保每个方法 ≤80 行，圈复杂度 ≤10：
```
generate()          → 状态检查 + 编排 + 状态更新（GENERATING → DONE/PARTIAL/FAILED）
processSource()     → 单个数据源的抓取 + 摘要 + 持久化
processItem()       → 单条新闻的 AI 摘要调用 + BriefingItem 构建
```

### 全局异常处理
新增 `@RestControllerAdvice` 类（`GlobalExceptionHandler`），统一处理：
- `DataAccessException` → 500
- `IllegalArgumentException` → 400
- 其他未捕获异常 → 500
不在各 Controller 中重复 try/catch。
