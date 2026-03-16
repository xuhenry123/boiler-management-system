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
