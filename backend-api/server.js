/**
 * 智慧供热系统后端服务
 * 使用SQLite数据库提供持久化存储
 */

const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const initSqlJs = require('sql.js');
const fs = require('fs');
const path = require('path');

const app = express();
const PORT = 8080;

app.use(cors());
app.use(bodyParser.json());

let db;
const dbPath = path.join(__dirname, 'boiler_management.db');

async function initDatabase() {
  const SQL = await initSqlJs();
  
  if (fs.existsSync(dbPath)) {
    const fileBuffer = fs.readFileSync(dbPath);
    db = new SQL.Database(fileBuffer);
    console.log('已加载现有数据库');
  } else {
    db = new SQL.Database();
    console.log('创建新数据库');
  }

  db.run(`
    CREATE TABLE IF NOT EXISTS stations (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      stationCode TEXT UNIQUE,
      stationName TEXT,
      address TEXT,
      designCapacity REAL,
      designFlow REAL,
      status INTEGER DEFAULT 1,
      createdAt TEXT DEFAULT CURRENT_TIMESTAMP,
      updatedAt TEXT DEFAULT CURRENT_TIMESTAMP
    )
  `);

  db.run(`
    CREATE TABLE IF NOT EXISTS buildings (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      buildingCode TEXT UNIQUE,
      buildingName TEXT,
      address TEXT,
      areaHeated REAL,
      buildingType TEXT,
      heatTransferCoefficient REAL,
      status INTEGER DEFAULT 1,
      createdAt TEXT DEFAULT CURRENT_TIMESTAMP,
      updatedAt TEXT DEFAULT CURRENT_TIMESTAMP
    )
  `);

  db.run(`
    CREATE TABLE IF NOT EXISTS users (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      userCode TEXT UNIQUE,
      userName TEXT,
      buildingName TEXT,
      unitNo TEXT,
      roomNo TEXT,
      area REAL,
      targetTemp REAL,
      status INTEGER DEFAULT 1,
      createdAt TEXT DEFAULT CURRENT_TIMESTAMP,
      updatedAt TEXT DEFAULT CURRENT_TIMESTAMP
    )
  `);

  db.run(`
    CREATE TABLE IF NOT EXISTS alarms (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      alarmType TEXT,
      alarmMessage TEXT,
      alarmLevel TEXT,
      alarmLevelText TEXT,
      buildingName TEXT,
      alarmValue REAL,
      thresholdValue REAL,
      createTime TEXT,
      acknowledged INTEGER DEFAULT 0,
      handleType TEXT,
      remark TEXT,
      createdAt TEXT DEFAULT CURRENT_TIMESTAMP,
      updatedAt TEXT DEFAULT CURRENT_TIMESTAMP
    )
  `);

  db.run(`
    CREATE TABLE IF NOT EXISTS equipment (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      code TEXT UNIQUE,
      name TEXT,
      type TEXT,
      station TEXT,
      status INTEGER DEFAULT 1,
      efficiency REAL DEFAULT 0,
      runtime REAL DEFAULT 0,
      createdAt TEXT DEFAULT CURRENT_TIMESTAMP,
      updatedAt TEXT DEFAULT CURRENT_TIMESTAMP
    )
  `);

  db.run(`
    CREATE TABLE IF NOT EXISTS boilers (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      name TEXT,
      status TEXT,
      loadRate REAL DEFAULT 0,
      supplyTemp REAL DEFAULT 0,
      returnTemp REAL DEFAULT 0,
      efficiency REAL DEFAULT 0,
      createdAt TEXT DEFAULT CURRENT_TIMESTAMP,
      updatedAt TEXT DEFAULT CURRENT_TIMESTAMP
    )
  `);

  db.run(`
    CREATE TABLE IF NOT EXISTS maintenance (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      equipment TEXT,
      maintainType TEXT,
      dueDate TEXT,
      daysLeft INTEGER,
      createdAt TEXT DEFAULT CURRENT_TIMESTAMP,
      updatedAt TEXT DEFAULT CURRENT_TIMESTAMP
    )
  `);

  console.log('数据库表初始化完成');
}

function saveDatabase() {
  const data = db.export();
  const buffer = Buffer.from(data);
  fs.writeFileSync(dbPath, buffer);
}

function queryAll(sql, params = []) {
  const stmt = db.prepare(sql);
  if (params.length > 0) stmt.bind(params);
  const results = [];
  while (stmt.step()) {
    results.push(stmt.getAsObject());
  }
  stmt.free();
  return results;
}

function queryOne(sql, params = []) {
  const results = queryAll(sql, params);
  return results.length > 0 ? results[0] : null;
}

function runSql(sql, params = []) {
  db.run(sql, params);
  saveDatabase();
  return db.getRowsModified();
}

