## Why

两个关联问题：今日简报接口当简报不存在时抛出 NPE；历史简报详情弹窗无法正常展示条目。

**NPE 根因**：`BriefingController.getToday()` 在简报不存在分支使用了 `Map.of("exists", false, "data", null)`，`Map.of()` 不允许 null value。

**历史简报明细根因**：`BriefingService.findById()` 改为调用 `briefingItemMapper.findByBriefingId()` 加载条目，但该方法当时缺少 XML SQL 映射（已在 `fix-briefing-item-mapper-binding` 中修复）。此外，XML 的 `findById` 已通过 LEFT JOIN 加载了 items，服务层重复加载会造成不必要的数据库查询。

## What Changes

- `BriefingController.getToday()`：简报不存在分支改用支持 null value 的 Map 构造方式
- `BriefingService.findById()`：移除冗余的 `findByBriefingId` 调用，XML 的 LEFT JOIN 已加载 items

## Capabilities

### Modified Capabilities
- `briefing`: 今日简报不存在时的响应格式从抛异常修正为正常返回 200；简报详情查询恢复为 XML LEFT JOIN 直接加载 items

## Impact

- 后端: `BriefingController.java` — 修复 Map.of null value
- 后端: `BriefingService.java` — 简化 findById 逻辑
