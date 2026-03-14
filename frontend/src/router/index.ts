import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard.vue'),
    meta: { title: '监控大屏' }
  },
  {
    path: '/demand-based-heating',
    name: 'DemandBasedHeating',
    component: () => import('@/views/demand-based-heating/index.vue'),
    meta: { title: '热用户按需供热' }
  },
  {
    path: '/secondary-network',
    name: 'SecondaryNetwork',
    component: () => import('@/views/secondary-network/index.vue'),
    meta: { title: '二网平衡' }
  },
  {
    path: '/heat-station',
    name: 'HeatStation',
    component: () => import('@/views/heat-station/index.vue'),
    meta: { title: '换热站自控' }
  },
  {
    path: '/primary-network',
    name: 'PrimaryNetwork',
    component: () => import('@/views/primary-network/index.vue'),
    meta: { title: '一网控制策略' }
  },
  {
    path: '/heat-source',
    name: 'HeatSource',
    component: () => import('@/views/heat-source/index.vue'),
    meta: { title: '热源调控' }
  },
  {
    path: '/hydraulic-simulation',
    name: 'HydraulicSimulation',
    component: () => import('@/views/hydraulic-simulation/index.vue'),
    meta: { title: '水力仿真' }
  },
  {
    path: '/valve-control',
    name: 'ValveControl',
    component: () => import('@/views/valve-control/index.vue'),
    meta: { title: '智能电动调节' }
  },
  {
    path: '/buildings',
    name: 'Buildings',
    component: () => import('@/views/Buildings.vue'),
    meta: { title: '建筑物管理' }
  },
  {
    path: '/users',
    name: 'Users',
    component: () => import('@/views/Users.vue'),
    meta: { title: '热用户管理' }
  },
  {
    path: '/stations',
    name: 'Stations',
    component: () => import('@/views/Stations.vue'),
    meta: { title: '换热站管理' }
  },
  {
    path: '/alarms',
    name: 'Alarms',
    component: () => import('@/views/Alarms.vue'),
    meta: { title: '告警管理' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
