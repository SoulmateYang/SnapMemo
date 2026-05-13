## Why

后端启动后访问接口返回 `Internal server error`，前端调用后端接口提示 404，导致应用无法正常使用。需要定位并修复这两个问题。

## What Changes

- 后端：增加启动健康检查端点 `GET /api/health`，便于验证服务是否正常启动
- 后端：`GlobalExceptionHandler` 补充日志输出，便于排查 500 错误根因
- 前端：`briefing.js` 补全缺失的列表和详情 API 方法
- 前端：增加 `.env.development` 明确配置 API baseURL，避免地址混淆

## Capabilities

### New Capabilities

- `health-check`: 后端健康检查端点，返回服务状态

### Modified Capabilities

- `briefing`: 前端 API 补全列表和详情方法

## Impact

- 新增 `GET /api/health` 端点
- 修改 `frontend/src/api/briefing.js`
- 新增 `frontend/.env.development`
- 修改 `GlobalExceptionHandler` 增加日志
