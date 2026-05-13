## ADDED Requirements

### Requirement: 顶部导航菜单栏
系统 SHALL 在页面顶部提供导航菜单栏，包含「今日简报」「历史简报」「数据源配置」三个菜单项，支持页面间切换。

#### Scenario: 默认进入今日简报
- **WHEN** 用户访问应用根路径 `/`
- **THEN** 页面自动跳转到今日简报页面 `/today`

#### Scenario: 点击历史简报菜单
- **WHEN** 用户点击「历史简报」菜单项
- **THEN** 页面导航至 `/history` 并展示历史简报占位页面

#### Scenario: 点击数据源配置菜单
- **WHEN** 用户点击「数据源配置」菜单项
- **THEN** 页面导航至 `/sources` 并展示数据源配置占位页面

#### Scenario: 当前页面高亮
- **WHEN** 用户位于某个菜单项对应的页面
- **THEN** 该菜单项高亮显示，标识当前所在位置

#### Scenario: 直接 URL 访问
- **WHEN** 用户直接在浏览器输入 `/history` 或 `/sources` 路径
- **THEN** 页面正常展示对应内容，菜单栏高亮对应项
