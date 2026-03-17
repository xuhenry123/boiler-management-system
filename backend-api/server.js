/**
 * 智慧供热系统后端服务
 * 使用NeDB数据库提供持久化存储
 */

const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const Datastore = require('nedb');

const app = express();
const PORT = 8080;

// 中间件
app.use(cors());
app.use(bodyParser.json());

// 数据库初始化
const db = {
  stations: new Datastore({ filename: './data/stations.db', autoload: true }),
  buildings: new Datastore({ filename: './data/buildings.db', autoload: true }),
  users: new Datastore({ filename: './data/users.db', autoload: true }),
  alarms: new Datastore({ filename: './data/alarms.db', autoload: true }),
  equipment: new Datastore({ filename: './data/equipment.db', autoload: true }),
  boilers: new Datastore({ filename: './data/boilers.db', autoload: true }),
  maintenance: new Datastore({ filename: './data/maintenance.db', autoload: true })
};

console.log('数据库初始化完成');

// 插入演示数据
function insertDemoData() {
  db.stations.count({}, (err, count) => {
    if (count === 0) {
      console.log('插入演示数据...');
      
      // 换热站数据
      db.stations.insert([
        { stationCode: 'HS001', stationName: '东城区换热站', address: '北京市东城区', designCapacity: 50, designFlow: 800, status: 1 },
        { stationCode: 'HS002', stationName: '西城区换热站', address: '北京市西城区', designCapacity: 40, designFlow: 650, status: 1 },
        { stationCode: 'HS003', stationName: '朝阳区换热站', address: '北京市朝阳区', designCapacity: 60, designFlow: 1000, status: 1 }
      ]);

      // 建筑物数据
      db.buildings.insert([
        { buildingCode: 'BLD001', buildingName: '阳光花园1号楼', address: '东城区阳光路1号', areaHeated: 12000, buildingType: 'residential', heatTransferCoefficient: 1.2, status: 1 },
        { buildingCode: 'BLD002', buildingName: '阳光花园2号楼', address: '东城区阳光路2号', areaHeated: 15000, buildingType: 'residential', heatTransferCoefficient: 1.15, status: 1 },
        { buildingCode: 'BLD003', buildingName: '商业大厦A座', address: '西城区金融街8号', areaHeated: 20000, buildingType: 'commercial', heatTransferCoefficient: 0.9, status: 1 },
        { buildingCode: 'BLD004', buildingName: '科技园区办公楼', address: '海淀区中关村1号', areaHeated: 18000, buildingType: 'commercial', heatTransferCoefficient: 0.85, status: 1 },
        { buildingCode: 'BLD005', buildingName: '市政府大楼', address: '东城区政府路1号', areaHeated: 25000, buildingType: 'public', heatTransferCoefficient: 0.8, status: 1 }
      ]);

      // 热用户数据
      const names = ['张三', '李四', '王五', '赵六', '钱七', '孙八', '周九', '吴十'];
      const buildings_list = ['阳光花园1号楼', '阳光花园2号楼', '商业大厦A座', '科技园区办公楼', '市政府大楼'];
      const users = [];
      for (let i = 0; i < 50; i++) {
        users.push({
          userCode: `U${String(i + 1).padStart(3, '0')}`,
          userName: names[i % names.length],
          buildingName: buildings_list[i % buildings_list.length],
          unitNo: String((i % 6) + 1),
          roomNo: `${101 + (i % 20)}`,
          area: 80 + Math.floor(Math.random() * 80),
          targetTemp: 18 + Math.floor(Math.random() * 8),
          status: 1
        });
      }
      db.users.insert(users);

      // 告警数据
      db.alarms.insert([
        { alarmType: 'low_temp', alarmMessage: '1号楼101室温度过低', alarmLevel: 'critical', alarmLevelText: '严重', buildingName: '阳光花园1号楼', alarmValue: 16.5, thresholdValue: 18, createTime: '2026-03-14 10:30:00', acknowledged: false },
        { alarmType: 'valve_fail', alarmMessage: '2号阀门通讯中断', alarmLevel: 'warning', alarmLevelText: '警告', buildingName: '阳光花园2号楼', alarmValue: 0, thresholdValue: 1, createTime: '2026-03-14 10:25:00', acknowledged: true },
        { alarmType: 'high_temp', alarmMessage: '3号楼501室温度过高', alarmLevel: 'info', alarmLevelText: '提示', buildingName: '商业大厦A座', alarmValue: 25, thresholdValue: 24, createTime: '2026-03-14 09:15:00', acknowledged: false },
        { alarmType: 'pressure', alarmMessage: '1号楼管网压力偏低', alarmLevel: 'warning', alarmLevelText: '警告', buildingName: '阳光花园1号楼', alarmValue: 0.25, thresholdValue: 0.35, createTime: '2026-03-14 11:00:00', acknowledged: false },
        { alarmType: 'flow', alarmMessage: '3号楼流量异常', alarmLevel: 'critical', alarmLevelText: '严重', buildingName: '商业大厦A座', alarmValue: 1.2, thresholdValue: 2.0, createTime: '2026-03-14 11:30:00', acknowledged: false }
      ]);

      // 设备数据
      db.equipment.insert([
        { code: 'BLR-001', name: '1号燃气锅炉', type: 'boiler', station: '东城区换热站', status: 1, efficiency: 92, runtime: 2150 },
        { code: 'BLR-002', name: '2号燃气锅炉', type: 'boiler', station: '东城区换热站', status: 1, efficiency: 88, runtime: 1980 },
        { code: 'PUMP-001', name: '1号循环泵', type: 'pump', station: '东城区换热站', status: 1, efficiency: 85, runtime: 3200 },
        { code: 'HE-001', name: '1号板式换热器', type: 'heat_exchanger', station: '东城区换热站', status: 1, efficiency: 95, runtime: 2800 },
        { code: 'VALVE-001', name: '1号楼调节阀', type: 'valve', station: '东城区换热站', status: 1, efficiency: 100, runtime: 4500 },
        { code: 'BLR-003', name: '3号燃煤锅炉', type: 'boiler', station: '西城区换热站', status: 2, efficiency: 75, runtime: 1500 },
        { code: 'PUMP-002', name: '2号循环泵', type: 'pump', station: '西城区换热站', status: 1, efficiency: 82, runtime: 2100 },
        { code: 'HE-002', name: '2号板式换热器', type: 'heat_exchanger', station: '西城区换热站', status: 1, efficiency: 93, runtime: 1900 }
      ]);

      // 锅炉数据
      db.boilers.insert([
        { name: '1号燃气锅炉', status: 'running', loadRate: 85, supplyTemp: 120, returnTemp: 70, efficiency: 0.95 },
        { name: '2号燃气锅炉', status: 'running', loadRate: 72, supplyTemp: 118, returnTemp: 68, efficiency: 0.93 },
        { name: '3号燃气锅炉', status: 'stopped', loadRate: 0, supplyTemp: 0, returnTemp: 0, efficiency: 0 }
      ]);

      // 维护提醒数据
      db.maintenance.insert([
        { equipment: '1号循环泵', maintainType: '定期保养', dueDate: '2026-03-20', daysLeft: 5 },
        { equipment: '2号燃气锅炉', maintainType: '年度检修', dueDate: '2026-03-25', daysLeft: 10 },
        { equipment: '1号板式换热器', maintainType: '清洗维护', dueDate: '2026-03-18', daysLeft: 3 }
      ]);

      console.log('演示数据插入完成');
    }
  });
}

