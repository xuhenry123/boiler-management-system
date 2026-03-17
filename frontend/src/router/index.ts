import { createRouter, createWebHistory } from 'vue-router'

/**
 * 路由配置数组
 * 定义了系统的所有页面路由，包括路径、组件名称、页面标题等
 */
const routes = [
  // 默认重定向到监控大屏
  {
    path: '/',
    redirect: '/dashboard'
  },
  // 监控大屏
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard.vue'),
    meta: { title: '监控大屏' }
  },
  // 热用户按需供热
  {
    path: '/demand-based-heating',
    name: 'DemandBasedHeating',
    component: () => import('@/views/demand-based-heating/index.vue'),
    meta: { title: '热用户按需供热' }
  },
  // 二网平衡
  {
    path: '/secondary-network',
    name: 'SecondaryNetwork',
    component: () => import('@/views/secondary-network/index.vue'),
    meta: { title: '二网平衡' }
  },
  // 换热站自控
  {
    path: '/heat-station',
    name: 'HeatStation',
    component: () => import('@/views/heat-station/index.vue'),
    meta: { title: '换热站自控' }
  },
  // 一网控制策略
  {
    path: '/primary-network',
    name: 'PrimaryNetwork',
    component: () => import('@/views/primary-network/index.vue'),
    meta: { title: '一网控制策略' }
  },
  // 热源调控
  {
    path: '/heat-source',
    name: 'HeatSource',
    component: () => import('@/views/heat-source/index.vue'),
    meta: { title: '热源调控' }
  },
  // 水力仿真
  {
    path: '/hydraulic-simulation',
    name: 'HydraulicSimulation',
    component: () => import('@/views/hydraulic-simulation/index.vue'),
    meta: { title: '水力仿真' }
  },
  // 智能电动调节
  {
    path: '/valve-control',
    name: 'ValveControl',
    component: () => import('@/views/valve-control/index.vue'),
    meta: { title: '智能电动调节' }
  },
  // 建筑物管理
  {
    path: '/buildings',
    name: 'Buildings',
    component: () => import('@/views/Buildings.vue'),
    meta: { title: '建筑物管理' }
  },
  // 热用户管理
  {
    path: '/users',
    name: 'Users',
    component: () => import('@/views/Users.vue'),
    meta: { title: '热用户管理' }
  },
  // 换热站管理
  {
    path: '/stations',
    name: 'Stations',
    component: () => import('@/views/Stations.vue'),
    meta: { title: '换热站管理' }
  },
  // 告警管理
  {
    path: '/alarms',
    name: 'Alarms',
    component: () => import('@/views/Alarms.vue'),
    meta: { title: '告警管理' }
  },
  // 气象分析
  {
    path: '/meteorology',
    name: 'Meteorology',
    component: () => import('@/views/meteorology/index.vue'),
    meta: { title: '气象分析' }
  },
  // 负荷预测
  {
    path: '/load-forecast',
    name: 'LoadForecast',
    component: () => import('@/views/load-forecast/index.vue'),
    meta: { title: '负荷预测' }
  },
  // 管网平衡分析
  {
    path: '/network-analysis',
    name: 'NetworkAnalysis',
    component: () => import('@/views/network-analysis/index.vue'),
    meta: { title: '管网平衡分析' }
  },
  // 趋势分析
  {
    path: '/trend-analysis',
    name: 'TrendAnalysis',
    component: () => import('@/views/trend-analysis/index.vue'),
    meta: { title: '趋势分析' }
  },
  // 供能设备管理
  {
    path: '/equipment',
    name: 'Equipment',
    component: () => import('@/views/equipment/index.vue'),
    meta: { title: '供能设备管理' }
  },
  // 气候补偿模式
  {
    path: '/climate-compensation',
    name: 'ClimateCompensation',
    component: () => import('@/views/climate-compensation/index.vue'),
    meta: { title: '气候补偿模式' }
  },
  // 二级网控温
  {
    path: '/secondary-temp-control',
    name: 'SecondaryTempControl',
    component: () => import('@/views/secondary-temp-control/index.vue'),
    meta: { title: '二级网控温' }
  },
  // 管网平衡策略
  {
    path: '/balance-strategy',
    name: 'BalanceStrategy',
    component: () => import('@/views/balance-strategy/index.vue'),
    meta: { title: '管网平衡策略' }
  },
  // 分时分区控制
  {
    path: '/time-zone-control',
    name: 'TimeZoneControl',
    component: () => import('@/views/time-zone-control/index.vue'),
    meta: { title: '分时分区控制' }
  },
  // 风险评估
  {
    path: '/risk-assessment',
    name: 'RiskAssessment',
    component: () => import('@/views/risk-assessment/index.vue'),
    meta: { title: '风险评估' }
  },
  // 异常状态预测
  {
    path: '/anomaly-prediction',
    name: 'AnomalyPrediction',
    component: () => import('@/views/anomaly-prediction/index.vue'),
    meta: { title: '异常状态预测' }
  },
  // 多能耦合寻优
  {
    path: '/multi-energy-optimization',
    name: 'MultiEnergyOptimization',
    component: () => import('@/views/multi-energy-optimization/index.vue'),
    meta: { title: '多能耦合寻优' }
  },
  // 多场景多目标调整
  {
    path: '/multi-scenario-optimization',
    name: 'MultiScenarioOptimization',
    component: () => import('@/views/multi-scenario-optimization/index.vue'),
    meta: { title: '多场景多目标调整' }
  },
  // 蒸汽管理
  {
    path: '/steam-management',
    name: 'SteamManagement',
    component: () => import('@/views/steam-management/index.vue'),
    meta: { title: '蒸汽管理' }
  },
  // 成本分析
  {
    path: '/cost-analysis',
    name: 'CostAnalysis',
    component: () => import('@/views/cost-analysis/index.vue'),
    meta: { title: '成本分析' }
  },
  // 冷热效率分析
  {
    path: '/efficiency-analysis',
    name: 'EfficiencyAnalysis',
    component: () => import('@/views/efficiency-analysis/index.vue'),
    meta: { title: '冷热效率分析' }
  },
  // 热源站管理
  {
    path: '/heat-source-station',
    name: 'HeatSourceStation',
    component: () => import('@/views/heat-source-station/index.vue'),
    meta: { title: '热源站管理' }
  },
  // 冷源站管理
  {
    path: '/cold-source-station',
    name: 'ColdSourceStation',
    component: () => import('@/views/cold-source-station/index.vue'),
    meta: { title: '冷源站管理' }
  }
]

/**
 * 创建Vue Router实例
 * 使用HTML5 History模式管理路由
 */
const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
