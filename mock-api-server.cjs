const express = require('express');
const app = express();
const PORT = 8080;

function random(min, max) {
  return Math.random() * (max - min) + min;
}

function randomInt(min, max) {
  return Math.floor(random(min, max + 1));
}

function formatDate(offset = 0) {
  const date = new Date();
  date.setHours(date.getHours() + offset);
  return date.toISOString().slice(0, 19).replace('T', ' ');
}

app.use(express.json());

// ============ 数据存储 ============

// 换热站数据
let stations = [
  { id: 1, stationCode: 'HS001', stationName: '东城区换热站', address: '北京市东城区', designCapacity: 50, designFlow: 800, primarySupplyTemp: 120, primaryReturnTemp: 70, secondarySupplyTemp: 50, secondaryReturnTemp: 40, status: 1 },
  { id: 2, stationCode: 'HS002', stationName: '西城区换热站', address: '北京市西城区', designCapacity: 40, designFlow: 650, primarySupplyTemp: 120, primaryReturnTemp: 70, secondarySupplyTemp: 50, secondaryReturnTemp: 40, status: 1 },
  { id: 3, stationCode: 'HS003', stationName: '朝阳区换热站', address: '北京市朝阳区', designCapacity: 60, designFlow: 1000, primarySupplyTemp: 120, primaryReturnTemp: 70, secondarySupplyTemp: 50, secondaryReturnTemp: 40, status: 1 }
];
let stationNextId = 4;

// 建筑物数据
let buildings = [
  { id: 1, buildingCode: 'BLD001', buildingName: '阳光花园小区1号楼', address: '东城区阳光路1号', areaTotal: 15000, areaHeated: 12000, buildYear: 2015, buildingType: 'residential', heatTransferCoefficient: 1.2, stationId: 1, status: 1 },
  { id: 2, buildingCode: 'BLD002', buildingName: '阳光花园小区2号楼', address: '东城区阳光路2号', areaTotal: 18000, areaHeated: 15000, buildYear: 2015, buildingType: 'residential', heatTransferCoefficient: 1.15, stationId: 1, status: 1 },
  { id: 3, buildingCode: 'BLD003', buildingName: '商业大厦A座', address: '西城区金融街8号', areaTotal: 25000, areaHeated: 20000, buildYear: 2018, buildingType: 'commercial', heatTransferCoefficient: 0.9, stationId: 2, status: 1 },
  { id: 4, buildingCode: 'BLD004', buildingName: '科技园区办公楼1', address: '朝阳区中关村大街1号', areaTotal: 30000, areaHeated: 25000, buildYear: 2020, buildingType: 'commercial', heatTransferCoefficient: 0.85, stationId: 3, status: 1 }
];
let buildingNextId = 5;

// 热用户数据
let heatUsers = [
  { id: 1, userCode: 'U001', userName: '张三', buildingId: 1, unitNo: '1', roomNo: '101', area: 120, targetTemp: 20, status: 1 },
  { id: 2, userCode: 'U002', userName: '李四', buildingId: 1, unitNo: '1', roomNo: '102', area: 120, targetTemp: 21, status: 1 },
  { id: 3, userCode: 'U003', userName: '王五', buildingId: 2, unitNo: '2', roomNo: '201', area: 130, targetTemp: 20, status: 1 },
  { id: 4, userCode: 'U004', userName: '赵六', buildingId: 2, unitNo: '2', roomNo: '301', area: 140, targetTemp: 22, status: 1 },
  { id: 5, userCode: 'U005', userName: '钱七', buildingId: 3, unitNo: 'A', roomNo: '1001', area: 200, targetTemp: 20, status: 1 }
];
let userNextId = 6;

// 告警数据
let alarms = [
  { id: 1, alarmType: 'temperature', alarmLevel: 'Warning', alarmMessage: '1号楼101室温度过低', alarmValue: 16, thresholdValue: 18, startTime: formatDate(-2), acknowledged: 0 },
  { id: 2, alarmType: 'equipment', alarmLevel: 'Critical', alarmMessage: '2号循环泵运行异常', alarmValue: 0, thresholdValue: 1, startTime: formatDate(-1), acknowledged: 0 },
  { id: 3, alarmType: 'pressure', alarmLevel: 'Info', alarmMessage: '3号换热站压力偏低', alarmValue: 0.35, thresholdValue: 0.4, startTime: formatDate(-3), acknowledged: 1 },
  { id: 4, alarmType: 'flow', alarmLevel: 'Warning', alarmMessage: '5号楼入口流量偏低', alarmValue: 0.8, thresholdValue: 1.0, startTime: formatDate(-5), acknowledged: 0 },
  { id: 5, alarmType: 'temperature', alarmLevel: 'Info', alarmMessage: '室外温度骤降', alarmValue: -15, thresholdValue: -10, startTime: formatDate(-0.5), acknowledged: 1 }
];
let alarmNextId = 6;

