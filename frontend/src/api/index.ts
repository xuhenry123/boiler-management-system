/**
 * API服务层
 * 调用实际后端API服务
 */

import axios from 'axios'

// 配置后端API地址
const API_BASE_URL = 'http://localhost:8080'

const apiClient = axios.create({
  baseURL: API_BASE_URL,
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

// ==================== 仪表盘 API ====================
export const dashboardApi = {
  async getStats() {
    return await apiClient.get('/api/dashboard/stats')
  },
  async getTemperatureTrend() {
    return await apiClient.get('/api/dashboard/temperature-trend')
  },
  async getStationStatus() {
    return await apiClient.get('/api/dashboard/station-status')
  },
  async getHeatLoad() {
    return await apiClient.get('/api/dashboard/heat-load')
  },
  async getAlarms() {
    return await apiClient.get('/api/dashboard/alarms')
  }
}

// ==================== 换热站 API ====================
export const stationApi = {
  async getStations(params = {}) {
    return await apiClient.get('/api/station', { params })
  },
  async getStationById(id) {
    return await apiClient.get(`/api/station/${id}`)
  },
  async createStation(data) {
    return await apiClient.post('/api/station', data)
  },
  async updateStation(id, data) {
    return await apiClient.put(`/api/station/${id}`, data)
  },
  async deleteStation(id) {
    return await apiClient.delete(`/api/station/${id}`)
  }
}

// ==================== 建筑物 API ====================
export const buildingApi = {
  async getBuildings(params = {}) {
    return await apiClient.get('/api/building', { params })
  },
  async getBuildingById(id) {
    return await apiClient.get(`/api/building/${id}`)
  },
  async createBuilding(data) {
    return await apiClient.post('/api/building', data)
  },
  async updateBuilding(id, data) {
    return await apiClient.put(`/api/building/${id}`, data)
  },
  async deleteBuilding(id) {
    return await apiClient.delete(`/api/building/${id}`)
  }
}

// ==================== 热用户 API ====================
export const heatUserApi = {
  async getUsers(params = {}) {
    return await apiClient.get('/api/heat-user', { params })
  },
  async getUserById(id) {
    return await apiClient.get(`/api/heat-user/${id}`)
  },
  async createUser(data) {
    return await apiClient.post('/api/heat-user', data)
  },
  async updateUser(id, data) {
    return await apiClient.put(`/api/heat-user/${id}`, data)
  },
  async deleteUser(id) {
    return await apiClient.delete(`/api/heat-user/${id}`)
  }
}

// ==================== 告警 API ====================
export const alarmApi = {
  async getAlarms(params = {}) {
    return await apiClient.get('/api/alarm', { params })
  },
  async acknowledgeAlarm(id) {
    return await apiClient.post(`/api/alarm/${id}/acknowledge`)
  },
  async processAlarm(id, handleType, remark) {
    return await apiClient.put(`/api/alarm/${id}/process`, { handleType, remark })
  }
}

// ==================== 设备 API ====================
export const equipmentApi = {
  async getEquipment(params = {}) {
    return await apiClient.get('/api/equipment', { params })
  },
  async createEquipment(data) {
    return await apiClient.post('/api/equipment', data)
  },
  async updateEquipment(id, data) {
    return await apiClient.put(`/api/equipment/${id}`, data)
  }
}

// ==================== 锅炉 API ====================
export const boilerApi = {
  async getBoilers() {
    return await apiClient.get('/api/boiler')
  },
  async adjustBoiler(id, data) {
    return await apiClient.put(`/api/boiler/${id}`, data)
  }
}

// ==================== 异常预测 API ====================
export const anomalyApi = {
  async getPredictions() {
    return { data: [], pendingCount: 0, highProbaCount: 0 }
  }
}

// ==================== 告警API别名 ====================
export const alertApi = alarmApi

// ==================== 气候补偿 API ====================
export const climateApi = {
  async getConfig() {
    return { data: {} }
  }
}

// ==================== 能效分析 API ====================
export const efficiencyApi = {
  async getData() {
    return { data: {} }
  }
}