function insertAndGetId(sql, params = []) {
  db.run(sql, params);
  const result = queryOne('SELECT last_insert_rowid() as id');
  saveDatabase();
  return result ? result.id : null;
}

// 插入演示数据
function insertDemoData() {
  const result = db.exec('SELECT COUNT(*) as count FROM stations');
  const count = result.length > 0 ? result[0].values[0][0] : 0;
  
  if (count === 0) {
    console.log('插入演示数据...');

    db.run(`INSERT INTO stations (stationCode, stationName, address, designCapacity, designFlow, status) VALUES 
      ('HS001', '东城区换热站', '北京市东城区', 50, 800, 1),
      ('HS002', '西城区换热站', '北京市西城区', 40, 650, 1),
      ('HS003', '朝阳区换热站', '北京市朝阳区', 60, 1000, 1)`);

    db.run(`INSERT INTO buildings (buildingCode, buildingName, address, areaHeated, buildingType, heatTransferCoefficient, status) VALUES 
      ('BLD001', '阳光花园1号楼', '东城区阳光路1号', 12000, 'residential', 1.2, 1),
      ('BLD002', '阳光花园2号楼', '东城区阳光路2号', 15000, 'residential', 1.15, 1),
      ('BLD003', '商业大厦A座', '西城区金融街8号', 20000, 'commercial', 0.9, 1),
      ('BLD004', '科技园区办公楼', '海淀区中关村1号', 18000, 'commercial', 0.85, 1),
      ('BLD005', '市政府大楼', '东城区政府路1号', 25000, 'public', 0.8, 1)`);

    const names = ['张三', '李四', '王五', '赵六', '钱七', '孙八', '周九', '吴十'];
    const buildings_list = ['阳光花园1号楼', '阳光花园2号楼', '商业大厦A座', '科技园区办公楼', '市政府大楼'];
    for (let i = 0; i < 50; i++) {
      db.run(`INSERT INTO users (userCode, userName, buildingName, unitNo, roomNo, area, targetTemp, status) VALUES (?, ?, ?, ?, ?, ?, ?, 1)`,
        [`U${String(i + 1).padStart(3, '0')}`, names[i % names.length], buildings_list[i % buildings_list.length], String((i % 6) + 1), `${101 + (i % 20)}`, 80 + Math.floor(Math.random() * 80), 18 + Math.floor(Math.random() * 8)]);
    }

    db.run(`INSERT INTO alarms (alarmType, alarmMessage, alarmLevel, alarmLevelText, buildingName, alarmValue, thresholdValue, createTime, acknowledged) VALUES 
      ('low_temp', '1号楼101室温度过低', 'critical', '严重', '阳光花园1号楼', 16.5, 18, '2026-03-14 10:30:00', 0),
      ('valve_fail', '2号阀门通讯中断', 'warning', '警告', '阳光花园2号楼', 0, 1, '2026-03-14 10:25:00', 1),
      ('high_temp', '3号楼501室温度过高', 'info', '提示', '商业大厦A座', 25, 24, '2026-03-14 09:15:00', 0),
      ('pressure', '1号楼管网压力偏低', 'warning', '警告', '阳光花园1号楼', 0.25, 0.35, '2026-03-14 11:00:00', 0),
      ('flow', '3号楼流量异常', 'critical', '严重', '商业大厦A座', 1.2, 2.0, '2026-03-14 11:30:00', 0)`);

    db.run(`INSERT INTO equipment (code, name, type, station, status, efficiency, runtime) VALUES 
      ('BLR-001', '1号燃气锅炉', 'boiler', '东城区换热站', 1, 92, 2150),
      ('BLR-002', '2号燃气锅炉', 'boiler', '东城区换热站', 1, 88, 1980),
      ('PUMP-001', '1号循环泵', 'pump', '东城区换热站', 1, 85, 3200),
      ('HE-001', '1号板式换热器', 'heat_exchanger', '东城区换热站', 1, 95, 2800),
      ('VALVE-001', '1号楼调节阀', 'valve', '东城区换热站', 1, 100, 4500),
      ('BLR-003', '3号燃煤锅炉', 'boiler', '西城区换热站', 2, 75, 1500),
      ('PUMP-002', '2号循环泵', 'pump', '西城区换热站', 1, 82, 2100),
      ('HE-002', '2号板式换热器', 'heat_exchanger', '西城区换热站', 1, 93, 1900)`);

    db.run(`INSERT INTO boilers (name, status, loadRate, supplyTemp, returnTemp, efficiency) VALUES 
      ('1号燃气锅炉', 'running', 85, 120, 70, 0.95),
      ('2号燃气锅炉', 'running', 72, 118, 68, 0.93),
      ('3号燃气锅炉', 'stopped', 0, 0, 0, 0)`);

    db.run(`INSERT INTO maintenance (equipment, maintainType, dueDate, daysLeft) VALUES 
      ('1号循环泵', '定期保养', '2026-03-20', 5),
      ('2号燃气锅炉', '年度检修', '2026-03-25', 10),
      ('1号板式换热器', '清洗维护', '2026-03-18', 3)`);

    saveDatabase();
    console.log('演示数据插入完成');
  }
}