// 设备数据
let equipment = [
  { id: 1, equipmentCode: 'EQ001', equipmentName: '1号循环泵', equipmentType: 'pump', stationId: 1, model: 'KQL150/315', manufacturer: '上海凯泉', status: 'running', maintenanceStatus: 'normal' },
  { id: 2, equipmentCode: 'EQ002', equipmentName: '2号循环泵', equipmentType: 'pump', stationId: 1, model: 'KQL150/315', manufacturer: '上海凯泉', status: 'standby', maintenanceStatus: 'normal' },
  { id: 3, equipmentCode: 'EQ003', equipmentName: '补水泵', equipmentType: 'pump', stationId: 1, model: 'KQL50/150', manufacturer: '上海凯泉', status: 'running', maintenanceStatus: 'normal' },
  { id: 4, equipmentCode: 'EQ004', equipmentName: '1号换热器', equipmentType: 'heat_exchanger', stationId: 1, model: 'BR0.35-1.6', manufacturer: '阿法拉伐', status: 'running', maintenanceStatus: 'normal' },
  { id: 5, equipmentCode: 'EQ005', equipmentName: '2号换热器', equipmentType: 'heat_exchanger', stationId: 2, model: 'BR0.35-1.6', manufacturer: '阿法拉伐', status: 'running', maintenanceStatus: 'warning' }
];
let equipmentNextId = 6;

// 锅炉数据
let boilers = [
  { id: 1, boilerCode: 'BLR001', boilerName: '1号燃气锅炉', boilerType: 'gas', manufacturer: '某锅炉厂', model: 'WNS10-1.0', ratedCapacity: 10, ratedPressure: 1.0, designEfficiency: 0.95, status: 1 },
  { id: 2, boilerCode: 'BLR002', boilerName: '2号燃气锅炉', boilerType: 'gas', manufacturer: '某锅炉厂', model: 'WNS10-1.0', ratedCapacity: 10, ratedPressure: 1.0, designEfficiency: 0.95, status: 1 },
  { id: 3, boilerCode: 'BLR003', boilerName: '3号燃气锅炉', boilerType: 'gas', manufacturer: '某锅炉厂', model: 'WNS15-1.0', ratedCapacity: 15, ratedPressure: 1.0, designEfficiency: 0.94, status: 0 }
];

// 气候补偿配置
let climateConfigs = [
  { id: 1, configName: '标准气候补偿曲线', stationId: 1, compensationType: 'climate', kp: 5.0, ki: 0.5, kd: 1.0, setpoint: 20, outputMin: 35, outputMax: 75, status: 1 },
  { id: 2, configName: '节能气候补偿', stationId: 2, compensationType: 'climate', kp: 4.0, ki: 0.4, kd: 0.8, setpoint: 20, outputMin: 30, outputMax: 70, status: 1 }
];
let climateConfigNextId = 3;

// 气候补偿曲线
let climateCurves = [
  { id: 1, curveName: '标准曲线', outdoorTempMin: -20, outdoorTempMax: 10, supplyTempTarget: 75, returnTempTarget: 55, status: 1 },
  { id: 2, curveName: '节能曲线', outdoorTempMin: -15, outdoorTempMax: 5, supplyTempTarget: 70, returnTempTarget: 50, status: 1 }
];

// 阀门数据
let valves = [
  { id: 1, valveCode: 'V001', valveName: '1号楼入口调节阀', valveType: 'control', nodeId: 1, manufacturer: '西门子', model: 'VVF40', diameter: 50, openRatio: 0.65, status: 1 },
  { id: 2, valveCode: 'V002', valveName: '2号楼入口调节阀', valveType: 'control', nodeId: 2, manufacturer: '西门子', model: 'VVF40', diameter: 50, openRatio: 0.72, status: 1 },
  { id: 3, valveCode: 'V003', valveName: '3号楼入口调节阀', valveType: 'control', nodeId: 3, manufacturer: '西门子', model: 'VVF40', diameter: 65, openRatio: 0.58, status: 1 },
  { id: 4, valveCode: 'V004', valveName: '支路隔离阀1', valveType: 'isolation', nodeId: 4, manufacturer: '丹佛斯', model: 'EV220B', diameter: 40, openRatio: 1.0, status: 1 }
];
let valveNextId = 5;

// 平衡策略
let balanceStrategies = [
  { id: 1, strategyName: '自适应平衡策略', strategyType: 'adaptive', targetDelta: 5, executionFrequency: 4, status: 'active' },
  { id: 2, strategyName: '固定开度策略', strategyType: 'fixed', targetDelta: 8, executionFrequency: 1, status: 'draft' }
];
let strategyNextId = 3;

// 能耗数据
let energyCosts = [
  { id: 1, enterpriseName: '阳光花园小区', recordDate: '2024-01', steamAmount: 120, waterAmount: 80, electricityAmount: 15000, standardCoal: 45, totalCost: 85000 },
  { id: 2, enterpriseName: '商业大厦A座', recordDate: '2024-01', steamAmount: 200, waterAmount: 120, electricityAmount: 25000, standardCoal: 75, totalCost: 145000 },
  { id: 3, enterpriseName: '科技园区', recordDate: '2024-01', steamAmount: 300, waterAmount: 180, electricityAmount: 38000, standardCoal: 110, totalCost: 210000 }
];

// 仿真节点数据
let simulationNodes = [
  { id: 1, nodeCode: 'N001', nodeName: '换热站出口', nodeType: 'source', xCoord: 116.4, yCoord: 39.9, pressure: 0.45, flowRate: 800 },
  { id: 2, nodeCode: 'N002', nodeName: '1号楼入口', nodeType: 'building', xCoord: 116.41, yCoord: 39.91, pressure: 0.42, flowRate: 120 },
  { id: 3, nodeCode: 'N003', nodeName: '2号楼入口', nodeType: 'building', xCoord: 116.42, yCoord: 39.92, pressure: 0.40, flowRate: 150 },
  { id: 4, nodeCode: 'N004', nodeName: '3号楼入口', nodeType: 'building', xCoord: 116.43, yCoord: 39.93, pressure: 0.38, flowRate: 200 }
];

