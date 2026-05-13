## Context

当前前端为单页面应用（`App.vue` 直嵌 `TodayBriefing` 组件），没有路由体系，也没有导航菜单。后端 `GET /api/briefings/today` 在当日简报不存在时返回 `404`，前端将其作为异常捕获并展示错误状态，但实际上"暂无简报"是正常的业务状态，不应以错误形式呈现。

### 约束
- 技术栈：Vue 3 + Element Plus + Vite（前端），Spring Boot 3 + MyBatis（后端）
- 前端目前无路由框架，需引入 `vue-router`
- 后端 spec `briefing/spec.md` 中"今日简报不存在返回 404"的行为需要变更

## Goals / Non-Goals

**Goals:**
- 新增顶部导航菜单栏，支持「今日简报」「历史简报」「数据源配置」三个入口
- 今日简报未生成时，后端返回有意义的正常响应（非 404），前端展示友好空状态
- 新增历史简报列表页、数据源配置页（占位页面，路由可用即可）
- 保持现有功能不受影响（简报展示、生成触发等）

**Non-Goals:**
- 历史简报页面的完整列表/搜索/筛选功能（本次仅占位）
- 数据源配置页面的 CRUD 功能（本次仅占位）
- 菜单权限/登录鉴权
- 响应式移动端适配

## Decisions

### 1. 导航方案：Element Plus `el-menu` + Vue Router
- **选型理由**: 项目已依赖 Element Plus，`el-menu` 组件开箱即用；Vue Router 是 Vue 3 官方路由方案
- **替代方案**: 手写导航栏 HTML+CSS — 增加维护成本，不用
- **替代方案**: 不使用路由、仅用 `v-if` 切页面 — 无法支持 URL 直达，不利于后续扩展

### 2. 后端空简报响应：返回 200 + `{ "exists": false }` 而非 404
- **选型理由**: HTTP 404 表示资源不存在（错误语义），但"今日简报尚未生成"是正常的业务空状态。200 + 业务字段区分更符合 REST 语义
- **API 变更**: `GET /api/briefings/today`
  - 存在简报 → 200 + `{ "exists": true, "data": { ... } }`
  - 不存在简报 → 200 + `{ "exists": false, "data": null }`
- **Breaking Change**: 现有前端依赖 404 判断简报不存在（通过 catch 捕获），需要同步更新

### 3. 前端错误处理重构：业务空状态 vs 网络异常分离
- 当前实现：`try/catch` 中所有非 2xx 都走 `error` 分支
- 改进后：API 始终返回 200，通过 `res.data.exists` 判断；网络异常仍走 catch
- 三个 UI 分支：加载中 → 有简报展示 / 无简报空状态 / 网络异常错误

## Risks / Trade-offs

- **[Breaking API]** 后端 `/api/briefings/today` 响应格式变更 → 前端同步更新，无旧版本客户端需兼容
- **[新依赖]** 引入 `vue-router` → 约 30KB gzip，对首屏加载影响可忽略。Vite 按需打包，增加量极小
- **[路由配置复杂度]** 随着页面增多路由配置膨胀 → 当前仅 3 条路由，无需过度设计（如懒加载、路由守卫）
