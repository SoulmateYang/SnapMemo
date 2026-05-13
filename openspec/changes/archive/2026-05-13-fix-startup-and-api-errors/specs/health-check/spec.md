## ADDED Requirements

### Requirement: 健康检查端点
系统 SHALL 提供 `GET /api/health` 端点，无需鉴权，返回服务状态。

#### Scenario: 服务正常
- **WHEN** 客户端请求 `GET /api/health`
- **THEN** 系统返回 200 和 `{"status":"ok"}`