// ============ 辅助函数 ============

function paginate(data, page, size) {
  const start = (page - 1) * size;
  const end = start + parseInt(size);
  return {
    data: data.slice(start, end),
    total: data.length,
    pages: Math.ceil(data.length / size),
    current: parseInt(page)
  };
}

// ============ 仪表盘 API ============

app.get('/api/dashboard/stats', (req, res) => {
  res.json({
    stationCount: stations.filter(s => s.status === 1).length,
    buildingCount: buildings.length,
    userCount: heatUsers.length,
    alarmCount: alarms.filter(a => !a.acknowledged).length
  });
});

app.get('/api/dashboard/temperature-trend', (req, res) => {
  const hours = ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00'];
  res.json({
    primarySupply: hours.map(() => random(115, 125)),
    primaryReturn: hours.map(() => random(65, 75)),
    secondarySupply: hours.map(() => random(45, 55)),
    secondaryReturn: hours.map(() => random(35, 45))
  });
});

app.get('/api/dashboard/station-status', (req, res) => {
  res.json([
    { value: stations.filter(s => s.status === 1).length, name: '运行中' },
    { value: stations.filter(s => s.status === 0).length, name: '停止' },
    { value: randomInt(0, 2), name: '故障' }
  ]);
});

app.get('/api/dashboard/heat-load', (req, res) => {
  const hours = ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00'];
  res.json(hours.map(() => random(40, 70)));
});

app.get('/api/dashboard/alarms', (req, res) => {
  res.json(alarms.slice(0, 10));
});

// ============ 换热站 API ============

app.get('/api/station', (req, res) => {
  const { page = 1, size = 10 } = req.query;
  res.json(paginate(stations, page, size));
});

app.get('/api/station/all', (req, res) => {
  res.json(stations);
});

app.get('/api/station/:id', (req, res) => {
  const station = stations.find(s => s.id === parseInt(req.params.id));
  if (station) {
    res.json(station);
  } else {
    res.status(404).json({ error: '换热站不存在' });
  }
});

app.post('/api/station', (req, res) => {
  const station = { ...req.body, id: stationNextId++ };
  stations.push(station);
  res.json({ success: true, id: station.id });
});

app.put('/api/station/:id', (req, res) => {
  const index = stations.findIndex(s => s.id === parseInt(req.params.id));
  if (index !== -1) {
    stations[index] = { ...stations[index], ...req.body };
    res.json({ success: true });
  } else {
    res.status(404).json({ error: '换热站不存在' });
  }
});

app.delete('/api/station/:id', (req, res) => {
  const index = stations.findIndex(s => s.id === parseInt(req.params.id));
  if (index !== -1) {
    stations.splice(index, 1);
    res.json({ success: true });
  } else {
    res.status(404).json({ error: '换热站不存在' });
  }
});

app.get('/api/station/:id/realtime-data', (req, res) => {
  const stationId = parseInt(req.params.id);
  res.json({
    stationId,
    primarySupplyTemp: random(115, 125),
    primaryReturnTemp: random(65, 75),
    secondarySupplyTemp: random(45, 55),
    secondaryReturnTemp: random(35, 45),
    primaryFlow: random(400, 600),
    secondaryFlow: random(350, 550),
    pumpSpeedRatio: random(0.6, 0.9),
    valveOpenRatio: random(0.5, 0.8),
    collectTime: formatDate(0)
  });
});

app.get('/api/station/:id/pid-config', (req, res) => {
  res.json({
    stationId: parseInt(req.params.id),
    tempKp: 5.0, tempKi: 0.5, tempKd: 1.0,
    pressureKp: 3.0, pressureKi: 0.3, pressureKd: 0.5,
    flowKp: 4.0, flowKi: 0.4, flowKd: 0.8
  });
});

app.put('/api/station/:id/pid-config', (req, res) => {
  res.json({ success: true });
});

app.get('/api/station/:id/history', (req, res) => {
  const hours = ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00'];
  res.json({
    dates: hours,
    temps: hours.map(() => random(45, 55)),
    pressures: hours.map(() => random(0.3, 0.5)),
    flows: hours.map(() => random(400, 600))
  });
});

// ============ 建筑物 API ============

app.get('/api/heat-user/building', (req, res) => {
  const { page = 1, size = 10 } = req.query;
  res.json(paginate(buildings, page, size));
});

app.get('/api/heat-user/building/:id', (req, res) => {
  const building = buildings.find(b => b.id === parseInt(req.params.id));
  if (building) {
    res.json(building);
  } else {
    res.status(404).json({ error: '建筑物不存在' });
  }
});

app.get('/api/heat-user/building/station/:stationId', (req, res) => {
  const stationBuildings = buildings.filter(b => b.stationId === parseInt(req.params.stationId));
  res.json(stationBuildings);
});

app.post('/api/heat-user/building', (req, res) => {
  const building = { ...req.body, id: buildingNextId++ };
  buildings.push(building);
  res.json({ success: true, id: building.id });
});

