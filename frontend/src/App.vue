<template>
  <el-container class="app-container">
    <el-aside width="220px" class="sidebar">
      <div class="logo">
        <el-icon :size="28"><Sunny /></el-icon>
        <span>智慧供热系统</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        background-color="#1a1a2e"
        text-color="#fff"
        active-text-color="#409eff"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><Monitor /></el-icon>
          <span>监控大屏</span>
        </el-menu-item>
        
        <el-sub-menu index="heat-management">
          <template #title>
            <el-icon><Coin /></el-icon>
            <span>供热管理</span>
          </template>
          <el-menu-item index="/demand-based-heating">热用户按需供热</el-menu-item>
          <el-menu-item index="/secondary-network">二网平衡</el-menu-item>
          <el-menu-item index="/heat-station">换热站自控</el-menu-item>
          <el-menu-item index="/primary-network">一网控制策略</el-menu-item>
          <el-menu-item index="/climate-compensation">气候补偿模式</el-menu-item>
          <el-menu-item index="/secondary-temp-control">二级网控温</el-menu-item>
          <el-menu-item index="/balance-strategy">管网平衡策略</el-menu-item>
          <el-menu-item index="/time-zone-control">分时分区控制</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="analysis">
          <template #title>
            <el-icon><DataAnalysis /></el-icon>
            <span>数据分析</span>
          </template>
          <el-menu-item index="/meteorology">气象分析</el-menu-item>
          <el-menu-item index="/load-forecast">负荷预测</el-menu-item>
          <el-menu-item index="/network-analysis">管网平衡分析</el-menu-item>
          <el-menu-item index="/trend-analysis">趋势分析</el-menu-item>
          <el-menu-item index="/cost-analysis">成本分析</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="optimization">
          <template #title>
            <el-icon><TrendCharts /></el-icon>
            <span>优化调度</span>
          </template>
          <el-menu-item index="/equipment">供能设备管理</el-menu-item>
          <el-menu-item index="/risk-assessment">风险评估</el-menu-item>
          <el-menu-item index="/anomaly-prediction">异常状态预测</el-menu-item>
          <el-menu-item index="/multi-energy-optimization">多能耦合寻优</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="source-control">
          <template #title>
            <el-icon><OfficeBuilding /></el-icon>
            <span>热源管理</span>
          </template>
          <el-menu-item index="/heat-source">热源调控</el-menu-item>
          <el-menu-item index="/hydraulic-simulation">水力仿真</el-menu-item>
          <el-menu-item index="/valve-control">智能电动调节</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/buildings">建筑物管理</el-menu-item>
          <el-menu-item index="/users">热用户管理</el-menu-item>
          <el-menu-item index="/stations">换热站管理</el-menu-item>
          <el-menu-item index="/alarms">告警管理</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentRouteName }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-badge :value="3" class="badge">
            <el-button :icon="Bell" circle />
          </el-badge>
          <el-dropdown>
            <span class="user-info">
              <el-icon><User /></el-icon>
              <span>管理员</span>
            </span>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { Bell, Sunny, Monitor, Coin, OfficeBuilding, Setting, User, DataAnalysis, TrendCharts } from '@element-plus/icons-vue'

const route = useRoute()
const activeMenu = computed(() => route.path)
const currentRouteName = computed(() => route.meta.title || '')
</script>

<style scoped>
.app-container {
  height: 100vh;
}

.sidebar {
  background-color: #1a1a2e;
  overflow-x: hidden;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #fff;
  font-size: 16px;
  font-weight: bold;
  border-bottom: 1px solid #2d2d44;
}

.sidebar-menu {
  border-right: none;
}

.header {
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.08);
}

.header-left {
  display: flex;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
}

.badge {
  margin-right: 10px;
}

.main-content {
  background-color: #f5f7fa;
  padding: 20px;
}
</style>
