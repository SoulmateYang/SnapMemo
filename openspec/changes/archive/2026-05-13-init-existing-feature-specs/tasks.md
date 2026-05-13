## 1. briefing 规范

- [x] 1.1 将 `specs/briefing/spec.md` 归档到 `openspec/specs/briefing/spec.md`
- [x] 1.2 验证简报 API 行为与规范一致（列表、今日、详情、手动触发）
- [x] 1.3 验证幂等性逻辑与规范一致（DONE/GENERATING 跳过，PARTIAL/FAILED 允许重试）
- [x] 1.4 验证简报状态流转（DONE / PARTIAL / FAILED）

## 2. news-sources 规范

- [x] 2.1 将 `specs/news-sources/spec.md` 归档到 `openspec/specs/news-sources/spec.md`
- [x] 2.2 验证数据源 CRUD API 行为与规范一致

## 3. ai-summarizer 规范

- [x] 3.1 将 `specs/ai-summarizer/spec.md` 归档到 `openspec/specs/ai-summarizer/spec.md`
- [x] 3.2 验证 LLM 容错降级逻辑（非 JSON、429）
- [x] 3.3 验证 importance_score clamp [1, 5]
- [x] 3.4 验证 HTTP 超时配置已生效

## 4. scheduler 规范

- [x] 4.1 将 `specs/scheduler/spec.md` 归档到 `openspec/specs/scheduler/spec.md`
- [x] 4.2 验证定时任务 08:00 触发配置
- [x] 4.3 验证定时任务与手动触发共享同一生成逻辑