app.put('/api/heat-user/building/:id', (req, res) => {
  const index = buildings.findIndex(b => b.id === parseInt(req.params.id));
  if (index !== -1) {
    buildings[index] = { ...buildings[index], ...req.body };
    res.json({ success: true });
  } else {
    res.status(404).json({ error: '建筑物不存在' });
  }
});

app.delete('/api/heat-user/building/:id', (req, res) => {
  const index = buildings.findIndex(b => b.id === parseInt(req.params.id));
  if (index !== -1) {
    buildings.splice(index, 1);
    res.json({ success: true });
  } else {
    res.status(404).json({ error: '建筑物不存在' });
  }
});

// ============ 热用户 API ============

app.get('/api/heat-user', (req, res) => {
  const { page = 1, size = 10 } = req.query;
  res.json(paginate(heatUsers, page, size));
});

app.get('/api/heat-user/:id', (req, res) => {
  const user = heatUsers.find(u => u.id === parseInt(req.params.id));
  if (user) {
    res.json(user);
  } else {
    res.status(404).json({ error: '热用户不存在' });
  }
});

app.get('/api/heat-user/building/:buildingId', (req, res) => {
  const buildingUsers = heatUsers.filter(u => u.buildingId === parseInt(req.params.buildingId));
  res.json(buildingUsers);
});

app.post('/api/heat-user', (req, res) => {
  const user = { ...req.body, id: userNextId++ };
  heatUsers.push(user);
  res.json({ success: true, id: user.id });
});

app.put('/api/heat-user/:id', (req, res) => {
  const index = heatUsers.findIndex(u => u.id === parseInt(req.params.id));
  if (index !== -1) {
    heatUsers[index] = { ...heatUsers[index], ...req.body };
    res.json({ success: true });
  } else {
    res.status(404).json({ error: '热用户不存在' });
  }
});

app.delete('/api/heat-user/:id', (req, res) => {
  const index = heatUsers.findIndex(u => u.id === parseInt(req.params.id));
  if (index !== -1) {
    heatUsers.splice(index, 1);
    res.json({ success: true });
  } else {
    res.status(404).json({ error: '热用户不存在' });
  }
});

// ============ 告警 API ============

app.get('/api/alarm', (req, res) => {
  const { page = 1, size = 10, acknowledged } = req.query;
  let filteredAlarms = alarms;
  if (acknowledged !== undefined) {
    filteredAlarms = alarms.filter(a => a.acknowledged === parseInt(acknowledged));
  }
  res.json(paginate(filteredAlarms, page, size));
});

app.post('/api/alarm/:id/acknowledge', (req, res) => {
  const index = alarms.findIndex(a => a.id === parseInt(req.params.id));
  if (index !== -1) {
    alarms[index].acknowledged = 1;
    res.json({ success: true });
  } else {
    res.status(404).json({ error: '告警不存在' });
  }
});

app.put('/api/alarm/:id/process', (req, res) => {
  res.json({ success: true });
});

// ============ 设备 API ============

app.get('/api/equipment', (req, res) => {
  const { page = 1, size = 10 } = req.query;
  res.json(paginate(equipment, page, size));
});

app.get('/api/equipment/maintenance-reminders', (req, res) => {
  res.json([
    { id: 1, equipmentName: '1号循环泵', maintenanceType: '定期保养', nextDate: formatDate(30), status: 'pending' },
    { id: 2, equipmentName: '2号换热器', maintenanceType: '清洗检查', nextDate: formatDate(15), status: 'pending' }
  ]);
});

app.get('/api/equipment/type-distribution', (req, res) => {
  res.json([
    { value: equipment.filter(e => e.equipmentType === 'pump').length, name: '循环泵' },
    { value: equipment.filter(e => e.equipmentType === 'heat_exchanger').length, name: '换热器' },
    { value: 2, name: '阀门' }
  ]);
});

app.post('/api/equipment', (req, res) => {
  const eq = { ...req.body, id: equipmentNextId++ };
  equipment.push(eq);
  res.json({ success: true, id: eq.id });
});

app.put('/api/equipment/:id', (req, res) => {
  const index = equipment.findIndex(e => e.id === parseInt(req.params.id));
  if (index !== -1) {
    equipment[index] = { ...equipment[index], ...req.body };
    res.json({ success: true });
  } else {
    res.status(404).json({ error: '设备不存在' });
  }
});

// ============ 热源 API ============

app.get('/api/heat-source/boilers', (req, res) => {
  res.json(boilers);
});

app.get('/api/heat-source/load-prediction', (req, res) => {
  const days = ['今天', '明天', '后天'];
  res.json({
    currentLoad: random(40, 60).toFixed(1),
    maxLoad: random(55, 75).toFixed(1),
    avgLoad: random(40, 55).toFixed(1),
    accuracy: random(90, 98).toFixed(1),
    forecastData: days.map(day => ({
      date: day,
      predictedLoad: random(40, 80),
      confidence: random(0.7, 0.95)
    }))
  });
});

app.get('/api/heat-source/efficiency-comparison', (req, res) => {
  res.json([
    { name: '1号锅炉', efficiency: random(92, 97), loadRate: random(50, 95) },
    { name: '2号锅炉', efficiency: random(90, 95), loadRate: random(40, 85) }
  ]);
});

app.get('/api/heat-source/model-info', (req, res) => {
  res.json({
    modelType: 'LSTM',
    modelVersion: 'v1.0.0',
    accuracy: 0.9235,
    trainDate: '2024-01-15'
  });
});

