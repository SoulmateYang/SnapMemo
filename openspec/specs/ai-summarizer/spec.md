## ADDED Requirements

### Requirement: 调用 LLM 生成摘要
系统 SHALL 对每条新闻调用 LLM API 生成 `ai_summary` 和 `importance_score`。

#### Scenario: LLM 返回合法 JSON
- **WHEN** LLM 返回包含 `ai_summary` 和 `importance` 字段的 JSON
- **THEN** 系统使用返回值填充 BriefingItem

#### Scenario: LLM 返回非 JSON 格式
- **WHEN** LLM 返回纯文本或 markdown 代码块等非 JSON 内容
- **THEN** 系统降级：`ai_summary` = 原始标题，`importance_score` = 3，不抛异常

### Requirement: importance_score 范围校验
系统 SHALL 将 LLM 返回的 importance 值 clamp 到 [1, 5]。

#### Scenario: 值超出范围
- **WHEN** LLM 返回的 importance 值小于 1 或大于 5
- **THEN** 系统将其 clamp 到边界值，不抛异常，不阻塞流程

### Requirement: 限流与请求间隔
系统 SHALL 在每次 LLM 调用之间插入 `ai.request-delay-ms` 毫秒的延迟。

#### Scenario: 正常调用间隔
- **WHEN** 连续调用 LLM API
- **THEN** 每次调用之间等待配置的延迟时间

### Requirement: HTTP 429 降级处理
系统 SHALL 在收到 LLM API 的 429 响应时进行降级处理。

#### Scenario: 收到 429
- **WHEN** LLM API 返回 HTTP 429
- **THEN** 系统降级：`ai_summary` = 原始标题，`importance_score` = 3，记录 WARN 日志

### Requirement: HTTP 超时配置
系统 SHALL 为 LLM HTTP 客户端配置显式连接超时和读取超时。

#### Scenario: 连接超时
- **WHEN** LLM API 连接时间超过 `ai.connect-timeout-ms`
- **THEN** 系统抛出超时异常，不永久阻塞 Scheduler 线程

#### Scenario: 读取超时
- **WHEN** LLM API 响应时间超过 `ai.read-timeout-ms`
- **THEN** 系统抛出超时异常，不永久阻塞 Scheduler 线程
