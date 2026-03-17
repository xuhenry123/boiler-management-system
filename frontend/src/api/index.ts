/**
 * API服务层
 * 使用本地数据存储提供实际的数据 CRUD 操作
 */

import axios from 'axios'

const apiClient = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

apiClient.interceptors.response.use(
  response => response.data,
  error => {
    console.error('API Error:', error)
    return Promise.reject(error)
  }
)

// 延迟函数
const delay = (ms = 300) => new Promise(resolve => setTimeout(resolve, ms))

// 检查DataStore是否可用
const hasDataStore = () => typeof window.DataStore !== 'undefined'

// ==================== 仪表盘 API ====================
export const dashboardApi = {
  async getStats() {
    if (hasDataStore()) {
      return await window.DataStore.getDashboardStats()
    }
    return { stationCount: 3, buildingCount: 5, userCount: 50, alarmCount: 3 }
  },
  async getTemperatureTrend() {
    if (hasDataStore()) {
      return await window.DataStore.getTemperatureTrend()
    }
    return { primarySupply: [118, 120, 122, 121, 119, 120], primaryReturn: [68, 70, 72, 71, 69, 70], secondarySupply: [48, 50, 51, 50, 49, 50], secondaryReturn: [38, 40, 41, 40, 39, 40] }
  },
  async getStationStatus() {
    if (hasDataStore()) {
      return await window.DataStore.getStationStatus()
    }
    return [{ value: 20, name: '运行中' }, { value: 3, name: '停止' }, { value: 1, name: '故障' }]
  },
  async getHeatLoad() {
    if (hasDataStore()) {
      return await window.DataStore.getHeatLoad()
    }
    return [45, 42, 55, 60, 58, 52]
  },
  async getAlarms() {
    if (hasDataStore()) {
      return await window.DataStore.getAlarmsList()
    }
    return []
  }
}

// ==================== 换热站 API ====================
export const stationApi = {
  async getStations(params = {}) {
    if (hasDataStore()) {
      return await window.DataStore.getStations(params)
    }
    return { data: [], total: 0 }
  },
  async getStationById(id) {
    if (hasDataStore()) {
      return await window.DataStore.getStationById(id)
    }
    return null
  },
  async createStation(data) {
    if (hasDataStore()) {
      return await window.DataStore.createStation(data)
    }
    return { success: false }
  },
  async updateStation(id, data) {
    if (hasDataStore()) {
      return await window.DataStore.updateStation(id, data)
    }
    return { success: false }
  },
  async deleteStation(id) {
    if (hasDataStore()) {
      return await window.DataStore.deleteStation(id)
    }
    return { success: false }
  }
}

// ==================== 建筑物 API ====================
export const buildingApi = {
  async getBuildings(params = {}) {
    if (hasDataStore()) {
      return await window.DataStore.getBuildings(params)
    }
    return { data: [], total: 0 }
  },
  async getBuildingById(id) {
    if (hasDataStore()) {
      return await window.DataStore.getBuildingById(id)
    }
    return null
  },
  async createBuilding(data) {
    if (hasDataStore()) {
      return await window.DataStore.createBuilding(data)
    }
    return { success: false }
  },
  async updateBuilding(id, data) {
    if (hasDataStore()) {
      return await window.DataStore.updateBuilding(id, data)
    }
    return { success: false }
  },
  async deleteBuilding(id) {
    if (hasDataStore()) {
      return await window.DataStore.deleteBuilding(id)
    }
    return { success: false }
  }
}

// ==================== 热用户 API ====================
export const heatUserApi = {
  async getUsers(params = {}) {
    if (hasDataStore()) {
      return await window.DataStore.getUsers(params)
    }
    return { data: [], total: 0 }
  },
  async getUserById(id) {
    if (hasDataStore()) {
      return await window.DataStore.getUserById(id)
    }
    return null
  },
  async createUser(data) {
    if (hasDataStore()) {
      return await window.DataStore.createUser(data)
    }
    return { success: false }
  },
  async updateUser(id, data) {
    if (hasDataStore()) {
      return await window.DataStore.updateUser(id, data)
    }
    return { success: false }
  },
  async deleteUser(id) {
    if (hasDataStore()) {
      return await window.DataStore.deleteUser(id)
    }
    return { success: false }
  }
}

// ==================== 告警 API ====================
export const alarmApi = {
  async getAlarms(params = {}) {
    if (hasDataStore()) {
      return await window.DataStore.getAlarms(params)
    }
    return { data: [], total: 0 }
  },
  async acknowledgeAlarm(id) {
    if (hasDataStore()) {
      return await window.DataStore.acknowledgeAlarm(id)
    }
    return { success: false }
  },
  async processAlarm(id, handleType, remark) {
    if (hasDataStore()) {
      return await window.DataStore.processAlarm(id, handleType, remark)
    }
    return { success: false }
  }
}

// ==================== 设备 API ====================
export const equipmentApi = {
  async getEquipment(params = {}) {
    if (hasDataStore()) {
      return await window.DataStore.getEquipment(params)
    }
    return { data: [], total: 0 }
  },
  async createEquipment(data) {
    if (hasDataStore()) {
      return await window.DataStore.createEquipment(data)
    }
    return { success: false }
  },
  async updateEquipment(id, data) {
    if (hasDataStore()) {
      return await window.DataStore.updateEquipment(id, data)
    }
    return { success: false }
  }
}

// ==================== 锅炉 API ====================
export const boilerApi = {
  async getBoilers() {
    if (hasDataStore()) {
      return await window.DataStore.getBoilers()
    }
    return { data: [] }
  },
  async adjustBoiler(id, loadRate) {
    if (hasDataStore()) {
      return await window.DataStore.adjustBoiler(id, loadRate)
    }
    return { success: false }
  }
}