app.post('/api/heat-source/adjust', (req, res) => {
  res.json({ success: true, newLoad: random(40, 80) });
});

// ============ 锅炉 API ============

app.get('/api/boiler', (req, res) => {
  res.json(boilers);
});

app.put('/api/boiler/:id', (req, res) => {
  const index = boilers.findIndex(b => b.id === parseInt(req.params.id));
  if (index !== -1) {
    boilers[index] = { ...boilers[index], ...req.body };
    res.json({ success: true });
  } else {
    res.status(404).json({ error: '锅炉不存在' });
  }
});

// ============ 气候补偿 API ============

app.get('/api/climate/statistics', (req, res) => {
  res.json({
    outdoorTemp: random(-15, 10).toFixed(1),
    supplyTemp: random(40, 60).toFixed(1),
    savingsRate: random(8, 18).toFixed(1),
    compensationMode: 'climate'
  });
});

app.get('/api/climate/configs', (req, res) => {
  const { page = 1, size = 10 } = req.query;
  res.json(paginate(climateConfigs, page, size));
});

app.get('/api/climate/configs/:id', (req, res) => {
  const config = climateConfigs.find(c => c.id === parseInt(req.params.id));
  if (config) {
    res.json(config);
  } else {
    res.status(404).json({ error: '配置不存在' });
  }
});

app.post('/api/climate/configs', (req, res) => {
  const config = { ...req.body, id: climateConfigNextId++ };
  climateConfigs.push(config);
  res.json({ success: true, id: config.id });
});

app.put('/api/climate/configs/:id', (req, res) => {
  const index = climateConfigs.findIndex(c => c.id === parseInt(req.params.id));
  if (index !== -1) {
    climateConfigs[index] = { ...climateConfigs[index], ...req.body };
    res.json({ success: true });
  } else {
    res.status(404).json({ error: '配置不存在' });
  }
});

app.delete('/api/climate/configs/:id', (req, res) => {
  const index = climateConfigs.findIndex(c => c.id === parseInt(req.params.id));
  if (index !== -1) {
    climateConfigs.splice(index, 1);
    res.json({ success: true });
  } else {
    res.status(404).json({ error: '配置不存在' });
  }
});

app.get('/api/climate/curves', (req, res) => {
  const outdoorTemps = [-20, -15, -10, -5, 0, 5, 10];
  res.json({
    temps: outdoorTemps,
    supplyTemp: outdoorTemps.map(t => Math.max(35, 75 + t * 2)),
    returnTemp: outdoorTemps.map(t => Math.max(25, 55 + t * 1.5))
  });
});

app.get('/api/climate/effects', (req, res) => {
  const days = ['周一', '周二', '周三', '周四', '周五', '周六', '周日'];
  res.json({
    days,
    before: days.map(() => random(40, 60)),
    after: days.map(() => random(35, 55))
  });
});

// ============ 能效分析 API ============

app.get('/api/efficiency/overview', (req, res) => {
  res.json({
    heatEfficiency: random(85, 95).toFixed(1),
    energyConsumption: random(100, 200).toFixed(1),
    costPerUnit: random(20, 40).toFixed(1),
    comparisonData: [
      { name: '本季度', value: random(85, 95) },
      { name: '上季度', value: random(80, 90) }
    ]
  });
});

app.get('/api/efficiency/ranking', (req, res) => {
  res.json(buildings.slice(0, 4).map((b, i) => ({
    buildingName: b.buildingName,
    efficiency: random(85, 98).toFixed(1),
    rank: i + 1
  })));
});

app.get('/api/efficiency/terminal-monitor', (req, res) => {
  res.json(heatUsers.slice(0, 5).map(u => ({
    userName: u.userName,
    currentTemp: random(18, 24).toFixed(1),
    targetTemp: u.targetTemp,
    status: random(0, 1) > 0.2 ? '正常' : '异常'
  })));
});

app.get('/api/efficiency/comparison', (req, res) => {
  res.json({
    labels: ['1月', '2月', '3月', '4月'],
    energy: [random(100, 150), random(90, 140), random(80, 130), random(70, 120)],
    cost: [random(20, 40), random(18, 38), random(16, 36), random(14, 34)]
  });
});

app.get('/api/efficiency/trend', (req, res) => {
  const days = ['周一', '周二', '周三', '周四', '周五', '周六', '周日'];
  res.json({
    days,
    efficiency: days.map(() => random(85, 95)),
    consumption: days.map(() => random(100, 200))
  });
});

app.get('/api/efficiency/heating-curve', (req, res) => {
  const outdoorTemps = [-20, -15, -10, -5, 0, 5, 10];
  res.json({
    outdoorTemps,
    targetSupplyTemps: outdoorTemps.map(t => Math.max(35, 75 + t * 1.5)),
    targetReturnTemps: outdoorTemps.map(t => Math.max(25, 55 + t * 1.2))
  });
});

app.post('/api/efficiency/resident-temperature', (req, res) => {
  res.json({ success: true });
});

app.post('/api/efficiency/apply-curve/:stationId', (req, res) => {
  res.json({ success: true });
});

// ============ 风险评估 API ============

app.get('/api/risk/index', (req, res) => {
  res.json({
    overallRisk: random(0, 0.3).toFixed(2),
    riskLevel: random(0, 0.3) > 0.2 ? 'low' : 'medium',
    trends: [
      { date: '周一', value: random(0.1, 0.3) },
      { date: '周二', value: random(0.1, 0.3) },
      { date: '今天', value: random(0.1, 0.3) }
    ]
  });
});

