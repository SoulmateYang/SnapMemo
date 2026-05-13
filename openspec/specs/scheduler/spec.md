## ADDED Requirements

### Requirement: 每日定时触发
系统 SHALL 在每天 08:00 自动触发简报生成流程。

#### Scenario: 定时触发成功
- **WHEN** 系统时间到达每日 08:00
- **THEN** BriefingScheduler 调用 BriefingService 执行生成流程

### Requirement: 与手动触发共享生成逻辑
系统 SHALL 使定时触发与手动触发（`POST /api/briefings/generate`）共享同一 BriefingService 生成逻辑。

#### Scenario: 定时触发与手动触发行为一致
- **WHEN** 定时任务触发生成
- **THEN** 执行与手动触发完全相同的幂等性检查、抓取、摘要、持久化流程

### Requirement: 定时任务不阻塞
系统 SHALL 保证定时任务线程不因 LLM 调用超时而永久阻塞。

#### Scenario: LLM 调用超时
- **WHEN** LLM API 在 `ai.read-timeout-ms` 内未响应
- **THEN** 超时异常被捕获，定时任务线程正常退出，下次调度不受影响
