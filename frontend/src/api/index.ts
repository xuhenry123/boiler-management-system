/**
 * API服务层
 * 调用实际后端API服务
 */

import axios from 'axios'

// 配置后端API地址（使用相对路径，经过Vite代理）
const API_BASE_URL = ''

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
    return await apiClient.get('/api/heat-user/building', { params })
  },
  async getBuildingById(id) {
    return await apiClient.get(`/api/heat-user/building/${id}`)
  },
  async createBuilding(data) {
    return await apiClient.post('/api/heat-user/building', data)
  },
  async updateBuilding(id, data) {
    return await apiClient.put(`/api/heat-user/building/${id}`, data)
  },
  async deleteBuilding(id) {
    return await apiClient.delete(`/api/heat-user/building/${id}`)
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
  async getMaintenanceReminders() {
    return await apiClient.get('/api/equipment/maintenance-reminders')
  },
  async getTypeDistribution() {
    return await apiClient.get('/api/equipment/type-distribution')
  },
  async createEquipment(data) {
    return await apiClient.post('/api/equipment', data)
  },
  async updateEquipment(id, data) {
    return await apiClient.put(`/api/equipment/${id}`, data)
  }
}

// ==================== 热源 API ====================
export const heatSourceApi = {
  async getBoilers() {
    return await apiClient.get('/api/heat-source/boilers')
  },
  async getLoadPrediction() {
    return await apiClient.get('/api/heat-source/load-prediction')
  },
  async getEfficiencyComparison() {
    return await apiClient.get('/api/heat-source/efficiency-comparison')
  },
  async getModelInfo() {
    return await apiClient.get('/api/heat-source/model-info')
  },
  async adjustBoiler(id, direction) {
    return await apiClient.post(`/api/heat-source/adjust?id=${id}&direction=${direction}`)
  }
}

// ==================== 换热站自动控制 API ====================
export const stationControlApi = {
  async getAllStations() {
    return await apiClient.get('/api/station/all')
  },
  async getRealtimeData(stationId) {
    return await apiClient.get(`/api/station/${stationId}/realtime-data`)
  },
  async getPidConfig(stationId) {
    return await apiClient.get(`/api/station/${stationId}/pid-config`)
  },
  async savePidConfig(stationId, config) {
    return await apiClient.put(`/api/station/${stationId}/pid-config`, config)
  },
  async getHistoryData(stationId, period = 'week') {
    return await apiClient.get(`/api/station/${stationId}/history?period=${period}`)
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
  },
  async getStatistics() {
    return await apiClient.get('/api/climate/statistics')
  },
  async getConfigs(params = {}) {
    return await apiClient.get('/api/climate/configs', { params })
  },
  async getConfigById(id) {
    return await apiClient.get(`/api/climate/configs/${id}`)
  },
  async createConfig(data) {
    return await apiClient.post('/api/climate/configs', data)
  },
  async updateConfig(id, data) {
    return await apiClient.put(`/api/climate/configs/${id}`, data)
  },
  async deleteConfig(id) {
    return await apiClient.delete(`/api/climate/configs/${id}`)
  },
  async getCurves(params = {}) {
    return await apiClient.get('/api/climate/curves', { params })
  },
  async getEffects(params = {}) {
    return await apiClient.get('/api/climate/effects', { params })
  }
}

// ==================== 能效分析 API ====================
export const efficiencyApi = {
  async getData() {
    return { data: {} }
  },
  async getOverview(params = {}) {
    return await apiClient.get('/api/efficiency/overview', { params })
  },
  async getRanking(params = {}) {
    return await apiClient.get('/api/efficiency/ranking', { params })
  },
  async getTerminalMonitor(params = {}) {
    return await apiClient.get('/api/efficiency/terminal-monitor', { params })
  },
  async getComparison(params = {}) {
    return await apiClient.get('/api/efficiency/comparison', { params })
  },
  async getTrend(params = {}) {
    return await apiClient.get('/api/efficiency/trend', { params })
  },
  async getHeatingCurve(params = {}) {
    return await apiClient.get('/api/efficiency/heating-curve', { params })
  },
  async saveResidentTemperature(data) {
    return await apiClient.post('/api/efficiency/resident-temperature', data)
  },
  async applyHeatingCurve(stationId, curveData) {
    return await apiClient.post(`/api/efficiency/apply-curve/${stationId}`, curveData)
  }
}

// ==================== 风险评估 API ====================
export const riskApi = {
  async getRiskIndex(params = {}) {
    return await apiClient.get('/api/risk/index', { params })
  },
  async getRiskDimension() {
    return await apiClient.get('/api/risk/dimension')
  },
  async getRiskDetails(params = {}) {
    return await apiClient.get('/api/risk/details', { params })
  },
  async getRiskTrend(params = {}) {
    return await apiClient.get('/api/risk/trend', { params })
  },
  async getRiskDistribution() {
    return await apiClient.get('/api/risk/distribution')
  },
  async getRiskAlerts(params = {}) {
    return await apiClient.get('/api/risk/alert', { params })
  },
  async handleAlert(id, data) {
    return await apiClient.put(`/api/risk/alert/handle/${id}`, data)
  },
  async generateReport(type) {
    return await apiClient.get('/api/risk/report', { params: { type } })
  }
}