app.get('/api/risk/dimension', (req, res) => {
  res.json([
    { dimension: '设备老化', weight: 0.3, risk: random(0.2, 0.5) },
    { dimension: '管网泄漏', weight: 0.25, risk: random(0.1, 0.4) },
    { dimension: '温度波动', weight: 0.25, risk: random(0.2, 0.5) },
    { dimension: '压力异常', weight: 0.2, risk: random(0.1, 0.3) }
  ]);
});

app.get('/api/risk/details', (req, res) => {
  res.json([
    { id: 1, riskType: '设备老化', riskLevel: '中等', description: '1号循环泵运行年限较长', suggestion: '计划更换' },
    { id: 2, riskType: '管网泄漏', riskLevel: '较低', description: '部分管网存在轻微泄漏', suggestion: '加强巡检' }
  ]);
});

app.get('/api/risk/trend', (req, res) => {
  const days = ['周一', '周二', '周三', '周四', '周五', '周六', '周日'];
  res.json({
    days,
    values: days.map(() => random(0.1, 0.4))
  });
});

app.get('/api/risk/distribution', (req, res) => {
  res.json([
    { area: '东城区', risk: random(0.1, 0.3) },
    { area: '西城区', risk: random(0.1, 0.35) },
    { area: '朝阳区', risk: random(0.15, 0.4) }
  ]);
});

app.get('/api/risk/alert', (req, res) => {
  res.json([
    { id: 1, level: 'warning', message: '2号楼管网压力异常', time: formatDate(-2) },
    { id: 2, level: 'info', message: '设备保养提醒', time: formatDate(-24) }
  ]);
});

app.put('/api/risk/alert/handle/:id', (req, res) => {
  res.json({ success: true });
});

app.get('/api/risk/report', (req, res) => {
  res.json({
    reportType: 'daily',
    generateTime: formatDate(0),
    riskOverview: random(0.1, 0.3).toFixed(2),
    mainRisks: [
      { type: '设备老化', count: 3 },
      { type: '管网泄漏', count: 2 }
    ]
  });
});

// ============ 按需供热 API ============

app.get('/api/demand-heating/stats', (req, res) => {
  res.json({
    totalUsers: heatUsers.length,
    activeUsers: heatUsers.filter(u => u.status === 1).length,
    avgTemp: random(19, 22).toFixed(1),
    demandRate: random(0.8, 1.2).toFixed(2)
  });
});

app.get('/api/demand-heating/temperature', (req, res) => {
  res.json(heatUsers.map(u => ({
    userId: u.id,
    userName: u.userName,
    currentTemp: random(18, 24).toFixed(1),
    targetTemp: u.targetTemp,
    collectTime: formatDate(0)
  })));
});

app.put('/api/demand-heating/user/:userId/target-temp', (req, res) => {
  const index = heatUsers.findIndex(u => u.id === parseInt(req.params.userId));
  if (index !== -1) {
    heatUsers[index].targetTemp = req.body.targetTemp;
    res.json({ success: true });
  } else {
    res.status(404).json({ error: '用户不存在' });
  }
});

app.put('/api/demand-heating/user/:userId/valve', (req, res) => {
  res.json({ success: true });
});

app.get('/api/demand-heating/prediction/:userId', (req, res) => {
  const hours = ['当前', '+1h', '+2h', '+3h', '+4h'];
  res.json({
    userId: parseInt(req.params.userId),
    predictions: hours.map(h => random(18, 24).toFixed(1))
  });
});

app.get('/api/demand-heating/temperature-distribution', (req, res) => {
  res.json([
    { range: '<18℃', count: randomInt(5, 15) },
    { range: '18-20℃', count: randomInt(30, 50) },
    { range: '20-22℃', count: randomInt(40, 60) },
    { range: '22-24℃', count: randomInt(20, 40) },
    { range: '>24℃', count: randomInt(5, 15) }
  ]);
});

// ============ 阀门控制 API ============

app.get('/api/valve', (req, res) => {
  const { page = 1, size = 10 } = req.query;
  res.json(paginate(valves, page, size));
});

app.get('/api/valve/:id', (req, res) => {
  const valve = valves.find(v => v.id === parseInt(req.params.id));
  if (valve) {
    res.json(valve);
  } else {
    res.status(404).json({ error: '阀门不存在' });
  }
});

app.post('/api/valve/:id/control', (req, res) => {
  const index = valves.findIndex(v => v.id === parseInt(req.params.id));
  if (index !== -1) {
    valves[index].openRatio = req.body.openRatio;
    res.json({ success: true, openRatio: valves[index].openRatio });
  } else {
    res.status(404).json({ error: '阀门不存在' });
  }
});

app.get('/api/valve/:id/fuzzy-pid', (req, res) => {
  res.json({
    valveId: parseInt(req.params.id),
    kp: random(3, 6).toFixed(2),
    ki: random(0.1, 0.5).toFixed(2),
    kd: random(0.5, 1.5).toFixed(2),
    setpoint: random(0.6, 0.8).toFixed(2)
  });
});

app.put('/api/valve/:id/fuzzy-pid', (req, res) => {
  res.json({ success: true });
});