// ==================== 保留原有API（通过axios调用后端）====================
// 以下保留原有API以便向后兼容，当后端服务可用时可切换使用

export const heatUserApiLegacy = {
  getUsers: (params) => apiClient.get('/heat-user', { params }),
  getUserById: (id) => apiClient.get(`/heat-user/${id}`),
  getUsersByBuilding: (buildingId) => apiClient.get(`/heat-user/building/${buildingId}`),
  createUser: (data) => apiClient.post('/heat-user', data),
  updateUser: (id, data) => apiClient.put(`/heat-user/${id}`, data),
  deleteUser: (id) => apiClient.delete(`/heat-user/${id}`),
  getTemperatures: (userId, params) => apiClient.get(`/heat-user/temperature/${userId}`, { params }),
  getLatestTemp: (userId) => apiClient.get(`/heat-user/temperature/${userId}/latest`),
  predict: (userId, hours = 2) => apiClient.get(`/heat-user/temperature/${userId}/predict`, { params: { hours } }),
  batchSaveTemp: (data) => apiClient.post('/heat-user/temperature/batch', data)
}

export const buildingApiLegacy = {
  getBuildings: (params) => apiClient.get('/heat-user/building', { params }),
  getBuildingById: (id) => apiClient.get(`/heat-user/building/${id}`),
  getBuildingsByStation: (stationId) => apiClient.get(`/heat-user/building/station/${stationId}`),
  createBuilding: (data) => apiClient.post('/heat-user/building', data),
  updateBuilding: (id, data) => apiClient.put(`/heat-user/building/${id}`, data),
  deleteBuilding: (id) => apiClient.delete(`/heat-user/building/${id}`)
}

export const stationApiLegacy = {
  getStations: (params) => apiClient.get('/station', { params }),
  getStationById: (id) => apiClient.get(`/station/${id}`),
  getAllStations: () => apiClient.get('/station/all'),
  getActiveStations: () => apiClient.get('/station/active'),
  createStation: (data) => apiClient.post('/station', data),
  updateStation: (id, data) => apiClient.put(`/station/${id}`, data),
  getRealtimeData: (stationId) => apiClient.get(`/station/${stationId}/realtime`),
  getHistoricalData: (stationId, params) => apiClient.get(`/station/${stationId}/history`, { params }),
  sendControlCommand: (stationId, data) => apiClient.post(`/station/${stationId}/control`, data)
}

export const alarmApiLegacy = {
  getAlarms: (params) => apiClient.get('/alarm', { params }),
  acknowledgeAlarm: (id) => apiClient.post(`/alarm/${id}/acknowledge`),
  getAlarmStats: () => apiClient.get('/alarm/stats')
}

export const climateApiLegacy = {
  getConfigs: (params) => apiClient.get('/climate/config', { params }),
  getConfigById: (id) => apiClient.get(`/climate/config/${id}`),
  getActiveConfig: (stationId) => apiClient.get('/climate/config/active', { params: { stationId } }),
  createConfig: (data) => apiClient.post('/climate/config', data),
  updateConfig: (id, data) => apiClient.put(`/climate/config/${id}`, data),
  deleteConfig: (id) => apiClient.delete(`/climate/config/${id}`),
  getCurves: (params) => apiClient.get('/climate/curve', { params }),
  saveCurves: (configId, curves) => apiClient.post('/climate/curve', curves, { params: { configId } }),
  getEffects: (params) => apiClient.get('/climate/effect', { params }),
  getStatistics: (stationId) => apiClient.get('/climate/statistics', { params: { stationId } })
}

export const riskApiLegacy = {
  getAssessment: () => apiClient.get('/risk/assessment'),
  getIndex: (params) => apiClient.get('/risk/index', { params }),
  getTrend: (params) => apiClient.get('/risk/trend', { params }),
  getDistribution: () => apiClient.get('/risk/distribution'),
  getDetails: (params) => apiClient.get('/risk/details', { params }),
  handleRisk: (id, data) => apiClient.put(`/risk/handle/${id}`, data),
  getAlerts: (params) => apiClient.get('/risk/alert', { params }),
  handleAlert: (id, data) => apiClient.put(`/risk/alert/handle/${id}`, data),
  ignoreAlert: (id) => apiClient.put(`/risk/alert/ignore/${id}`),
  getConfig: () => apiClient.get('/risk/config'),
  updateConfig: (data) => apiClient.put('/risk/config', data),
  generateReport: (type) => apiClient.get('/risk/report', { params: { type } })
}

export const efficiencyApiLegacy = {
  getOverview: (params) => apiClient.get('/efficiency/overview', { params }),
  getRanking: (params) => apiClient.get('/efficiency/ranking', { params }),
  getTerminalMonitor: (params) => apiClient.get('/efficiency/terminal-monitor', { params }),
  getComparison: (params) => apiClient.get('/efficiency/comparison', { params }),
  getResidentTemperatures: (params) => apiClient.get('/efficiency/resident-temperatures', { params }),
  saveResidentTemperature: (data) => apiClient.post('/efficiency/resident-temperatures/manual', data),
  getHeatingCurve: (params) => apiClient.get('/efficiency/heating-curve', { params }),
  applyHeatingCurve: (stationId, curve) => apiClient.post(`/efficiency/heating-curve/apply?stationId=${stationId}`, curve),
  getTrend: (params) => apiClient.get('/efficiency/trend', { params })
}