// 初始化数据
insertDemoData();

// ========== API 路由 ==========

// 换热站API
app.get('/api/station', (req, res) => {
  db.stations.find({}, (err, docs) => {
    if (err) return res.status(500).json({ error: err.message });
    res.json({ data: docs, total: docs.length });
  });
});

app.get('/api/station/:id', (req, res) => {
  db.stations.findOne({ _id: req.params.id }, (err, doc) => {
    if (err) return res.status(500).json({ error: err.message });
    res.json(doc);
  });
});

app.post('/api/station', (req, res) => {
  const { stationCode, stationName, address, designCapacity, designFlow, status } = req.body;
  db.stations.insert({ stationCode, stationName, address, designCapacity, designFlow, status: status || 1 }, (err, newDoc) => {
    if (err) return res.status(500).json({ error: err.message });
    res.json({ success: true, id: newDoc._id });
  });
});

app.put('/api/station/:id', (req, res) => {
  const { stationCode, stationName, address, designCapacity, designFlow, status } = req.body;
  db.stations.update({ _id: req.params.id }, { $set: { stationCode, stationName, address, designCapacity, designFlow, status } }, (err) => {
    if (err) return res.status(500).json({ error: err.message });
    res.json({ success: true });
  });
});

app.delete('/api/station/:id', (req, res) => {
  db.stations.remove({ _id: req.params.id }, (err) => {
    if (err) return res.status(500).json({ error: err.message });
    res.json({ success: true });
  });
});