app.get('/api/valve/:id/history', (req, res) => {
  const hours = ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00'];
  res.json({
    dates: hours,
    openRatios: hours.map(() => random(0.4, 0.9)),
    pressures: hours.map(() => random(0.3, 0.5))
  });
});

// ============ 负荷预测 API ============

app.get('/api/load-forecast', (req, res) => {
  res.json({
    currentLoad: random(40, 60).toFixed(1),
    maxLoad: random(55, 75).toFixed(1),
    avgLoad: random(40, 55).toFixed(1),
    accuracy: random(90, 98).toFixed(1),
    forecastData: [
      { time: '现在', value: random(40, 60) },
      { time: '+1h', value: random(42, 62) },
      { time: '+2h', value: random(45, 65) },
      { time: '+3h', value: random(43, 63) }
    ]
  });
});

app.get('/api/load-forecast/station/:stationId', (req, res) => {
  const hours = ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00'];
  res.json({
    stationId: parseInt(req.params.stationId),
    forecastData: hours.map(h => ({
      time: h,
      predictedLoad: random(40, 70),
      confidence: random(0.7, 0.95)
    }))
  });
});

app.get('/api/load-forecast/details', (req, res) => {
  res.json({
    hourlyData: [
      { hour: '00:00', load: random(40, 60), predicted: random(38, 62) },
      { hour: '04:00', load: random(35, 55), predicted: random(33, 57) },
      { hour: '08:00', load: random(50, 70), predicted: random(48, 72) },
      { hour: '12:00', load: random(55, 75), predicted: random(53, 77) },
      { hour: '16:00', load: random(50, 70), predicted: random(48, 72) },
      { hour: '20:00', load: random(45, 65), predicted: random(43, 67) }
    ]
  });
});

// ============ 气象数据 API ============

app.get('/api/meteorology/current', (req, res) => {
  res.json({
    temperature: random(-15, 10).toFixed(1),
    humidity: randomInt(30, 70),
    windSpeed: random(1, 8).toFixed(1),
    weather: '多云',
    pressure: random(1010, 1025).toFixed(0),
    updateTime: formatDate(0)
  });
});

app.get('/api/meteorology/forecast', (req, res) => {
  res.json({
    hours: Array.from({ length: 24 }, (_, i) => ({
      time: `${String(i).padStart(2, '0')}:00`,
      temp: random(-15, 10).toFixed(1),
      weather: ['晴', '多云', '阴', '小雪'][randomInt(0, 3)],
      windSpeed: `${randomInt(1, 6)}级`
    }))
  });
});

app.get('/api/meteorology/history', (req, res) => {
  const days = ['周一', '周二', '周三', '周四', '周五', '周六', '周日'];
  res.json({
    days,
    temps: days.map(() => random(-10, 5).toFixed(1)),
    loads: days.map(() => random(40, 65).toFixed(1))
  });
});

app.get('/api/meteorology/warnings', (req, res) => {
  res.json([
    { level: 'blue', message: '未来12小时气温骤降', time: formatDate(-1) },
    { level: 'yellow', message: '大风蓝色预警', time: formatDate(-3) }
  ]);
});

// ============ 平衡策略 API ============

app.get('/api/balance/strategies', (req, res) => {
  const { page = 1, size = 10 } = req.query;
  res.json(paginate(balanceStrategies, page, size));
});

app.post('/api/balance/strategies/:id/execute', (req, res) => {
  res.json({ success: true, executionId: randomInt(1000, 9999) });
});

app.get('/api/balance/execution-records', (req, res) => {
  res.json([
    { id: 1, strategyName: '自适应平衡策略', executeTime: formatDate(-2), status: 'success', result: '优化完成' },
    { id: 2, strategyName: '固定开度策略', executeTime: formatDate(-24), status: 'success', result: '阀门调整完成' }
  ]);
});

// ============ 趋势分析 API ============

app.get('/api/trend/energy-rank', (req, res) => {
  res.json(buildings.slice(0, 4).map((b, i) => ({
    buildingName: b.buildingName,
    consumption: random(1000, 5000),
    rank: i + 1
  })));
});

app.get('/api/trend/anomaly', (req, res) => {
  res.json([
    { id: 1, type: '温度异常', buildingName: '1号楼', time: formatDate(-2), severity: 'warning' },
    { id: 2, type: '流量异常', buildingName: '2号楼', time: formatDate(-5), severity: 'info' }
  ]);
});

app.get('/api/trend/reports', (req, res) => {
  res.json([
    { id: 1, reportName: '日报-2024-01-15', generateTime: formatDate(-24), type: 'daily' },
    { id: 2, reportName: '周报-第三周', generateTime: formatDate(-72), type: 'weekly' }
  ]);
});

// ============ 能耗分析 API ============

app.get('/api/cost/rank', (req, res) => {
  res.json(energyCosts.map((c, i) => ({
    enterpriseName: c.enterpriseName,
    totalCost: c.totalCost,
    rank: i + 1
  })));
});

app.get('/api/cost/list', (req, res) => {
  const { page = 1, size = 10 } = req.query;
  res.json(paginate(energyCosts, page, size));
});

app.get('/api/cost/trend', (req, res) => {
  const months = ['1月', '2月', '3月', '4月', '5月', '6月'];
  res.json({
    months,
    energyCosts: months.map(() => random(80000, 150000)),
    waterCosts: months.map(() => random(10000, 20000)),
    electricityCosts: months.map(() => random(20000, 40000))
  });
});

