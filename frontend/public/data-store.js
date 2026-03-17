/**
 * 本地数据存储服务
 * 提供实际的增删改查功能，模拟真实后端API
 */

// 数据存储类
class DataStore {
  constructor() {
    this.initializeData()
  }

  // 初始化演示数据
  initializeData() {
    // 换热站数据
    this.stations = [
      { id: 1, stationCode: 'HS001', stationName: '东城区换热站', address: '北京市东城区', designCapacity: 50, designFlow: 800, status: 1 },
      { id: 2, stationCode: 'HS002', stationName: '西城区换热站', address: '北京市西城区', designCapacity: 40, designFlow: 650, status: 1 },
      { id: 3, stationCode: 'HS003', stationName: '朝阳区换热站', address: '北京市朝阳区', designCapacity: 60, designFlow: 1000, status: 1 }
    ]
    this.stationIdCounter = 4

    // 建筑物数据
    this.buildings = [
      { id: 1, buildingCode: 'BLD001', buildingName: '阳光花园1号楼', address: '东城区阳光路1号', areaHeated: 12000, buildingType: 'residential', heatTransferCoefficient: 1.2, status: 1 },
      { id: 2, buildingCode: 'BLD002', buildingName: '阳光花园2号楼', address: '东城区阳光路2号', areaHeated: 15000, buildingType: 'residential', heatTransferCoefficient: 1.15, status: 1 },
      { id: 3, buildingCode: 'BLD003', buildingName: '商业大厦A座', address: '西城区金融街8号', areaHeated: 20000, buildingType: 'commercial', heatTransferCoefficient: 0.9, status: 1 },
      { id: 4, buildingCode: 'BLD004', buildingName: '科技园区办公楼', address: '海淀区中关村1号', areaHeated: 18000, buildingType: 'commercial', heatTransferCoefficient: 0.85, status: 1 },
      { id: 5, buildingCode: 'BLD005', buildingName: '市政府大楼', address: '东城区政府路1号', areaHeated: 25000, buildingType: 'public', heatTransferCoefficient: 0.8, status: 1 }
    ]
    this.buildingIdCounter = 6

    // 热用户数据
    this.users = []
    const names = ['张三', '李四', '王五', '赵六', '钱七', '孙八', '周九', '吴十']
    const buildings = ['阳光花园1号楼', '阳光花园2号楼', '商业大厦A座', '科技园区办公楼', '市政府大楼']
    for (let i = 0; i < 50; i++) {
      this.users.push({
        id: i + 1,
        userCode: `U${String(i + 1).padStart(3, '0')}`,
        userName: names[i % names.length],
        buildingName: buildings[i % buildings.length],
        unitNo: String((i % 6) + 1),
        roomNo: `${101 + (i % 20)}`,
        area: 80 + Math.floor(Math.random() * 80),
        targetTemp: 18 + Math.floor(Math.random() * 8),
        status: 1
      })
    }
    this.userIdCounter = 51

    // 告警数据
    this.alarms = [
      { id: 1, alarmType: 'low_temp', alarmMessage: '1号楼101室温度过低', alarmLevel: 'critical', alarmLevelText: '严重', buildingName: '阳光花园1号楼', alarmValue: 16.5, thresholdValue: 18, createTime: '2026-03-14 10:30:00', acknowledged: false },
      { id: 2, alarmType: 'valve_fail', alarmMessage: '2号阀门通讯中断', alarmLevel: 'warning', alarmLevelText: '警告', buildingName: '阳光花园2号楼', alarmValue: 0, thresholdValue: 1, createTime: '2026-03-14 10:25:00', acknowledged: true },
      { id: 3, alarmType: 'high_temp', alarmMessage: '3号楼501室温度过高', alarmLevel: 'info', alarmLevelText: '提示', buildingName: '商业大厦A座', alarmValue: 25, thresholdValue: 24, createTime: '2026-03-14 09:15:00', acknowledged: false },
      { id: 4, alarmType: 'pressure', alarmMessage: '1号楼管网压力偏低', alarmLevel: 'warning', alarmLevelText: '警告', buildingName: '阳光花园1号楼', alarmValue: 0.25, thresholdValue: 0.35, createTime: '2026-03-14 11:00:00', acknowledged: false },
      { id: 5, alarmType: 'flow', alarmMessage: '3号楼流量异常', alarmLevel: 'critical', alarmLevelText: '严重', buildingName: '商业大厦A座', alarmValue: 1.2, thresholdValue: 2.0, createTime: '2026-03-14 11:30:00', acknowledged: false }
    ]
    this.alarmIdCounter = 6

    // 设备数据
    this.equipment = [
      { id: 1, code: 'BLR-001', name: '1号燃气锅炉', type: 'boiler', station: '东城区换热站', status: 1, efficiency: 92, runtime: 2150 },
      { id: 2, code: 'BLR-002', name: '2号燃气锅炉', type: 'boiler', station: '东城区换热站', status: 1, efficiency: 88, runtime: 1980 },
      { id: 3, code: 'PUMP-001', name: '1号循环泵', type: 'pump', station: '东城区换热站', status: 1, efficiency: 85, runtime: 3200 },
      { id: 4, code: 'HE-001', name: '1号板式换热器', type: 'heat_exchanger', station: '东城区换热站', status: 1, efficiency: 95, runtime: 2800 },
      { id: 5, code: 'VALVE-001', name: '1号楼调节阀', type: 'valve', station: '东城区换热站', status: 1, efficiency: 100, runtime: 4500 },
      { id: 6, code: 'BLR-003', name: '3号燃煤锅炉', type: 'boiler', station: '西城区换热站', status: 2, efficiency: 75, runtime: 1500 },
      { id: 7, code: 'PUMP-002', name: '2号循环泵', type: 'pump', station: '西城区换热站', status: 1, efficiency: 82, runtime: 2100 },
      { id: 8, code: 'HE-002', name: '2号板式换热器', type: 'heat_exchanger', station: '西城区换热站', status: 1, efficiency: 93, runtime: 1900 }
    ]
    this.equipmentIdCounter = 9

    // 锅炉数据
    this.boilers = [
      { id: 1, name: '1号燃气锅炉', status: 'running', loadRate: 85, supplyTemp: 120, returnTemp: 70, efficiency: 0.95 },
      { id: 2, name: '2号燃气锅炉', status: 'running', loadRate: 72, supplyTemp: 118, returnTemp: 68, efficiency: 0.93 },
      { id: 3, name: '3号燃气锅炉', status: 'stopped', loadRate: 0, supplyTemp: 0, returnTemp: 0, efficiency: 0 }
    ]

    // 维护提醒
    this.maintenanceReminders = [
      { id: 1, equipment: '1号循环泵', maintainType: '定期保养', dueDate: '2026-03-20', daysLeft: 5 },
      { id: 2, equipment: '2号燃气锅炉', maintainType: '年度检修', dueDate: '2026-03-25', daysLeft: 10 },
      { id: 3, equipment: '1号板式换热器', maintainType: '清洗维护', dueDate: '2026-03-18', daysLeft: 3 }
    ]
    this.maintenanceIdCounter = 4
  }

