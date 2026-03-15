// 模拟后端API服务
// 用于演示和开发测试

const MockAPI = {
  // 延迟模拟
  delay(ms = 500) {
    return new Promise(resolve => setTimeout(resolve, ms));
  },

  // 生成随机数据
  random(min, max) {
    return Math.random() * (max - min) + min;
  },

  // 仪表数据
  getDashboardStats() {
    return {
      stationCount: Math.floor(this.random(10, 30)),
      buildingCount: Math.floor(this.random(50, 200)),
      userCount: Math.floor(this.random(500, 2000)),
      alarmCount: Math.floor(this.random(0, 20))
    };
  },

  // 温度趋势数据
  getTemperatureTrend() {
    const hours = ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00'];
    return {
      primarySupply: hours.map(() => this.random(115, 125)),
      primaryReturn: hours.map(() => this.random(65, 75)),
      secondarySupply: hours.map(() => this.random(45, 55)),
      secondaryReturn: hours.map(() => this.random(35, 45))
    };
  },

  // 换热站状态
  getStationStatus() {
    return [
      { value: Math.floor(this.random(15, 25)), name: '运行中' },
      { value: Math.floor(this.random(1, 5)), name: '停止' },
      { value: Math.floor(this.random(0, 3)), name: '故障' }
    ];
  },

  // 热负荷数据
  getHeatLoad() {
    const hours = ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00'];
    return hours.map(() => this.random(40, 70));
  },

  // 告警数据
  getAlarms() {
    const types = ['温度异常', '设备故障', '压力异常', '流量异常', '通讯中断'];
    const levels = ['Info', 'Warning', 'Critical'];
    const messages = [
      '1号楼101室温度过低',
      '2号阀门通讯中断',
      '3号换热站压力异常',
      '5号楼入口流量偏低',
      '循环泵运行异常'
    ];
    
    return Array.from({ length: 10 }, (_, i) => ({
      id: i + 1,
      alarmType: types[Math.floor(this.random(0, types.length))],
      alarmMessage: messages[Math.floor(this.random(0, messages.length))],
      alarmLevel: levels[Math.floor(this.random(0, levels.length))],
      createTime: this.formatDate(Date.now() - Math.random() * 86400000)
    }));
  },

  // 热用户数据
  getHeatUsers() {
    const names = ['张三', '李四', '王五', '赵六', '钱七', '孙八', '周九', '吴十'];
    const buildings = ['阳光花园1号楼', '阳光花园2号楼', '阳光花园3号楼', '商业大厦A座', '科技园区办公楼'];
    
    return Array.from({ length: 20 }, (_, i) => ({
      id: i + 1,
      userCode: `U${String(i + 1).padStart(3, '0')}`,
      userName: names[Math.floor(this.random(0, names.length))],
      buildingName: buildings[Math.floor(this.random(0, buildings.length))],
      temperature: this.random(18, 24).toFixed(1),
      targetTemp: this.random(18, 24).toFixed(1),
      valveStatus: Math.random() > 0.2 ? 'on' : 'off',
      collectTime: this.formatDate(Date.now() - Math.random() * 3600000)
    }));
  },

  // 负荷预测数据
  getLoadForecast() {
    return {
      currentLoad: this.random(40, 60).toFixed(1),
      maxLoad: this.random(55, 75).toFixed(1),
      avgLoad: this.random(40, 55).toFixed(1),
      accuracy: this.random(90, 98).toFixed(1),
      forecastData: this.getForecastCurve('short'),
      errorData: this.getErrorData(),
      stationLoad: this.getStationLoadDistribution()
    };
  },

  getForecastCurve(type) {
    const data = {
      'ultra-short': 13,
      'short': 25,
      'medium': 169
    };
    const count = data[type] || 25;
    return Array.from({ length: count }, (_, i) => ({
      time: type === 'medium' ? `第${Math.floor(i / 24) + 1}周` : 
             `${String(Math.floor(i / 4)).padStart(2, '0')}:00`,
      actual: this.random(40, 60),
      predicted: this.random(38, 62)
    }));
  },

  getErrorData() {
    const hours = ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00'];
    return hours.map(() => this.random(-5, 5));
  },

  getStationLoadDistribution() {
    return [
      { value: this.random(10, 25), name: '东城区换热站' },
      { value: this.random(10, 25), name: '西城区换热站' },
      { value: this.random(10, 25), name: '朝阳区换热站' },
      { value: this.random(5, 15), name: '海淀区换热站' }
    ];
  },

  // 换热站数据
  getStationData(stationId) {
    return {
      id: stationId,
      primarySupplyTemp: this.random(110, 125).toFixed(1),
      primaryReturnTemp: this.random(65, 75).toFixed(1),
      secondarySupplyTemp: this.random(45, 55).toFixed(1),
      secondaryReturnTemp: this.random(35, 45).toFixed(1),
      primaryFlow: Math.floor(this.random(400, 600)),
      secondaryFlow: Math.floor(this.random(350, 550)),
      pumpSpeed: this.random(0.6, 0.9).toFixed(2),
      status: 'running'
    };
  },

  // 管网平衡数据
  getNetworkBalance() {
    const buildings = ['阳光花园1号楼', '阳光花园2号楼', '阳光花园3号楼', '商业大厦A座', '科技园区办公楼', '市政府大楼'];
    
    return {
      nodeCount: Math.floor(this.random(40, 60)),
      segmentCount: Math.floor(this.random(50, 70)),
      balanceDegree: this.random(85, 98).toFixed(1),
      diagnosisStatus: '正常',
      nodes: buildings.map(name => ({
        nodeName: name,
        pressure: this.random(0.3, 0.55).toFixed(3),
        designPressure: '0.40',
        status: Math.random() > 0.3 ? '正常' : '偏低'
      })),
      valves: buildings.map(name => ({
        valveName: `${name}入口调节阀`,
        buildingName: name,
        currentOpenRatio: this.random(0.5, 0.9),
        targetOpenRatio: this.random(0.6, 0.85),
        status: Math.random() > 0.5 ? 'optimal' : 'optimizing',
        statusText: Math.random() > 0.5 ? '已优化' : '待优化'
      }))
    };
  },

  // 气象数据
  getMeteorology() {
    return {
      outdoorTemp: this.random(-15, 10).toFixed(1),
      weatherType: ['晴', '多云', '阴', '小雪'][Math.floor(this.random(0, 4))],
      windSpeed: this.random(1, 8).toFixed(1),
      humidity: Math.floor(this.random(30, 70)),
      forecast: this.getWeatherForecast(),
      correlation: this.getWeatherCorrelation()
    };
  },

  getWeatherForecast() {
    return Array.from({ length: 72 }, (_, i) => ({
      time: `${String(i).padStart(2, '0')}:00`,
      temp: this.random(-15, 10).toFixed(1),
      weather: ['晴', '多云', '阴', '小雪'][Math.floor(this.random(0, 4))],
      wind: `${Math.floor(this.random(1, 6))}-${Math.floor(this.random(1, 6))}级`,
      humidity: `${Math.floor(this.random(40, 70))}%`
    }));
  },

  getWeatherCorrelation() {
    const days = ['周一', '周二', '周三', '周四', '周五', '周六', '周日'];
    return {
      dates: days,
      temps: days.map(() => this.random(-10, 5).toFixed(1)),
      loads: days.map(() => this.random(40, 65).toFixed(1))
    };
  },

  // 异常预测数据
  getAnomalyPredictions() {
    const types = ['温度异常', '压力异常', '流量异常', '设备故障'];
    const locations = ['1号楼101室', '2号楼入口', '3号循环泵', '4号楼支路', '5号楼202室'];
    const severities = [1, 2, 3];
    const statuses = [0, 1, 2];
    
    return {
      pendingCount: Math.floor(this.random(3, 10)),
      highProbaCount: Math.floor(this.random(1, 5)),
      accuracy: this.random(85, 95).toFixed(1),
      avgLeadTime: `${this.random(1, 4).toFixed(1)}小时`,
      predictions: Array.from({ length: 15 }, (_, i) => ({
        id: i + 1,
        predictTime: this.formatDate(Date.now() - Math.random() * 86400000),
        type: types[Math.floor(this.random(0, types.length))],
        location: locations[Math.floor(this.random(0, locations.length))],
        anomalyProb: Math.floor(this.random(50, 95)),
        occurTime: this.formatDate(Date.now() + Math.random() * 86400000),
        leadTime: `${this.random(1, 24).toFixed(1)}小时`,
        severity: severities[Math.floor(this.random(0, severities.length))],
        description: '预计温度/压力/流量发生异常',
        advice: '检查供热系统/检查管网/观察运行',
        status: statuses[Math.floor(this.random(0, statuses.length))]
      }))
    };
  },

  // 气候补偿配置
  getClimateCompensation() {
    return {
      currentConfig: ['智能补偿', '气候补偿', '固定温度'][Math.floor(this.random(0, 3))],
      outdoorTemp: this.random(-15, 5).toFixed(1),
      supplyTemp: this.random(40, 60).toFixed(1),
      savingsRate: this.random(8, 18).toFixed(1),
      compensationMode: 'climate',
      selectedCurve: 'standard',
      indoorTarget: 20,
      minOutdoorTemp: -20,
      maxOutdoorTemp: 10,
      compensationEnabled: true,
      curveData: this.getCompensationCurve(),
      effectData: this.getCompensationEffect()
    };
  },

  getCompensationCurve() {
    const outdoorTemps = [-20, -15, -10, -5, 0, 5, 10];
    return {
      temps: outdoorTemps,
      supplyTemp: outdoorTemps.map(t => Math.max(35, 75 + t * 2)),
      returnTemp: outdoorTemps.map(t => Math.max(25, 55 + t * 1.5))
    };
  },

  getCompensationEffect() {
    const days = ['周一', '周二', '周三', '周四', '周五', '周六', '周日'];
    return {
      days,
      before: days.map(() => Math.floor(this.random(40, 60))),
      after: days.map(() => Math.floor(this.random(35, 55)))
    };
  },

  // 多能耦合数据
  getMultiEnergy() {
    return {
      todayCost: Math.floor(this.random(20000, 35000)),
      savingsRate: this.random(10, 20).toFixed(1),
      emissions: this.random(30, 50).toFixed(1),
      reliability: this.random(97, 99.5).toFixed(1),
      energyDistribution: [
        { value: Math.floor(this.random(40, 60)), name: '燃气' },
        { value: Math.floor(this.random(20, 35)), name: '电力' },
        { value: Math.floor(this.random(5, 15)), name: '光伏' },
        { value: Math.floor(this.random(2, 8)), name: '余热' }
      ],
      devices: [
        { name: '1号燃气锅炉', status: '运行', power: this.random(5, 10).toFixed(1), efficiency: this.random(92, 97).toFixed(1), loadRate: Math.floor(this.random(50, 95)), runTime: Math.floor(this.random(50, 200)) },
        { name: '2号燃气锅炉', status: Math.random() > 0.3 ? '运行' : '停止', power: this.random(0, 8).toFixed(1), efficiency: this.random(90, 95).toFixed(1), loadRate: Math.floor(this.random(0, 80)), runTime: Math.floor(this.random(0, 150)) },
        { name: '空气源热泵1', status: Math.random() > 0.4 ? '运行' : '停止', power: this.random(0, 5).toFixed(1), efficiency: this.random(250, 320).toFixed(1), loadRate: Math.floor(this.random(0, 90)), runTime: Math.floor(this.random(0, 300)) },
        { name: '空气源热泵2', status: '停止', power: '0', efficiency: '0', loadRate: 0, runTime: 0 }
      ]
    };
  },

  // 格式化为中文日期
  formatDate(timestamp) {
    const date = new Date(timestamp);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hour = String(date.getHours()).padStart(2, '0');
    const minute = String(date.getMinutes()).padStart(2, '0');
    const second = String(date.getSeconds()).padStart(2, '0');
    return `${year}-${month}-${day} ${hour}:${minute}:${second}`;
  }
};

// 导出模拟API
window.MockAPI = MockAPI;
