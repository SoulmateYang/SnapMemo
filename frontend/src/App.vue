<template>
  <el-container>
    <el-header>
      <span class="logo">AI 简报</span>
      <el-menu
        :default-active="activeMenu"
        mode="horizontal"
        router
        class="nav-menu"
      >
        <el-menu-item index="/today">今日简报</el-menu-item>
        <el-menu-item index="/history">历史简报</el-menu-item>
        <el-menu-item index="/sources">数据源配置</el-menu-item>
      </el-menu>
    </el-header>
    <el-main>
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const activeMenu = computed(() => route.path)
</script>

<style>
body { margin: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif; }
.el-header { background: #409eff; display: flex; align-items: center; }
.logo { color: white; font-size: 18px; font-weight: 600; margin-right: 40px; flex-shrink: 0; }
.nav-menu { flex: 1; background: transparent; border-bottom: none; }
.nav-menu.el-menu--horizontal { background: transparent; border-bottom: none; }
.nav-menu .el-menu-item { color: rgba(255, 255, 255, 0.85); border-bottom-color: transparent; }
.nav-menu .el-menu-item.is-active { color: #fff; border-bottom-color: #fff; background: transparent; }
.nav-menu .el-menu-item:hover { background: rgba(255, 255, 255, 0.1) !important; }
</style>
