<template>
  <div class="briefing-page">
    <h1>AI 简报</h1>

    <el-skeleton v-if="loading" :rows="5" animated />

    <template v-else-if="briefing">
      <el-alert
        v-if="briefing.status === 'GENERATING'"
        type="info"
        title="正在生成今日简报，通常需要 1-3 分钟..."
        :closable="false"
        style="margin-bottom: 16px"
      />
      <el-alert
        v-if="briefing.status === 'PARTIAL'"
        type="warning"
        title="部分数据源获取失败，简报可能不完整"
        :closable="false"
        style="margin-bottom: 16px"
      />
      <el-result
        v-if="briefing.status === 'FAILED'"
        icon="error"
        title="今日简报生成失败"
        sub-title="请明天再试"
      />

      <template v-if="briefing.status !== 'FAILED'">
        <p class="summary">{{ briefing.title }} · {{ briefing.summary }}</p>
        <el-skeleton v-if="briefing.status === 'GENERATING'" :rows="5" animated />
        <div v-else class="items">
          <el-card
            v-for="item in sortedItems"
            :key="item.id"
            class="news-card"
          >
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
    </template>

    <el-empty
      v-else-if="!error"
      description="今天还没有简报，将在每天 8:00 自动生成"
    />

    <el-result
      v-if="error"
      icon="error"
      title="加载失败"
      :sub-title="error"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { briefingApi } from '../api/briefing'

const briefing = ref(null)
const loading = ref(true)
const error = ref(null)

const sortedItems = computed(() => {
  if (!briefing.value?.items) return []
  return [...briefing.value.items].sort((a, b) => {
    if (b.importanceScore !== a.importanceScore) return b.importanceScore - a.importanceScore
    return new Date(a.createdAt) - new Date(b.createdAt)
  })
})

function importanceType(score) {
  if (score >= 5) return 'danger'
  if (score >= 4) return 'warning'
  if (score >= 3) return 'primary'
  return 'info'
}

onMounted(async () => {
  try {
    const res = await briefingApi.getToday()
    console.log('今日简报数据：', res)
    if (res.exists) {
      briefing.value = res.data
    }

  } catch (e) {
    error.value = e.message || '网络错误'
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.briefing-page { max-width: 800px; margin: 0 auto; padding: 24px; }
.summary { color: #666; margin-bottom: 16px; }
.items { display: flex; flex-direction: column; gap: 12px; }
.news-card { width: 100%; }
.card-header { display: flex; justify-content: space-between; align-items: flex-start; gap: 12px; }
.news-title { font-weight: 500; color: #303133; text-decoration: none; flex: 1; }
.news-title:hover { color: #409eff; }
.tags { display: flex; gap: 6px; flex-shrink: 0; }
.ai-summary { margin-top: 8px; color: #606266; font-size: 14px; line-height: 1.6; }
</style>
