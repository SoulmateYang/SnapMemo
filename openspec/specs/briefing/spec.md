## ADDED Requirements

### Requirement: 查询简报列表
系统 SHALL 提供分页查询所有简报的接口。

#### Scenario: 分页查询
- **WHEN** 客户端请求 `GET /api/briefings`
- **THEN** 系统返回分页简报列表

### Requirement: 查询今日简报
系统 SHALL 提供查询当天简报的接口。

#### Scenario: 今日简报存在
- **WHEN** 客户端请求 `GET /api/briefings/today` 且当天已生成简报
- **THEN** 系统返回今日简报数据

#### Scenario: 今日简报不存在
- **WHEN** 客户端请求 `GET /api/briefings/today` 且当天尚未生成简报
- **THEN** 系统返回 404

### Requirement: 查询简报详情
系统 SHALL 提供按 ID 查询简报详情的接口。

#### Scenario: 简报存在
- **WHEN** 客户端请求 `GET /api/briefings/{id}` 且该 ID 存在
- **THEN** 系统返回对应简报详情

#### Scenario: 简报不存在
- **WHEN** 客户端请求 `GET /api/briefings/{id}` 且该 ID 不存在
- **THEN** 系统返回 404

### Requirement: 手动触发生成
系统 SHALL 提供手动触发简报生成的接口，并通过静态 Token 保护。

#### Scenario: Token 正确
- **WHEN** 客户端携带正确的 `X-Trigger-Token` header 请求 `POST /api/briefings/generate`
- **THEN** 系统触发简报生成流程并返回 202

#### Scenario: Token 缺失或错误
- **WHEN** 客户端未携带或携带错误的 `X-Trigger-Token` header
- **THEN** 系统返回 401，不触发生成

### Requirement: 幂等性保护
系统 SHALL 在触发生成前检查当天是否已有 `DONE` 或 `GENERATING` 状态的简报。

#### Scenario: 已有 DONE 或 GENERATING 状态
- **WHEN** 触发生成时当天已存在 `DONE` 或 `GENERATING` 状态的简报
- **THEN** 系统跳过生成，不重复调用 LLM

#### Scenario: 状态为 PARTIAL 或 FAILED
- **WHEN** 触发生成时当天简报状态为 `PARTIAL` 或 `FAILED`
- **THEN** 系统允许重新生成

### Requirement: 简报状态流转
系统 SHALL 维护简报的状态：`GENERATING` → `DONE` / `PARTIAL` / `FAILED`。

#### Scenario: 全部数据源成功
- **WHEN** 所有数据源抓取和摘要均成功
- **THEN** 简报状态置为 `DONE`

#### Scenario: 部分数据源失败
- **WHEN** 至少一个数据源失败但有成功项
- **THEN** 简报状态置为 `PARTIAL`

#### Scenario: 全部失败
- **WHEN** 所有数据源均失败
- **THEN** 简报状态置为 `FAILED`

### Requirement: 前端 API 方法完整性
前端 briefing API 模块 SHALL 包含与后端所有已实现端点对应的方法：列表、今日、详情、手动触发。

#### Scenario: 调用列表接口
- **WHEN** 前端调用 `briefingApi.list(page, size)`
- **THEN** 发起 `GET /api/briefings?page=&size=` 请求

#### Scenario: 调用详情接口
- **WHEN** 前端调用 `briefingApi.getById(id)`
- **THEN** 发起 `GET /api/briefings/{id}` 请求
