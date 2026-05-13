<template>
  <div class="history-page">
    <h1>历史简报</h1>

    <el-skeleton v-if="loading" :rows="6" animated />

    <template v-else-if="!error">
      <el-table
        v-if="items.length > 0"
        :data="items"
        stripe
        highlight-current-row
        @row-click="openDetail"
      >
        <el-table-column prop="date" label="日期" width="140" />
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="summary" label="摘要" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="primary" link @click.stop="openDetail(row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-else description="暂无历史简报" />

      <div v-if="items.length > 0" class="pagination-wrapper">
        <el-pagination
          v-model:current-page="page"
          :page-size="size"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="fetchList"
        />
      </div>
    </template>

    <el-result
      v-if="error"
      icon="error"
      title="加载失败"
      :sub-title="error"
    />

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      destroy-on-close
    >
      <el-skeleton v-if="detailLoading" :rows="4" animated />
      <template v-else-if="detailItems.length > 0">
        <p class="detail-summary">{{ detailBriefing?.summary }}</p>
        <div class="items">
          <el-card v-for="item in sortedDetailItems" :key="item.id" class="news-card">
            <div class="card-header">
              <a :href="item.url" target="_blank" rel="noopener noreferrer" class="news-title">
                {{ item.title }}
              </a>
              <div class="tags">
                <el-tag type="info" size="small">{{ item.source }}</el-tag>
                <el-tag :type="importanceType(item.importanceScore)" size="small">
                  重要性 {{ item.importanceScore }}
                </el-tag>
              </div>
            </div>
            <p v-if="item.aiSummary && item.aiSummary !== item.title" class="ai-summary">
              {{ item.aiSummary }}
            </p>
          </el-card>
        </div>
      </template>
      <el-empty v-else description="该简报暂无条目" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { briefingApi } from '../api/briefing'

const loading = ref(true)
const error = ref(null)
const items = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)

const dialogVisible = ref(false)
const dialogTitle = ref('')
const detailLoading = ref(false)
const detailBriefing = ref(null)
const detailItems = ref([])

const sortedDetailItems = computed(() => {
  if (!detailItems.value.length) return []
  return [...detailItems.value].sort((a, b) => {
    if (b.importanceScore !== a.importanceScore) return b.importanceScore - a.importanceScore
    return new Date(a.createdAt) - new Date(b.createdAt)
  })
})

function statusType(status) {
  const map = { DONE: 'success', PARTIAL: 'warning', FAILED: 'danger', GENERATING: 'info' }
  return map[status] || 'info'
}

function statusLabel(status) {
  const map = { DONE: '已完成', PARTIAL: '部分完成', FAILED: '失败', GENERATING: '生成中' }
  return map[status] || status
}

function importanceType(score) {
  if (score >= 5) return 'danger'
  if (score >= 4) return 'warning'
  if (score >= 3) return 'primary'
  return 'info'
}

async function fetchList() {
  try {
    const res = await briefingApi.list(page.value, size.value)
    items.value = res.items
    total.value = res.total
  } catch (e) {
    error.value = e.message || '网络错误'
  } finally {
    loading.value = false
  }
}

async function openDetail(row) {
  dialogVisible.value = true
  dialogTitle.value = `${row.date} · ${row.title}`
  detailLoading.value = true
  detailBriefing.value = row
  detailItems.value = []

  try {
    const res = await briefingApi.getById(row.id)
    detailBriefing.value = res
    detailItems.value = res.items || []
  } catch (e) {
    detailItems.value = []
  } finally {
    detailLoading.value = false
  }
}

fetchList()
</script>

<style scoped>
.history-page { max-width: 1000px; margin: 0 auto; padding: 24px; }
.pagination-wrapper { display: flex; justify-content: center; margin-top: 20px; }
.detail-summary { color: #666; margin-bottom: 16px; }
.items { display: flex; flex-direction: column; gap: 12px; max-height: 60vh; overflow-y: auto; }
.news-card { width: 100%; }
.card-header { display: flex; justify-content: space-between; align-items: flex-start; gap: 12px; }
.news-title { font-weight: 500; color: #303133; text-decoration: none; flex: 1; }
.news-title:hover { color: #409eff; }
.tags { display: flex; gap: 6px; flex-shrink: 0; }
.ai-summary { margin-top: 8px; color: #606266; font-size: 14px; line-height: 1.6; }
</style>
