## Context

项目已完成初步开发，后端采用 Spring Boot 3.x + MyBatis + MySQL，前端采用 Vue 3 + Element Plus。本次变更仅新增 openspec/specs/ 下的规范文件，不涉及任何业务代码修改。

## Goals / Non-Goals

**Goals:**
- 将 project.md 中已有的工程约束和功能描述转化为可追溯的 spec 规范
- 覆盖四个核心能力：briefing、news-sources、ai-summarizer、scheduler

**Non-Goals:**
- 不修改任何业务代码
- 不引入新功能或新约束
- 不覆盖前端 UI 规范（仅后端 API 行为）

## Decisions

**按能力边界拆分 spec 文件**：每个能力独立一个 `specs/<name>/spec.md`，便于后续单独迭代。替代方案（单一大文件）会导致职责混淆，变更追踪困难。

**直接从 project.md 提取约束**：project.md 已包含经过 CEO/Eng Review 的约束，直接转化为 SHALL 语句，避免重复设计。

## Risks / Trade-offs

- [规范与代码漂移] → 后续代码变更时需同步更新 spec；通过 `/opsx:apply` 流程约束
- [覆盖不完整] → 首次初始化以 project.md 为唯一来源，后续可通过新变更补充