// ==================== 按需供热 API ====================
export const demandHeatingApi = {
  async getStats() {
    return await apiClient.get('/api/demand-heating/stats')
  },
  async getTemperatureData(params = {}) {
    return await apiClient.get('/api/demand-heating/temperature', { params })
  },
  async updateTargetTemp(userId, targetTemp) {
    return await apiClient.put(`/api/demand-heating/user/${userId}/target-temp`, { targetTemp })
  },
  async toggleValve(userId, status) {
    return await apiClient.put(`/api/demand-heating/user/${userId}/valve`, { status })
  },
  async getPrediction(userId, hours) {
    return await apiClient.get(`/api/demand-heating/prediction/${userId}`, { params: { hours } })
  },
  async getTemperatureDistribution() {
    return await apiClient.get('/api/demand-heating/temperature-distribution')
  }
}

// ==================== 阀门控制 API ====================
export const valveControlApi = {
  async getValves(params = {}) {
    return await apiClient.get('/api/valve', { params })
  },
  async getValveById(id) {
    return await apiClient.get(`/api/valve/${id}`)
  },
  async controlValve(id, openRatio) {
    return await apiClient.post(`/api/valve/${id}/control`, { openRatio })
  },
  async getFuzzyPIDConfig(valveId) {
    return await apiClient.get(`/api/valve/${valveId}/fuzzy-pid`)
  },
  async saveFuzzyPIDConfig(valveId, config) {
    return await apiClient.put(`/api/valve/${valveId}/fuzzy-pid`, config)
  },
  async getControlHistory(valveId, period) {
    return await apiClient.get(`/api/valve/${valveId}/history`, { params: { period } })
  }
}

// ==================== 负荷预测 API ====================
export const loadForecastApi = {
  async getForecast(params = {}) {
    return await apiClient.get('/api/load-forecast', { params })
  },
  async getStationForecast(stationId, params = {}) {
    return await apiClient.get(`/api/load-forecast/station/${stationId}`, { params })
  },
  async getForecastDetails(params = {}) {
    return await apiClient.get('/api/load-forecast/details', { params })
  }
}

// ==================== 气象数据 API ====================
export const meteorologyApi = {
  async getCurrentWeather() {
    return await apiClient.get('/api/meteorology/current')
  },
  async getForecast(params = {}) {
    return await apiClient.get('/api/meteorology/forecast', { params })
  },
  async getHistory(params = {}) {
    return await apiClient.get('/api/meteorology/history', { params })
  },
  async getWarnings() {
    return await apiClient.get('/api/meteorology/warnings')
  }
}

// ==================== 平衡策略 API ====================
export const balanceStrategyApi = {
  async getStrategies(params = {}) {
    return await apiClient.get('/api/balance/strategies', { params })
  },
  async executeStrategy(id) {
    return await apiClient.post(`/api/balance/strategies/${id}/execute`)
  },
  async getExecutionRecords(params = {}) {
    return await apiClient.get('/api/balance/execution-records', { params })
  }
}

// ==================== 趋势分析 API ====================
export const trendAnalysisApi = {
  async getEnergyRank(params = {}) {
    return await apiClient.get('/api/trend/energy-rank', { params })
  },
  async getAnomalyData(params = {}) {
    return await apiClient.get('/api/trend/anomaly', { params })
  },
  async getReports(params = {}) {
    return await apiClient.get('/api/trend/reports', { params })
  }
}

// ==================== 能耗分析 API ====================
export const costAnalysisApi = {
  async getRankList(params = {}) {
    return await apiClient.get('/api/cost/rank', { params })
  },
  async getCostList(params = {}) {
    return await apiClient.get('/api/cost/list', { params })
  },
  async getCostTrend(params = {}) {
    return await apiClient.get('/api/cost/trend', { params })
  }
}

// ==================== 仿真 API ====================
export const simulationApi = {
  async getNodes(params = {}) {
    return await apiClient.get('/api/simulation/nodes', { params })
  },
  async runHydraulicSimulation(params = {}) {
    return await apiClient.post('/api/simulation/hydraulic', params)
  },
  async getSimulationResults(params = {}) {
    return await apiClient.get('/api/simulation/results', { params })
  }
}

// ==================== 异常预测 API ====================
export const anomalyPredictionApi = {
  async getMonitorPoints() {
    return await apiClient.get('/api/anomaly/monitor-points')
  },
  async getPredictions(params = {}) {
    return await apiClient.get('/api/anomaly/predictions', { params })
  },
  async getPredictionDetail(id) {
    return await apiClient.get(`/api/anomaly/predictions/${id}`)
  },
  async acknowledgeAnomaly(id) {
    return await apiClient.post(`/api/anomaly/predictions/${id}/acknowledge`)
  }
}

// ==================== 场景优化 API ====================
export const multiScenarioApi = {
  async getScenarios(params = {}) {
    return await apiClient.get('/api/scenario/scenarios', { params })
  },
  async getObjectives() {
    return await apiClient.get('/api/scenario/objectives')
  },
  async runOptimization(data) {
    return await apiClient.post('/api/scenario/optimize', data)
  }
}
