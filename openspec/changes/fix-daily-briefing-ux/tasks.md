## 1. 后端 — 今日简报接口响应格式修复

- [x] 1.1 修改 `BriefingController.getToday()`：当日简报不存在时返回 200 + `{"exists":false,"data":null}` 而非 404
- [x] 1.2 修改 `BriefingService.getTodayBriefing()` 返回类型，使 Controller 可直接获取 Optional 判断存在性
- [x] 1.3 运行后端测试 `mvn test` 确认已有测试通过，必要时更新 `BriefingControllerTest` 中 404 断言的用例

## 2. 前端基础设施 — 路由与导航

- [x] 2.1 安装 `vue-router` 依赖：`npm install vue-router`
- [x] 2.2 创建 `src/router/index.js`，配置三条路由：`/today`（今日简报）、`/history`（历史简报）、`/sources`（数据源配置），根路径 `/` 重定向至 `/today`
- [x] 2.3 在 `main.js` 中注册 router 实例

## 3. 前端 — 导航菜单栏

- [x] 3.1 重构 `App.vue`：使用 `el-menu` 组件替换原有简单 header，菜单项绑定路由路径
- [x] 3.2 添加 `<router-view />` 替代原有的 `<TodayBriefing />` 直嵌，实现视图切换

## 4. 前端 — 占位页面

- [x] 4.1 创建 `src/views/History.vue` 历史简报占位页面（el-empty 提示"历史简报功能开发中"）
- [x] 4.2 创建 `src/views/Sources.vue` 数据源配置占位页面（el-empty 提示"数据源配置功能开发中"）

## 5. 前端 — 今日简报页面适配新响应格式

- [x] 5.1 更新 `TodayBriefing.vue`：根据响应 `exists` 字段区分「未生成」与「加载失败」，未生成时展示 `el-empty`，加载失败时展示 `el-result error`
- [x] 5.2 更新 `api/briefing.js`：`getToday()` 不再将 2xx 响应视为错误，返回完整 `response.data`
