## Context

后端运行在 `localhost:8090`，前端 Vite 开发服务器通过 proxy 将 `/api` 转发到后端。两个问题：
1. 访问后端直接地址（非代理）出现 500，根因是数据库连接失败或其他启动异常，但错误信息被吞掉无法排查
2. 前端 404 是因为 `briefing.js` 缺少列表/详情方法，或前端绕过 Vite 代理直接访问后端

## Goals / Non-Goals

**Goals:**
- 提供 `/api/health` 端点快速验证后端是否正常启动
- 让 500 错误有日志可查
- 前端 API 方法与后端实际端点对齐

**Non-Goals:**
- 不修改数据库配置（数据库连接问题由用户自行保证）
- 不引入 Spring Actuator 等重量级依赖

## Decisions

**用轻量 Controller 实现 `/api/health`**：返回 `{"status":"ok"}` 即可，无需引入 Actuator。

**在 GlobalExceptionHandler 中加 log.error**：当前 500 错误被静默吞掉，加日志后可在控制台看到堆栈，快速定位根因。

**前端用相对路径 `/api`**：Vite proxy 已配置正确，前端 axios 不设 baseURL，保持相对路径即可通过代理转发。

## Risks / Trade-offs

- [health 端点无鉴权] → 仅暴露 `{"status":"ok"}`，不含敏感信息，风险可接受
