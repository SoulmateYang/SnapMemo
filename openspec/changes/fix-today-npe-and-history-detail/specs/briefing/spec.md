## MODIFIED Requirements

### Requirement: 查询今日简报
系统 SHALL 提供查询当天简报的接口。当简报不存在时返回 200 + 空状态标识，不抛出异常。

#### Scenario: 今日简报存在
- **WHEN** 客户端请求 `GET /api/briefings/today` 且当天已生成简报
- **THEN** 系统返回 200，响应体格式 `{ "exists": true, "data": { ... } }`

#### Scenario: 今日简报不存在
- **WHEN** 客户端请求 `GET /api/briefings/today` 且当天尚未生成简报
- **THEN** 系统返回 200，响应体格式 `{ "exists": false, "data": null }`，不抛出 NullPointerException