// 建筑物API
app.get('/api/building', (req, res) => {
  db.buildings.find({}, (err, docs) => {
    if (err) return res.status(500).json({ error: err.message });
    res.json({ data: docs, total: docs.length });
  });
});

app.get('/api/building/:id', (req, res) => {
  db.buildings.findOne({ _id: req.params.id }, (err, doc) => {
    if (err) return res.status(500).json({ error: err.message });
    res.json(doc);
  });
});

app.post('/api/building', (req, res) => {
  const { buildingCode, buildingName, address, areaHeated, buildingType, heatTransferCoefficient, status } = req.body;
  db.buildings.insert({ buildingCode, buildingName, address, areaHeated, buildingType, heatTransferCoefficient, status: status || 1 }, (err, newDoc) => {
    if (err) return res.status(500).json({ error: err.message });
    res.json({ success: true, id: newDoc._id });
  });
});

app.put('/api/building/:id', (req, res) => {
  const { buildingCode, buildingName, address, areaHeated, buildingType, heatTransferCoefficient, status } = req.body;
  db.buildings.update({ _id: req.params.id }, { $set: { buildingCode, buildingName, address, areaHeated, buildingType, heatTransferCoefficient, status } }, (err) => {
    if (err) return res.status(500).json({ error: err.message });
    res.json({ success: true });
  });
});

app.delete('/api/building/:id', (req, res) => {
  db.buildings.remove({ _id: req.params.id }, (err) => {
    if (err) return res.status(500).json({ error: err.message });
    res.json({ success: true });
  });
});

// 热用户API
app.get('/api/heat-user', (req, res) => {
  db.users.find({}, (err, docs) => {
    if (err) return res.status(500).json({ error: err.message });
    res.json({ data: docs, total: docs.length });
  });
});

app.get('/api/heat-user/:id', (req, res) => {
  db.users.findOne({ _id: req.params.id }, (err, doc) => {
    if (err) return res.status(500).json({ error: err.message });
    res.json(doc);
  });
});

app.post('/api/heat-user', (req, res) => {
  const { userCode, userName, buildingName, unitNo, roomNo, area, targetTemp, status } = req.body;
  db.users.insert({ userCode, userName, buildingName, unitNo, roomNo, area, targetTemp, status: status || 1 }, (err, newDoc) => {
    if (err) return res.status(500).json({ error: err.message });
    res.json({ success: true, id: newDoc._id });
  });
});

app.put('/api/heat-user/:id', (req, res) => {
  const { userCode, userName, buildingName, unitNo, roomNo, area, targetTemp, status } = req.body;
  db.users.update({ _id: req.params.id }, { $set: { userCode, userName, buildingName, unitNo, roomNo, area, targetTemp, status } }, (err) => {
    if (err) return res.status(500).json({ error: err.message });
    res.json({ success: true });
  });
});

app.delete('/api/heat-user/:id', (req, res) => {
  db.users.remove({ _id: req.params.id }, (err) => {
    if (err) return res.status(500).json({ error: err.message });
    res.json({ success: true });
  });
});

// 告警API
app.get('/api/alarm', (req, res) => {
  const level = req.query.level;
  let query = {};
  if (level && level !== 'all') {
    query = { alarmLevel: level };
  }
  db.alarms.find(query).sort({ createTime: -1 }).exec((err, docs) => {
    if (err) return res.status(500).json({ error: err.message });
    res.json({ data: docs, total: docs.length });
  });
});

