## 1. 后端修复

- [x] 1.1 新增 `HealthController`，实现 `GET /api/health` 返回 `{"status":"ok"}`
- [x] 1.2 在 `GlobalExceptionHandler#handleGeneral` 中添加 `log.error` 输出异常堆栈

## 2. 前端修复

- [x] 2.1 在 `frontend/src/api/briefing.js` 补全 `list(page, size)` 和 `getById(id)` 方法
- [x] 2.2 验证 `vite.config.js` proxy 配置正确（`/api` → `http://localhost:8090`）
