## Why

项目已完成初步开发，但缺乏正式的功能规范文档。将现有功能以 spec 形式固化，为后续迭代、重构和新功能开发提供可追溯的需求基线。

## What Changes

- 新增 `briefing`（简报生成与查询）功能规范
- 新增 `news-sources`（数据源管理）功能规范
- 新增 `ai-summarizer`（AI 摘要）功能规范
- 新增 `scheduler`（定时任务）功能规范

## Capabilities

### New Capabilities

- `briefing`: 简报的生成、查询、状态管理及手动触发，包含幂等性、容错和 Token 保护约束
- `news-sources`: 数据源的 CRUD 管理，支持 RSS 和 HackerNews API
- `ai-summarizer`: 调用 LLM API 生成摘要，含限流、容错、importance_score 校验
- `scheduler`: 每日 08:00 定时触发简报生成，与手动触发共享同一生成逻辑

### Modified Capabilities

（无——首次初始化，无已有规范需变更）

## Impact

- 仅新增 `openspec/specs/` 下的规范文件，不修改任何业务代码
- 为后续 `/opsx:apply` 实现任务提供规范依据