app.post('/api/alarm/:id/acknowledge', (req, res) => {
  db.alarms.update({ _id: req.params.id }, { $set: { acknowledged: true } }, (err) => {
    if (err) return res.status(500).json({ error: err.message });
    res.json({ success: true });
  });
});

app.put('/api/alarm/:id/process', (req, res) => {
  const { handleType, remark } = req.body;
  db.alarms.update({ _id: req.params.id }, { $set: { acknowledged: true, handleType, remark } }, (err) => {
    if (err) return res.status(500).json({ error: err.message });
    res.json({ success: true });
  });
});

// 设备API
app.get('/api/equipment', (req, res) => {
  const { type, status } = req.query;
  let query = {};
  if (type) query.type = type;
  if (status) query.status = parseInt(status);
  
  db.equipment.find(query, (err, docs) => {
    if (err) return res.status(500).json({ error: err.message });
    res.json({ data: docs, total: docs.length });
  });
});

app.post('/api/equipment', (req, res) => {
  const { code, name, type, station, status, efficiency, runtime } = req.body;
  db.equipment.insert({ code, name, type, station, status: status || 1, efficiency: efficiency || 0, runtime: runtime || 0 }, (err, newDoc) => {
    if (err) return res.status(500).json({ error: err.message });
    res.json({ success: true, id: newDoc._id });
  });
});

app.put('/api/equipment/:id', (req, res) => {
  const { code, name, type, station, status, efficiency, runtime } = req.body;
  db.equipment.update({ _id: req.params.id }, { $set: { code, name, type, station, status, efficiency, runtime } }, (err) => {
    if (err) return res.status(500).json({ error: err.message });
    res.json({ success: true });
  });
});

// 锅炉API
app.get('/api/boiler', (req, res) => {
  db.boilers.find({}, (err, docs) => {
    if (err) return res.status(500).json({ error: err.message });
    res.json({ data: docs });
  });
});

app.put('/api/boiler/:id', (req, res) => {
  const { name, status, loadRate, supplyTemp, returnTemp, efficiency } = req.body;
  db.boilers.update({ _id: req.params.id }, { $set: { name, status, loadRate, supplyTemp, returnTemp, efficiency } }, (err) => {
    if (err) return res.status(500).json({ error: err.message });
    res.json({ success: true });
  });
});

// 仪表盘数据API
app.get('/api/dashboard/stats', (req, res) => {
  db.stations.count({}, (err, stationCount) => {
    db.buildings.count({}, (err, buildingCount) => {
      db.users.count({}, (err, userCount) => {
        db.alarms.count({ acknowledged: false }, (err, alarmCount) => {
          res.json({
            stationCount,
            buildingCount,
            userCount,
            alarmCount
          });
        });
      });
    });
  });
});

app.get('/api/dashboard/temperature-trend', (req, res) => {
  const hours = ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00'];
  res.json({
    primarySupply: hours.map(() => Math.floor(Math.random() * 10) + 115),
    primaryReturn: hours.map(() => Math.floor(Math.random() * 10) + 65),
    secondarySupply: hours.map(() => Math.floor(Math.random() * 10) + 45),
    secondaryReturn: hours.map(() => Math.floor(Math.random() * 10) + 35)
  });
});

app.get('/api/dashboard/station-status', (req, res) => {
  db.stations.count({ status: 1 }, (err, running) => {
    db.stations.count({ status: 0 }, (err, stopped) => {
      res.json([
        { value: running || 0, name: '运行中', itemStyle: { color: '#67c23a' } },
        { value: stopped || 0, name: '停止', itemStyle: { color: '#909399' } },
        { value: 0, name: '故障', itemStyle: { color: '#f56c6c' } }
      ]);
    });
  });
});

app.get('/api/dashboard/heat-load', (req, res) => {
  res.json(['00:00', '04:00', '08:00', '12:00', '16:00', '20:00'].map(() => Math.floor(Math.random() * 30) + 40));
});

app.get('/api/dashboard/alarms', (req, res) => {
  db.alarms.find({}).sort({ createTime: -1 }).limit(8).exec((err, docs) => {
    if (err) return res.status(500).json({ error: err.message });
    res.json(docs);
  });
});

// 启动服务器
app.listen(PORT, '0.0.0.0', () => {
  console.log(`后端服务已启动: http://localhost:${PORT}`);
});
