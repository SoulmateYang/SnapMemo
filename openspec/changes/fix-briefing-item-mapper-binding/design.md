## Context

`BriefingItemMapper` 接口中定义了 `findByBriefingId` 方法，但对应的 XML Mapper（`BriefingItemMapper.xml`）只包含 `insert` 的 SQL 映射。MyBatis 在运行时会为每个接口方法查找对应的 SQL 语句，找不到则抛出 `BindingException`。

## Goals / Non-Goals

**Goals:** 在 `BriefingItemMapper.xml` 中新增 `findByBriefingId` 的 `<select>` SQL，按 `briefing_id` 查询 `BriefingItem` 列表。

**Non-Goals:** 无 — 纯粹的缺陷修复。

## Decisions

- SQL 查询：`SELECT * FROM briefing_item WHERE briefing_id = #{briefingId}`
- 结果自动映射到 `BriefingItem` 对象（MyBatis 默认驼峰转换已配置）
