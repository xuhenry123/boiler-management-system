const express = require('express');
const app = express();
const PORT = 8080;

function random(min, max) {
  return Math.random() * (max - min) + min;
}

function randomInt(min, max) {
  return Math.floor(random(min, max + 1));
}

app.use(express.json());

// 建筑物数据存储
let buildings = [
  { id: 1, buildingCode: 'BLD001', buildingName: '阳光花园小区1号楼', address: '东城区阳光路1号', areaHeated: 12000, buildingType: 'residential', heatTransferCoefficient: 1.2, status: 1 },
  { id: 2, buildingCode: 'BLD002', buildingName: '阳光花园小区2号楼', address: '东城区阳光路2号', areaHeated: 15000, buildingType: 'residential', heatTransferCoefficient: 1.15, status: 1 },
  { id: 3, buildingCode: 'BLD003', buildingName: '商业大厦A座', address: '西城区金融街8号', areaHeated: 20000, buildingType: 'commercial', heatTransferCoefficient: 0.9, status: 1 }
];
let nextId = 4;

// 建筑物列表
app.get('/api/heat-user/building', (req, res) => {
  const { page = 1, size = 10 } = req.query;
  const start = (page - 1) * size;
  const end = start + parseInt(size);
  const pagedBuildings = buildings.slice(start, end);
  res.json({
    data: pagedBuildings,
    total: buildings.length,
    pages: Math.ceil(buildings.length / size),
    current: parseInt(page)
  });
});

// 获取单个建筑物
app.get('/api/heat-user/building/:id', (req, res) => {
  const building = buildings.find(b => b.id === parseInt(req.params.id));
  if (building) {
    res.json(building);
  } else {
    res.status(404).json({ error: '建筑物不存在' });
  }
});

// 创建建筑物
app.post('/api/heat-user/building', (req, res) => {
  const building = { ...req.body, id: nextId++ };
  buildings.push(building);
  res.json({ success: true, id: building.id });
});

// 更新建筑物
app.put('/api/heat-user/building/:id', (req, res) => {
  const index = buildings.findIndex(b => b.id === parseInt(req.params.id));
  if (index !== -1) {
    buildings[index] = { ...buildings[index], ...req.body };
    res.json({ success: true });
  } else {
    res.status(404).json({ error: '建筑物不存在' });
  }
});

// 删除建筑物
app.delete('/api/heat-user/building/:id', (req, res) => {
  const index = buildings.findIndex(b => b.id === parseInt(req.params.id));
  if (index !== -1) {
    buildings.splice(index, 1);
    res.json({ success: true });
  } else {
    res.status(404).json({ error: '建筑物不存在' });
  }
});