// ============ 仿真 API ============

app.get('/api/simulation/nodes', (req, res) => {
  const { page = 1, size = 10 } = req.query;
  res.json(paginate(simulationNodes, page, size));
});

app.post('/api/simulation/hydraulic', (req, res) => {
  res.json({
    simulationId: randomInt(1000, 9999),
    status: 'completed',
    flowRate: random(100, 500),
    pressureLoss: random(0.1, 0.5),
    results: simulationNodes.map(n => ({
      nodeCode: n.nodeCode,
      pressure: random(0.3, 0.5),
      flowRate: random(50, 200)
    }))
  });
});

app.get('/api/simulation/results', (req, res) => {
  res.json({
    simulationId: randomInt(1000, 9999),
    status: 'completed',
    nodes: simulationNodes.map(n => ({
      nodeCode: n.nodeCode,
      pressure: random(0.3, 0.5),
      velocity: random(0.5, 2.0)
    }))
  });
});

// ============ 异常预测 API ============

app.get('/api/anomaly/monitor-points', (req, res) => {
  res.json([
    { id: 1, deviceId: 'T001', deviceName: '1号楼温度传感器', deviceType: 'temperature_sensor', status: 'normal' },
    { id: 2, deviceId: 'P001', deviceName: '1号楼压力传感器', deviceType: 'pressure_sensor', status: 'normal' },
    { id: 3, deviceId: 'F001', deviceName: '主管道流量计', deviceType: 'flow_meter', status: 'warning' }
  ]);
});

app.get('/api/anomaly/predictions', (req, res) => {
  res.json({
    pendingCount: randomInt(3, 10),
    highProbaCount: randomInt(1, 5),
    accuracy: random(85, 95).toFixed(1),
    avgLeadTime: `${random(1, 4).toFixed(1)}小时`,
    predictions: [
      { id: 1, type: '温度异常', location: '1号楼101室', anomalyProb: randomInt(50, 95), occurTime: formatDate(2), severity: randomInt(1, 3) },
      { id: 2, type: '压力异常', location: '2号楼入口', anomalyProb: randomInt(40, 85), occurTime: formatDate(4), severity: randomInt(1, 3) },
      { id: 3, type: '流量异常', location: '3号楼支路', anomalyProb: randomInt(30, 75), occurTime: formatDate(6), severity: randomInt(1, 3) }
    ]
  });
});

app.get('/api/anomaly/predictions/:id', (req, res) => {
  res.json({
    id: parseInt(req.params.id),
    type: '温度异常',
    location: '1号楼101室',
    anomalyProb: randomInt(50, 95),
    occurTime: formatDate(2),
    severity: randomInt(1, 3),
    description: '预计未来2小时内温度将低于设定值',
    advice: '检查供热系统是否正常'
  });
});

app.post('/api/anomaly/predictions/:id/acknowledge', (req, res) => {
  res.json({ success: true });
});

// ============ 场景优化 API ============

app.get('/api/scenario/scenarios', (req, res) => {
  res.json([
    { id: 1, scenarioName: '节能模式', description: '优先节能的供热策略', expectedSavings: '15%', constraints: ['温度>=18℃', '压力>=0.3MPa'] },
    { id: 2, scenarioName: '舒适模式', description: '优先用户舒适度', expectedSavings: '5%', constraints: ['温度>=20℃', '压力>=0.4MPa'] },
    { id: 3, scenarioName: '经济模式', description: '最小化运行成本', expectedSavings: '20%', constraints: ['温度>=18℃', '压力>=0.35MPa'] }
  ]);
});

app.get('/api/scenario/objectives', (req, res) => {
  res.json([
    { id: 1, objectiveName: '能耗最低', weight: 0.6, unit: 'kWh' },
    { id: 2, objectiveName: '舒适度最高', weight: 0.3, unit: '%' },
    { id: 3, objectiveName: '成本最低', weight: 0.1, unit: '元' }
  ]);
});

app.post('/api/scenario/optimize', (req, res) => {
  res.json({
    success: true,
    optimalScenario: '节能模式',
    expectedSavings: '15%',
    optimizationResults: {
      supplyTemp: random(45, 55).toFixed(1),
      flowRate: random(400, 600).toFixed(1),
      pumpSpeed: random(0.6, 0.9).toFixed(2)
    }
  });
});

// ============ 蒸汽管理 API ============

app.get('/api/steam/management', (req, res) => {
  res.json({
    steamPressure: random(0.4, 0.6).toFixed(2),
    steamFlow: random(10, 50).toFixed(1),
    steamTemperature: random(150, 180).toFixed(1),
    waterConsumption: random(5, 20).toFixed(1),
    efficiency: random(90, 98).toFixed(1),
    quality: random(95, 100).toFixed(1)
  });
});

// ============ 二次网 API ============

app.get('/api/secondary-network/balance', (req, res) => {
  res.json({
    balanceDegree: random(85, 98).toFixed(1),
    diagnosisStatus: '正常',
    nodes: buildings.slice(0, 4).map(b => ({
      buildingName: b.buildingName,
      pressure: random(0.3, 0.55).toFixed(3),
      designPressure: 0.40,
      status: '正常'
    }))
  });
});

// ============ 启动服务器 ============

app.listen(PORT, '0.0.0.0', () => {
  console.log(`Mock API server running on port ${PORT}`);
});