// ========== API 路由 ==========

// 换热站API
app.get('/api/station', (req, res) => {
  try {
    const rows = queryAll('SELECT * FROM stations');
    res.json({ data: rows, total: rows.length });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.get('/api/station/:id', (req, res) => {
  try {
    const row = queryOne('SELECT * FROM stations WHERE id = ?', [req.params.id]);
    res.json(row || null);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.post('/api/station', (req, res) => {
  try {
    const { stationCode, stationName, address, designCapacity, designFlow, status } = req.body;
    const id = insertAndGetId(
      'INSERT INTO stations (stationCode, stationName, address, designCapacity, designFlow, status) VALUES (?, ?, ?, ?, ?, ?)',
      [stationCode, stationName, address, designCapacity, designFlow, status || 1]
    );
    res.json({ success: true, id });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.put('/api/station/:id', (req, res) => {
  try {
    const { stationCode, stationName, address, designCapacity, designFlow, status } = req.body;
    runSql(
      'UPDATE stations SET stationCode=?, stationName=?, address=?, designCapacity=?, designFlow=?, status=? WHERE id=?',
      [stationCode, stationName, address, designCapacity, designFlow, status, req.params.id]
    );
    res.json({ success: true });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.delete('/api/station/:id', (req, res) => {
  try {
    runSql('DELETE FROM stations WHERE id = ?', [req.params.id]);
    res.json({ success: true });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// 建筑物API
app.get('/api/building', (req, res) => {
  try {
    const rows = queryAll('SELECT * FROM buildings');
    res.json({ data: rows, total: rows.length });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.get('/api/building/:id', (req, res) => {
  try {
    const row = queryOne('SELECT * FROM buildings WHERE id = ?', [req.params.id]);
    res.json(row || null);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.post('/api/building', (req, res) => {
  try {
    const { buildingCode, buildingName, address, areaHeated, buildingType, heatTransferCoefficient, status } = req.body;
    const id = insertAndGetId(
      'INSERT INTO buildings (buildingCode, buildingName, address, areaHeated, buildingType, heatTransferCoefficient, status) VALUES (?, ?, ?, ?, ?, ?, ?)',
      [buildingCode, buildingName, address, areaHeated, buildingType, heatTransferCoefficient, status || 1]
    );
    res.json({ success: true, id });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.put('/api/building/:id', (req, res) => {
  try {
    const { buildingCode, buildingName, address, areaHeated, buildingType, heatTransferCoefficient, status } = req.body;
    runSql(
      'UPDATE buildings SET buildingCode=?, buildingName=?, address=?, areaHeated=?, buildingType=?, heatTransferCoefficient=?, status=? WHERE id=?',
      [buildingCode, buildingName, address, areaHeated, buildingType, heatTransferCoefficient, status, req.params.id]
    );
    res.json({ success: true });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.delete('/api/building/:id', (req, res) => {
  try {
    runSql('DELETE FROM buildings WHERE id = ?', [req.params.id]);
    res.json({ success: true });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// 热用户API
app.get('/api/heat-user', (req, res) => {
  try {
    const rows = queryAll('SELECT * FROM users');
    res.json({ data: rows, total: rows.length });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.get('/api/heat-user/:id', (req, res) => {
  try {
    const row = queryOne('SELECT * FROM users WHERE id = ?', [req.params.id]);
    res.json(row || null);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.post('/api/heat-user', (req, res) => {
  try {
    const { userCode, userName, buildingName, unitNo, roomNo, area, targetTemp, status } = req.body;
    const id = insertAndGetId(
      'INSERT INTO users (userCode, userName, buildingName, unitNo, roomNo, area, targetTemp, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)',
      [userCode, userName, buildingName, unitNo, roomNo, area, targetTemp, status || 1]
    );
    res.json({ success: true, id });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.put('/api/heat-user/:id', (req, res) => {
  try {
    const { userCode, userName, buildingName, unitNo, roomNo, area, targetTemp, status } = req.body;
    runSql(
      'UPDATE users SET userCode=?, userName=?, buildingName=?, unitNo=?, roomNo=?, area=?, targetTemp=?, status=? WHERE id=?',
      [userCode, userName, buildingName, unitNo, roomNo, area, targetTemp, status, req.params.id]
    );
    res.json({ success: true });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.delete('/api/heat-user/:id', (req, res) => {
  try {
    runSql('DELETE FROM users WHERE id = ?', [req.params.id]);
    res.json({ success: true });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// 告警API
app.get('/api/alarm', (req, res) => {
  try {
    const level = req.query.level;
    let sql = 'SELECT * FROM alarms';
    let params = [];
    if (level && level !== 'all') {
      sql += ' WHERE alarmLevel = ?';
      params.push(level);
    }
    sql += ' ORDER BY createTime DESC';
    const rows = queryAll(sql, params);
    res.json({ data: rows, total: rows.length });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.post('/api/alarm/:id/acknowledge', (req, res) => {
  try {
    runSql('UPDATE alarms SET acknowledged = 1 WHERE id = ?', [req.params.id]);
    res.json({ success: true });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.put('/api/alarm/:id/process', (req, res) => {
  try {
    const { handleType, remark } = req.body;
    runSql('UPDATE alarms SET acknowledged = 1, handleType = ?, remark = ? WHERE id = ?', [handleType, remark, req.params.id]);
    res.json({ success: true });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// 设备API
app.get('/api/equipment', (req, res) => {
  try {
    const { type, status } = req.query;
    let sql = 'SELECT * FROM equipment WHERE 1=1';
    let params = [];
    if (type) {
      sql += ' AND type = ?';
      params.push(type);
    }
    if (status) {
      sql += ' AND status = ?';
      params.push(parseInt(status));
    }
    const rows = queryAll(sql, params);
    res.json({ data: rows, total: rows.length });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.post('/api/equipment', (req, res) => {
  try {
    const { code, name, type, station, status, efficiency, runtime } = req.body;
    const id = insertAndGetId(
      'INSERT INTO equipment (code, name, type, station, status, efficiency, runtime) VALUES (?, ?, ?, ?, ?, ?, ?)',
      [code, name, type, station, status || 1, efficiency || 0, runtime || 0]
    );
    res.json({ success: true, id });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.put('/api/equipment/:id', (req, res) => {
  try {
    const { code, name, type, station, status, efficiency, runtime } = req.body;
    runSql(
      'UPDATE equipment SET code=?, name=?, type=?, station=?, status=?, efficiency=?, runtime=? WHERE id=?',
      [code, name, type, station, status, efficiency, runtime, req.params.id]
    );
    res.json({ success: true });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// 锅炉API
app.get('/api/boiler', (req, res) => {
  try {
    const rows = queryAll('SELECT * FROM boilers');
    res.json({ data: rows });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.put('/api/boiler/:id', (req, res) => {
  try {
    const { name, status, loadRate, supplyTemp, returnTemp, efficiency } = req.body;
    runSql(
      'UPDATE boilers SET name=?, status=?, loadRate=?, supplyTemp=?, returnTemp=?, efficiency=? WHERE id=?',
      [name, status, loadRate, supplyTemp, returnTemp, efficiency, req.params.id]
    );
    res.json({ success: true });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// 仪表盘数据API
app.get('/api/dashboard/stats', (req, res) => {
  try {
    const stationCount = queryOne('SELECT COUNT(*) as count FROM stations');
    const buildingCount = queryOne('SELECT COUNT(*) as count FROM buildings');
    const userCount = queryOne('SELECT COUNT(*) as count FROM users');
    const alarmCount = queryOne('SELECT COUNT(*) as count FROM alarms WHERE acknowledged = 0');
    res.json({
      stationCount: stationCount ? stationCount.count : 0,
      buildingCount: buildingCount ? buildingCount.count : 0,
      userCount: userCount ? userCount.count : 0,
      alarmCount: alarmCount ? alarmCount.count : 0
    });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
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
  try {
    const running = queryOne('SELECT COUNT(*) as count FROM stations WHERE status = 1');
    const stopped = queryOne('SELECT COUNT(*) as count FROM stations WHERE status = 0');
    res.json([
      { value: running ? running.count : 0, name: '运行中', itemStyle: { color: '#67c23a' } },
      { value: stopped ? stopped.count : 0, name: '停止', itemStyle: { color: '#909399' } },
      { value: 0, name: '故障', itemStyle: { color: '#f56c6c' } }
    ]);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.get('/api/dashboard/heat-load', (req, res) => {
  res.json(['00:00', '04:00', '08:00', '12:00', '16:00', '20:00'].map(() => Math.floor(Math.random() * 30) + 40));
});

app.get('/api/dashboard/alarms', (req, res) => {
  try {
    const rows = queryAll('SELECT * FROM alarms ORDER BY createTime DESC LIMIT 8');
    res.json(rows);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// 启动服务器
async function startServer() {
  try {
    await initDatabase();
    insertDemoData();
    app.listen(PORT, '0.0.0.0', () => {
      console.log(`后端服务已启动: http://localhost:${PORT}`);
    });
  } catch (err) {
    console.error('启动失败:', err);
    process.exit(1);
  }
}

startServer();
