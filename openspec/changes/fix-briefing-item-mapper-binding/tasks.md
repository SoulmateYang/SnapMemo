## 1. 修复 Mapper XML

- [x] 1.1 在 `BriefingItemMapper.xml` 中新增 `<select id="findByBriefingId">` SQL 映射，按 `briefing_id` 查询 `BriefingItem` 列表
- [x] 1.2 重启后端服务，验证 `GET /api/briefings/{id}` 不再抛出 BindingException
