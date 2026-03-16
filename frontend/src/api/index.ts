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
    const url = error.config?.url || ''
    if (url.includes('/climate/')) {
      return mockClimateApi(url, error.config?.method)
    }
    if (url.includes('/risk/')) {
      return mockRiskApi(url, error.config?.method, error.config?.params)
    }
    return Promise.reject(error)
  }
)

const mockClimateApi = (url, method) => {
  if (!window.MockAPI) return Promise.reject(new Error('MockAPI not found'))
  if (url.includes('/climate/config') && method === 'get') {
    return Promise.resolve({ data: [] })
  }
  if (url.includes('/climate/curve') && method === 'get') {
    return Promise.resolve({
      curves: [
        { outdoorTemp: -20, supplyTemp: 75, returnTemp: 55 },
        { outdoorTemp: -15, supplyTemp: 68, returnTemp: 50 },
        { outdoorTemp: -10, supplyTemp: 60, returnTemp: 44 },
        { outdoorTemp: -5, supplyTemp: 52, returnTemp: 38 },
        { outdoorTemp: 0, supplyTemp: 45, returnTemp: 33 },
        { outdoorTemp: 5, supplyTemp: 40, returnTemp: 30 },
        { outdoorTemp: 10, supplyTemp: 35, returnTemp: 28 }
      ]
    })
  }
  if (url.includes('/climate/effect') && method === 'get') {
    return Promise.resolve({
      data: [
        { recordDate: '2026-03-10', energyBefore: 52, energyAfter: 45 },
        { recordDate: '2026-03-11', energyBefore: 48, energyAfter: 42 },
        { recordDate: '2026-03-12', energyBefore: 55, energyAfter: 48 },
        { recordDate: '2026-03-13', energyBefore: 50, energyAfter: 44 },
        { recordDate: '2026-03-14', energyBefore: 45, energyAfter: 40 },
        { recordDate: '2026-03-15', energyBefore: 42, energyAfter: 37 },
        { recordDate: '2026-03-16', energyBefore: 46, energyAfter: 40 }
      ]
    })
  }
  if (url.includes('/climate/statistics') && method === 'get') {
    return Promise.resolve({
      currentMode: '智能补偿',
      outdoorTemp: -5,
      supplyTemp: 52,
      savingsRate: 12.5
    })
  }
  return Promise.reject(new Error('No mock data'))
}

const mockRiskApi = (url, method, params) => {
  if (!window.MockAPI) return Promise.reject(new Error('MockAPI not found'))
  
  if (url.includes('/risk/index') && method === 'get') {
    return Promise.resolve({
      compositeIndex: 72,
      riskLevel: 'MEDIUM',
      pendingCount: 8,
      handledCount: 15,
      handledRate: 65
    })
  }
  if (url.includes('/risk/dimension') && method === 'get') {
    return Promise.resolve({
      data: [
        { name: '设备风险', type: 'equipment', score: 72, level: 'MEDIUM', count: 5 },
        { name: '运行风险', type: 'operation', score: 65, level: 'MEDIUM', count: 3 },
        { name: '能效风险', type: 'energy', score: 45, level: 'LOW', count: 2 },
        { name: '环境风险', type: 'environment', score: 38, level: 'LOW', count: 1 },
        { name: '安全风险', type: 'safety', score: 28, level: 'LOW', count: 1 }
      ]
    })
  }
  if (url.includes('/risk/details') && method === 'get') {
    return Promise.resolve({
      data: [
        { type: '设备', item: '1号锅炉效率下降', level: '高', score: 85, factors: '效率从92%下降至82%', suggestion: '检查燃烧器', status: '待处理' },
        { type: '设备', item: '3号循环泵振动超标', level: '中', score: 68, factors: '振动幅值超过标准', suggestion: '轴承检修', status: '已处理' },
        { type: '运行', item: '2号楼管网不平衡', level: '中', score: 65, factors: '流量偏差率12%', suggestion: '调节阀门', status: '待处理' },
        { type: '能效', item: '系统能效比下降', level: '低', score: 45, factors: 'COP从3.2下降至2.9', suggestion: '优化运行参数', status: '已处理' },
        { type: '安全', item: '燃气压力波动', level: '低', score: 35, factors: '压力波动在允许范围内', suggestion: '继续监控', status: '已处理' }
      ]
    })
  }
  if (url.includes('/risk/trend') && method === 'get') {
    return Promise.resolve({
      data: {
        dates: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
        equipment: [65, 68, 72, 70, 75, 78, 72],
        operation: [55, 58, 62, 60, 65, 68, 65],
        energy: [45, 48, 52, 50, 55, 52, 45],
        environment: [35, 38, 42, 40, 45, 42, 38],
        safety: [30, 32, 35, 33, 38, 35, 32],
        composite: [72, 68, 75, 70, 78, 75, 72]
      }
    })
  }
  if (url.includes('/risk/distribution') && method === 'get') {
    return Promise.resolve({
      data: [
        { value: 5, name: '设备风险' },
        { value: 3, name: '运行风险' },
        { value: 2, name: '能效风险' },
        { value: 1, name: '环境风险' },
        { value: 1, name: '安全风险' }
      ]
    })
  }
  if (url.includes('/risk/alert') && method === 'get') {
    return Promise.resolve({
      data: [
        { id: 1, level: 'critical', message: '1号锅炉效率低于80%，需立即检查', time: '10:30' },
        { id: 2, level: 'warn', message: '2号楼管网流量偏差超过10%', time: '09:15' },
        { id: 3, level: 'warn', message: '供水温度持续偏离设定值', time: '11:00' },
        { id: 4, level: 'info', message: '明日预报低温-18°C，请注意', time: '06:00' }
      ]
    })
  }
  return Promise.reject(new Error('No mock data'))
}