app.get('/api/dashboard/stats', (req, res) => {
  res.json({
    stationCount: randomInt(10, 30),
    buildingCount: randomInt(50, 200),
    userCount: randomInt(500, 2000),
    alarmCount: randomInt(0, 20)
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

app.get('/api/stations/status', (req, res) => {
  res.json([
    { value: randomInt(15, 25), name: '运行中' },
    { value: randomInt(1, 5), name: '停止' },
    { value: randomInt(0, 3), name: '故障' }
  ]);
});

app.get('/api/stations', (req, res) => {
  res.json([
    { id: 1, name: '换热站A', status: 'running', temperature: random(45, 55) },
    { id: 2, name: '换热站B', status: 'running', temperature: random(45, 55) },
    { id: 3, name: '换热站C', status: 'stopped', temperature: 0 }
  ]);
});

app.get('/api/buildings', (req, res) => {
  res.json([
    { id: 1, name: '1号楼', area: randomInt(5000, 10000), temperature: random(18, 24) },
    { id: 2, name: '2号楼', area: randomInt(5000, 10000), temperature: random(18, 24) },
    { id: 3, name: '3号楼', area: randomInt(5000, 10000), temperature: random(18, 24) }
  ]);
});

app.get('/api/alarms', (req, res) => {
  res.json([
    { id: 1, level: 'warning', message: '温度异常', time: new Date().toISOString() },
    { id: 2, level: 'info', message: '设备维护', time: new Date().toISOString() }
  ]);
});

app.get('/api/users', (req, res) => {
  res.json([
    { id: 1, name: '用户A', room: '101', temperature: random(18, 24) },
    { id: 2, name: '用户B', room: '102', temperature: random(18, 24) },
    { id: 3, name: '用户C', room: '103', temperature: random(18, 24) }
  ]);
});

app.get('/api/heat-load', (req, res) => {
  const hours = ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00'];
  res.json(hours.map(() => random(40, 70)));
});

app.get('/api/efficiency/indicators', (req, res) => {
  res.json({
    heatEfficiency: random(85, 95),
    energyConsumption: random(100, 200),
    costPerUnit: random(20, 40)
  });
});

app.get('/api/cost/summary', (req, res) => {
  res.json({
    totalCost: random(100000, 500000),
    energyCost: random(80000, 400000),
    maintenanceCost: random(10000, 50000)
  });
});

app.get('/api/climate/compensation', (req, res) => {
  res.json({
    outdoorTemp: random(-10, 10),
    supplyTemp: random(80, 120),
    compensationCurve: [115, 110, 105, 100, 95, 90].map(v => v + random(-5, 5))
  });
});

app.get('/api/primary-network/pressure', (req, res) => {
  res.json({
    supplyPressure: random(0.6, 0.8),
    returnPressure: random(0.3, 0.5),
    pressureDiff: random(0.2, 0.4)
  });
});

app.get('/api/secondary-network/pressure', (req, res) => {
  res.json({
    supplyPressure: random(0.4, 0.6),
    returnPressure: random(0.2, 0.4),
    pressureDiff: random(0.1, 0.3)
  });
});

app.get('/api/hydraulic/simulation', (req, res) => {
  res.json({
    flowRate: random(100, 500),
    pressureLoss: random(0.1, 0.5),
    status: 'balanced'
  });
});

app.get('/api/anomaly/predictions', (req, res) => {
  res.json([
    { id: 1, type: 'temperature', probability: random(0, 0.3), description: '温度异常预测' },
    { id: 2, type: 'pressure', probability: random(0, 0.2), description: '压力异常预测' }
  ]);
});

app.get('/api/load/forecast', (req, res) => {
  const days = ['今天', '明天', '后天'];
  res.json(days.map(day => ({
    date: day,
    predictedLoad: random(40, 80),
    confidence: random(0.7, 0.95)
  })));
});

app.get('/api/balance/strategy', (req, res) => {
  res.json({
    strategy: 'adaptive',
    adjustmentFrequency: randomInt(1, 4),
    targetDelta: random(5, 10)
  });
});

app.get('/api/cold-source/station', (req, res) => {
  res.json({
    status: 'running',
    cop: random(3, 5),
    power: random(100, 300)
  });
});

app.get('/api/heat-source/station', (req, res) => {
  res.json({
    status: 'running',
    efficiency: random(85, 95),
    output: random(50, 100)
  });
});

app.get('/api/valve/status', (req, res) => {
  res.json([
    { id: 1, name: '阀门A', position: randomInt(0, 100), status: 'normal' },
    { id: 2, name: '阀门B', position: randomInt(0, 100), status: 'normal' }
  ]);
});

app.get('/api/demand/heating', (req, res) => {
  res.json({
    currentDemand: random(40, 80),
    predictedDemand: random(45, 85),
    adjustment: random(-5, 5)
  });
});

app.get('/api/meteorology/data', (req, res) => {
  res.json({
    temperature: random(-10, 10),
    humidity: random(30, 70),
    windSpeed: random(0, 10),
    weather: '多云'
  });
});

app.get('/api/trend/analysis', (req, res) => {
  res.json({
    temperatureTrend: random(-2, 2),
    loadTrend: random(-5, 5),
    efficiencyTrend: random(-1, 1)
  });
});

app.get('/api/multi-scenario/optimization', (req, res) => {
  res.json({
    optimalScenario: 'energy-saving',
    expectedSavings: random(5, 15),
    constraints: ['temperature', 'pressure']
  });
});

app.get('/api/multi-energy/optimization', (req, res) => {
  res.json({
    electricPower: random(100, 200),
    thermalPower: random(200, 400),
    totalEfficiency: random(80, 90)
  });
});

app.get('/api/secondary-temp/control', (req, res) => {
  res.json({
    targetTemp: random(45, 55),
    currentTemp: random(44, 56),
    adjustment: random(-2, 2)
  });
});

app.get('/api/time-zone/control', (req, res) => {
  res.json({
    schedules: [
      { zone: '白天', start: '06:00', end: '22:00', targetTemp: 22 },
      { zone: '夜间', start: '22:00', end: '06:00', targetTemp: 18 }
    ]
  });
});

app.get('/api/risk/assessment', (req, res) => {
  res.json({
    overallRisk: random(0, 0.3),
    factors: [
      { name: '设备老化', risk: random(0, 0.5) },
      { name: '管网泄漏', risk: random(0, 0.3) },
      { name: '温度波动', risk: random(0, 0.4) }
    ]
  });
});

app.get('/api/equipment/status', (req, res) => {
  res.json([
    { id: 1, name: '循环泵A', status: 'running', vibration: random(0, 5) },
    { id: 2, name: '循环泵B', status: 'standby', vibration: 0 },
    { id: 3, name: '补水泵', status: 'running', vibration: random(0, 3) }
  ]);
});

app.get('/api/steam/management', (req, res) => {
  res.json({
    steamPressure: random(0.4, 0.6),
    steamFlow: random(10, 50),
    quality: random(95, 100)
  });
});

app.listen(PORT, '0.0.0.0', () => {
  console.log(`Mock API server running on port ${PORT}`);
});
