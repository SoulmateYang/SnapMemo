## 1. 修复今日简报 NPE

- [x] 1.1 修改 `BriefingController.getToday()`：将 `Map.of("exists", false, "data", null)` 改为 HashMap 构造，避免 Map.of 禁止 null value 导致 NPE

## 2. 修复历史简报详情

- [x] 2.1 修改 `BriefingService.findById()`：撤回到直接委托 `briefingMapper.findById(id)`，移除冗余的 `findByBriefingId` 调用（XML LEFT JOIN 已加载 items）

## 3. 验证

- [x] 3.1 重启后端，验证 `GET /api/briefings/today`（无简报时）返回 200 不抛异常
- [x] 3.2 验证 `GET /api/briefings/{id}` 返回简报 + 条目列表，前端弹窗正常展示