  // 延迟模拟
  delay(ms = 300) {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  // 生成随机数
  random(min, max) {
    return Math.random() * (max - min) + min;
  }

  // 格式化日期
  formatDate(timestamp) {
    const date = new Date(timestamp);
    return date.toISOString().replace('T', ' ').substring(0, 19);
  }

  // ==================== 换热站 API ====================
  async getStations(params = {}) {
    await this.delay();
    let data = [...this.stations];
    if (params.stationName) {
      data = data.filter(s => s.stationName.includes(params.stationName));
    }
    return { data, total: data.length };
  }

  async getStationById(id) {
    await this.delay();
    return this.stations.find(s => s.id === id) || null;
  }

  async createStation(station) {
    await this.delay();
    station.id = this.stationIdCounter++;
    this.stations.push(station);
    return { success: true, id: station.id };
  }

  async updateStation(id, station) {
    await this.delay();
    const index = this.stations.findIndex(s => s.id === id);
    if (index !== -1) {
      this.stations[index] = { ...this.stations[index], ...station };
      return { success: true };
    }
    return { success: false };
  }

  async deleteStation(id) {
    await this.delay();
    const index = this.stations.findIndex(s => s.id === id);
    if (index !== -1) {
      this.stations.splice(index, 1);
      return { success: true };
    }
    return { success: false };
  }

  // ==================== 建筑物 API ====================
  async getBuildings(params = {}) {
    await this.delay();
    let data = [...this.buildings];
    if (params.buildingName) {
      data = data.filter(b => b.buildingName.includes(params.buildingName));
    }
    return { data, total: data.length };
  }

  async getBuildingById(id) {
    await this.delay();
    return this.buildings.find(b => b.id === id) || null;
  }

  async createBuilding(building) {
    await this.delay();
    building.id = this.buildingIdCounter++;
    building.status = 1;
    this.buildings.push(building);
    return { success: true, id: building.id };
  }

  async updateBuilding(id, building) {
    await this.delay();
    const index = this.buildings.findIndex(b => b.id === id);
    if (index !== -1) {
      this.buildings[index] = { ...this.buildings[index], ...building };
      return { success: true };
    }
    return { success: false };
  }

  async deleteBuilding(id) {
    await this.delay();
    const index = this.buildings.findIndex(b => b.id === id);
    if (index !== -1) {
      this.buildings.splice(index, 1);
      return { success: true };
    }
    return { success: false };
  }

  // ==================== 热用户 API ====================
  async getUsers(params = {}) {
    await this.delay();
    let data = [...this.users];
    if (params.userName) {
      data = data.filter(u => u.userName.includes(params.userName));
    }
    return { data, total: data.length };
  }

  async getUserById(id) {
    await this.delay();
    return this.users.find(u => u.id === id) || null;
  }

  async createUser(user) {
    await this.delay();
    user.id = this.userIdCounter++;
    user.status = 1;
    this.users.push(user);
    return { success: true, id: user.id };
  }

  async updateUser(id, user) {
    await this.delay();
    const index = this.users.findIndex(u => u.id === id);
    if (index !== -1) {
      this.users[index] = { ...this.users[index], ...user };
      return { success: true };
    }
    return { success: false };
  }

  async deleteUser(id) {
    await this.delay();
    const index = this.users.findIndex(u => u.id === id);
    if (index !== -1) {
      this.users.splice(index, 1);
      return { success: true };
    }
    return { success: false };
  }

  // ==================== 告警 API ====================
  async getAlarms(params = {}) {
    await this.delay();
    let data = [...this.alarms];
    if (params.level && params.level !== 'all') {
      data = data.filter(a => a.alarmLevel === params.level);
    }
    return { data, total: data.length };
  }

  async acknowledgeAlarm(id) {
    await this.delay();
    const alarm = this.alarms.find(a => a.id === id);
    if (alarm) {
      alarm.acknowledged = true;
      return { success: true };
    }
    return { success: false };
  }

  async processAlarm(id, handleType, remark) {
    await this.delay();
    const alarm = this.alarms.find(a => a.id === id);
    if (alarm) {
      alarm.acknowledged = true;
      alarm.handleType = handleType;
      alarm.remark = remark;
      return { success: true };
    }
    return { success: false };
  }

  // ==================== 设备 API ====================
  async getEquipment(params = {}) {
    await this.delay();
    let data = [...this.equipment];
    if (params.type) {
      data = data.filter(e => e.type === params.type);
    }
    if (params.status) {
      data = data.filter(e => e.status === parseInt(params.status));
    }
    return { data, total: data.length };
  }

  async createEquipment(equipment) {
    await this.delay();
    equipment.id = this.equipmentIdCounter++;
    this.equipment.push(equipment);
    return { success: true, id: equipment.id };
  }

  async updateEquipment(id, equipment) {
    await this.delay();
    const index = this.equipment.findIndex(e => e.id === id);
    if (index !== -1) {
      this.equipment[index] = { ...this.equipment[index], ...equipment };
      return { success: true };
    }
    return { success: false };
  }

  // ==================== 锅炉 API ====================
  async getBoilers() {
    await this.delay();
    return { data: this.boilers };
  }

  async adjustBoiler(id, loadRate) {
    await this.delay();
    const boiler = this.boilers.find(b => b.id === id);
    if (boiler) {
      boiler.loadRate = loadRate;
      return { success: true };
    }
    return { success: false };
  }

  // ==================== 仪表盘数据 ====================
  async getDashboardStats() {
    await this.delay();
    return {
      stationCount: this.stations.length,
      buildingCount: this.buildings.length,
      userCount: this.users.length,
      alarmCount: this.alarms.filter(a => !a.acknowledged).length
    };
  }

  async getTemperatureTrend() {
    await this.delay();
    const hours = ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00'];
    return {
      primarySupply: hours.map(() => Math.floor(this.random(115, 125))),
      primaryReturn: hours.map(() => Math.floor(this.random(65, 75))),
      secondarySupply: hours.map(() => Math.floor(this.random(45, 55))),
      secondaryReturn: hours.map(() => Math.floor(this.random(35, 45)))
    };
  }

  async getStationStatus() {
    await this.delay();
    const running = this.stations.filter(s => s.status === 1).length;
    const stopped = this.stations.filter(s => s.status === 0).length;
    const fault = Math.floor(this.random(0, 2));
    return [
      { value: running, name: '运行中', itemStyle: { color: '#67c23a' } },
      { value: stopped, name: '停止', itemStyle: { color: '#909399' } },
      { value: fault, name: '故障', itemStyle: { color: '#f56c6c' } }
    ];
  }

  async getHeatLoad() {
    await this.delay();
    return ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00'].map(() => Math.floor(this.random(40, 70)));
  }

  async getAlarmsList() {
    await this.delay();
    return this.alarms.slice(0, 8);
  }
}

// 创建全局数据存储实例
const dataStore = new DataStore();

// 导出
window.DataStore = dataStore;