export const heatUserApi = {
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

export const buildingApi = {
  getBuildings: (params) => apiClient.get('/heat-user/building', { params }),
  getBuildingById: (id) => apiClient.get(`/heat-user/building/${id}`),
  getBuildingsByStation: (stationId) => apiClient.get(`/heat-user/building/station/${stationId}`),
  createBuilding: (data) => apiClient.post('/heat-user/building', data),
  updateBuilding: (id, data) => apiClient.put(`/heat-user/building/${id}`, data),
  deleteBuilding: (id) => apiClient.delete(`/heat-user/building/${id}`)
}

export const stationApi = {
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

export const balanceApi = {
  optimizeGA: (stationId, buildings) => apiClient.post('/balance/optimize/ga', { stationId, buildings }),
  optimizePSO: (stationId, buildings) => apiClient.post('/balance/optimize/pso', { stationId, buildings }),
  getOptimizationResults: (stationId, params) => apiClient.get(`/balance/results/${stationId}`, { params }),
  applyOptimization: (resultId) => apiClient.post(`/balance/apply/${resultId}`)
}

export const simulationApi = {
  runSimulation: (config) => apiClient.post('/simulation/run', config),
  runDynamicSimulation: (config, timeSteps) => apiClient.post('/simulation/dynamic', { config, timeSteps }),
  getSimulationResults: (resultId) => apiClient.get(`/simulation/result/${resultId}`),
  exportResults: (resultId, format) => apiClient.get(`/simulation/export/${resultId}`, { params: { format } })
}

export const valveApi = {
  getValves: (params) => apiClient.get('/valve', { params }),
  getValveById: (id) => apiClient.get(`/valve/${id}`),
  controlValve: (id, openRatio) => apiClient.post(`/valve/${id}/control`, { openRatio }),
  batchControl: (valves) => apiClient.post('/valve/batch-control', valves),
  getValveHistory: (id, params) => apiClient.get(`/valve/${id}/history`, { params }),
  
  calculatePID: (params) => apiClient.get('/valve/pid/calculate', { params }),
  calculateFuzzyPID: (params) => apiClient.get('/valve/pid/fuzzy', { params }),
  updatePIDParams: (id, params) => apiClient.put(`/valve/${id}/pid-params`, params)
}

export const boilerApi = {
  getBoilers: (params) => apiClient.get('/boiler', { params }),
  getBoilerById: (id) => apiClient.get(`/boiler/${id}`),
  getBoilerRealtime: (id) => apiClient.get(`/boiler/${id}/realtime`),
  controlBoiler: (id, command) => apiClient.post(`/boiler/${id}/control`, command),
  predictLoad: (hours) => apiClient.get('/boiler/predict', { params: { hours } })
}

export const alarmApi = {
  getAlarms: (params) => apiClient.get('/alarm', { params }),
  acknowledgeAlarm: (id) => apiClient.post(`/alarm/${id}/acknowledge`),
  getAlarmStats: () => apiClient.get('/alarm/stats')
}

export const anomalyApi = {
  getAnomalies: (params) => apiClient.get('/anomaly/list', { params }),
  getAnomalyById: (id) => apiClient.get(`/anomaly/${id}`),
  getPendingAnomalies: () => apiClient.get('/anomaly/pending'),
  createAnomaly: (data) => apiClient.post('/anomaly', data),
  confirmAnomaly: (id, remark) => apiClient.put(`/anomaly/${id}/confirm`, { remark }),
  resolveAnomaly: (id) => apiClient.put(`/anomaly/${id}/resolve`),
  getAnomalyStats: () => apiClient.get('/anomaly/stats'),
  getAnomaliesByDevice: (deviceId) => apiClient.get(`/anomaly/device/${deviceId}`)
}

export const alertApi = {
  getAlertConfigs: (params) => apiClient.get('/alert/config/list', { params }),
  getAlertConfigById: (id) => apiClient.get(`/alert/config/${id}`),
  createAlertConfig: (data) => apiClient.post('/alert/config', data),
  updateAlertConfig: (id, data) => apiClient.put(`/alert/config/${id}`, data),
  deleteAlertConfig: (id) => apiClient.delete(`/alert/config/${id}`),
  getEnabledConfigs: () => apiClient.get('/alert/config/enabled'),
  
  getAlertRecords: (params) => apiClient.get('/alert/list', { params }),
  createAlert: (data) => apiClient.post('/alert', data),
  acknowledgeAlert: (id, acknowledgedBy) => apiClient.put(`/alert/${id}/acknowledge`, { acknowledgedBy }),
  getUnacknowledgedCount: () => apiClient.get('/alert/unacknowledged/count')
}

export const climateApi = {
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

export const riskApi = {
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

export const efficiencyApi = {
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
