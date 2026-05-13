## MODIFIED Requirements

### Requirement: 前端 API 方法完整性
前端 briefing API 模块 SHALL 包含与后端所有已实现端点对应的方法：列表、今日、详情、手动触发。

#### Scenario: 调用列表接口
- **WHEN** 前端调用 `briefingApi.list(page, size)`
- **THEN** 发起 `GET /api/briefings?page=&size=` 请求

#### Scenario: 调用详情接口
- **WHEN** 前端调用 `briefingApi.getById(id)`
- **THEN** 发起 `GET /api/briefings/{id}` 请求
