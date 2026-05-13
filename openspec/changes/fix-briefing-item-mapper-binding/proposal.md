## Why

`BriefingItemMapper` 接口新增了 `findByBriefingId` 方法签名，但对应的 XML Mapper 文件中缺少该方法的 SQL 映射，导致 MyBatis 在运行时报错 `Invalid bound statement (not found)`。

## What Changes

- 在 `BriefingItemMapper.xml` 中补充 `findByBriefingId` 的 `<select>` SQL 映射

## Capabilities

### New Capabilities
<!-- None — this is a bug fix for an existing interface method -->

### Modified Capabilities
<!-- None — no spec-level behavior changes -->

## Impact

- 后端: `src/main/resources/mapper/BriefingItemMapper.xml` — 新增 `<select id="findByBriefingId">` SQL
