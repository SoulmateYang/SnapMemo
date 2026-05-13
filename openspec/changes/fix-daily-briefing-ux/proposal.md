## Why

当今日简报未生成时，首页显示 404 错误状态，用户体验差；同时页面缺少导航菜单栏，无法在简报页面与其他功能页面之间切换。

## What Changes

- 前端新增顶部导航菜单栏，支持页面间切换（今日简报 / 历史简报 / 数据源配置）
- 后端 `GET /api/briefings/today` 当简报不存在时返回 200 + "无简报"响应体，而非 404 **BREAKING**
- 前端新增"未生成"状态的专门 UI 展示，替代之前的错误提示
- 前端新增历史简报列表页面和数据源配置页面（占位页面，后续迭代完善）

## Capabilities

### New Capabilities
- `navigation-menu`: 顶部导航菜单栏，提供页面间路由切换能力

### Modified Capabilities
- `briefing`: 今日简报不存在时，后端返回 200（含空状态标识）而非 404，前端展示"尚未生成"友好提示

## Impact

- 后端: `BriefingController.getToday()` — 修改返回值逻辑
- 后端: 现有 spec `briefing/spec.md` — 场景「今日简报不存在」的预期行为从 404 变更为 200
- 前端: `App.vue` — 新增导航菜单栏 + 路由配置
- 前端: `TodayBriefing.vue` — 区分「未生成」和「加载失败」两种状态
- 前端: `briefing.js` API 模块 — GET /api/briefings/today 的响应处理不再将空状态视为错误
- 新增依赖: `vue-router`（前端路由）
