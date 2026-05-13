## Context

`BriefingController.getToday()` 在简报不存在时构造 `Map.of("data", null)`，JDK 9+ `Map.of()` 禁止 null value，直接抛 NPE。`BriefingService.findById()` 改为调用 `findByBriefingId` 加载条目，但 XML 的 `findById` 已通过 `briefingWithItemsMap`（LEFT JOIN + collection）加载了 items，服务层调用是冗余的。

## Decisions

- **Controller 修复**：简报不存在分支改用 `new HashMap<>(2)` 构造 Map，允许 null value。简报存在分支保持不变（`Map.of` 无 null 值，安全）
- **Service 修复**：`findById` 撤回为直接委托 `briefingMapper.findById(id)`，不额外调用 `findByBriefingId`
