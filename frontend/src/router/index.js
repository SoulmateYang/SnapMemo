import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/today'
  },
  {
    path: '/today',
    name: 'Today',
    component: () => import('../views/TodayBriefing.vue')
  },
  {
    path: '/history',
    name: 'History',
    component: () => import('../views/History.vue')
  },
  {
    path: '/sources',
    name: 'Sources',
    component: () => import('../views/Sources.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
