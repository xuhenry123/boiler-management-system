/**
 * 智慧供热系统后端服务
 * 使用MySQL数据库提供持久化存储
 */

const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const mysql = require('mysql2/promise');

const app = express();
const PORT = 8080;

// 中间件
app.use(cors());
app.use(bodyParser.json());

// 数据库连接池
let pool;

async function initDatabase() {
  pool = mysql.createPool({
    host: 'localhost',
    user: 'root',
    password: '',
    database: 'boiler_management',
    waitForConnections: true,
    connectionLimit: 10,
    queueLimit: 0
  });

  // 创建表
  const createTablesSQL = `
    CREATE TABLE IF NOT EXISTS stations (
      id INT AUTO_INCREMENT PRIMARY KEY,
      stationCode VARCHAR(50) UNIQUE,
      stationName VARCHAR(100),
      address VARCHAR(255),
      designCapacity DECIMAL(10,2),
      designFlow DECIMAL(10,2),
      status INT DEFAULT 1,
      createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

    CREATE TABLE IF NOT EXISTS buildings (
      id INT AUTO_INCREMENT PRIMARY KEY,
      buildingCode VARCHAR(50) UNIQUE,
      buildingName VARCHAR(100),
      address VARCHAR(255),
      areaHeated DECIMAL(12,2),
      buildingType VARCHAR(50),
      heatTransferCoefficient DECIMAL(5,2),
      status INT DEFAULT 1,
      createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

    CREATE TABLE IF NOT EXISTS users (
      id INT AUTO_INCREMENT PRIMARY KEY,
      userCode VARCHAR(50) UNIQUE,
      userName VARCHAR(50),
      buildingName VARCHAR(100),
      unitNo VARCHAR(20),
      roomNo VARCHAR(20),
      area DECIMAL(10,2),
      targetTemp DECIMAL(5,1),
      status INT DEFAULT 1,
      createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

    CREATE TABLE IF NOT EXISTS alarms (
      id INT AUTO_INCREMENT PRIMARY KEY,
      alarmType VARCHAR(50),
      alarmMessage TEXT,
      alarmLevel VARCHAR(20),
      alarmLevelText VARCHAR(20),
      buildingName VARCHAR(100),
      alarmValue DECIMAL(10,2),
      thresholdValue DECIMAL(10,2),
      createTime DATETIME,
      acknowledged TINYINT DEFAULT 0,
      handleType VARCHAR(50),
      remark TEXT,
      createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

    CREATE TABLE IF NOT EXISTS equipment (
      id INT AUTO_INCREMENT PRIMARY KEY,
      code VARCHAR(50) UNIQUE,
      name VARCHAR(100),
      type VARCHAR(50),
      station VARCHAR(100),
      status INT DEFAULT 1,
      efficiency DECIMAL(5,2) DEFAULT 0,
      runtime DECIMAL(12,2) DEFAULT 0,
      createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

    CREATE TABLE IF NOT EXISTS boilers (
      id INT AUTO_INCREMENT PRIMARY KEY,
      name VARCHAR(100),
      status VARCHAR(20),
      loadRate DECIMAL(5,2) DEFAULT 0,
      supplyTemp DECIMAL(5,1) DEFAULT 0,
      returnTemp DECIMAL(5,1) DEFAULT 0,
      efficiency DECIMAL(5,3) DEFAULT 0,
      createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

    CREATE TABLE IF NOT EXISTS maintenance (
      id INT AUTO_INCREMENT PRIMARY KEY,
      equipment VARCHAR(100),
      maintainType VARCHAR(50),
      dueDate DATE,
      daysLeft INT,
      createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );
  `;

  const statements = createTablesSQL.split(';').filter(s => s.trim());
  for (const stmt of statements) {
    if (stmt.trim()) {
      await pool.execute(stmt);
    }
  }

  console.log('数据库表初始化完成');
}

// 插入演示数据
async function insertDemoData() {
  const [rows] = await pool.query('SELECT COUNT(*) as count FROM stations');
  if (rows[0].count === 0) {
    console.log('插入演示数据...');

    // 换热站数据
    await pool.query(`
      INSERT INTO stations (stationCode, stationName, address, designCapacity, designFlow, status) VALUES
      ('HS001', '东城区换热站', '北京市东城区', 50, 800, 1),
      ('HS002', '西城区换热站', '北京市西城区', 40, 650, 1),
      ('HS003', '朝阳区换热站', '北京市朝阳区', 60, 1000, 1)
    `);

    // 建筑物数据
    await pool.query(`
      INSERT INTO buildings (buildingCode, buildingName, address, areaHeated, buildingType, heatTransferCoefficient, status) VALUES
      ('BLD001', '阳光花园1号楼', '东城区阳光路1号', 12000, 'residential', 1.2, 1),
      ('BLD002', '阳光花园2号楼', '东城区阳光路2号', 15000, 'residential', 1.15, 1),
      ('BLD003', '商业大厦A座', '西城区金融街8号', 20000, 'commercial', 0.9, 1),
      ('BLD004', '科技园区办公楼', '海淀区中关村1号', 18000, 'commercial', 0.85, 1),
      ('BLD005', '市政府大楼', '东城区政府路1号', 25000, 'public', 0.8, 1)
    `);

    // 热用户数据
    const names = ['张三', '李四', '王五', '赵六', '钱七', '孙八', '周九', '吴十'];
    const buildings_list = ['阳光花园1号楼', '阳光花园2号楼', '商业大厦A座', '科技园区办公楼', '市政府大楼'];
    for (let i = 0; i < 50; i++) {
      await pool.query(`
        INSERT INTO users (userCode, userName, buildingName, unitNo, roomNo, area, targetTemp, status) VALUES
        (?, ?, ?, ?, ?, ?, ?, 1)
      `, [
        `U${String(i + 1).padStart(3, '0')}`,
        names[i % names.length],
        buildings_list[i % buildings_list.length],
        String((i % 6) + 1),
        `${101 + (i % 20)}`,
        80 + Math.floor(Math.random() * 80),
        18 + Math.floor(Math.random() * 8)
      ]);
    }

    // 告警数据
    await pool.query(`
      INSERT INTO alarms (alarmType, alarmMessage, alarmLevel, alarmLevelText, buildingName, alarmValue, thresholdValue, createTime, acknowledged) VALUES
      ('low_temp', '1号楼101室温度过低', 'critical', '严重', '阳光花园1号楼', 16.5, 18, '2026-03-14 10:30:00', 0),
      ('valve_fail', '2号阀门通讯中断', 'warning', '警告', '阳光花园2号楼', 0, 1, '2026-03-14 10:25:00', 1),
      ('high_temp', '3号楼501室温度过高', 'info', '提示', '商业大厦A座', 25, 24, '2026-03-14 09:15:00', 0),
      ('pressure', '1号楼管网压力偏低', 'warning', '警告', '阳光花园1号楼', 0.25, 0.35, '2026-03-14 11:00:00', 0),
      ('flow', '3号楼流量异常', 'critical', '严重', '商业大厦A座', 1.2, 2.0, '2026-03-14 11:30:00', 0)
    `);

    // 设备数据
    await pool.query(`
      INSERT INTO equipment (code, name, type, station, status, efficiency, runtime) VALUES
      ('BLR-001', '1号燃气锅炉', 'boiler', '东城区换热站', 1, 92, 2150),
      ('BLR-002', '2号燃气锅炉', 'boiler', '东城区换热站', 1, 88, 1980),
      ('PUMP-001', '1号循环泵', 'pump', '东城区换热站', 1, 85, 3200),
      ('HE-001', '1号板式换热器', 'heat_exchanger', '东城区换热站', 1, 95, 2800),
      ('VALVE-001', '1号楼调节阀', 'valve', '东城区换热站', 1, 100, 4500),
      ('BLR-003', '3号燃煤锅炉', 'boiler', '西城区换热站', 2, 75, 1500),
      ('PUMP-002', '2号循环泵', 'pump', '西城区换热站', 1, 82, 2100),
      ('HE-002', '2号板式换热器', 'heat_exchanger', '西城区换热站', 1, 93, 1900)
    `);

    // 锅炉数据
    await pool.query(`
      INSERT INTO boilers (name, status, loadRate, supplyTemp, returnTemp, efficiency) VALUES
      ('1号燃气锅炉', 'running', 85, 120, 70, 0.95),
      ('2号燃气锅炉', 'running', 72, 118, 68, 0.93),
      ('3号燃气锅炉', 'stopped', 0, 0, 0, 0)
    `);

    // 维护提醒数据
    await pool.query(`
      INSERT INTO maintenance (equipment, maintainType, dueDate, daysLeft) VALUES
      ('1号循环泵', '定期保养', '2026-03-20', 5),
      ('2号燃气锅炉', '年度检修', '2026-03-25', 10),
      ('1号板式换热器', '清洗维护', '2026-03-18', 3)
    `);

    console.log('演示数据插入完成');
  }
}

// ========== API 路由 ==========

// 换热站API
app.get('/api/station', async (req, res) => {
  try {
    const [rows] = await pool.query('SELECT * FROM stations');
    res.json({ data: rows, total: rows.length });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.get('/api/station/:id', async (req, res) => {
  try {
    const [rows] = await pool.query('SELECT * FROM stations WHERE id = ?', [req.params.id]);
    res.json(rows[0] || null);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.post('/api/station', async (req, res) => {
  try {
    const { stationCode, stationName, address, designCapacity, designFlow, status } = req.body;
    const [result] = await pool.query(
      'INSERT INTO stations (stationCode, stationName, address, designCapacity, designFlow, status) VALUES (?, ?, ?, ?, ?, ?)',
      [stationCode, stationName, address, designCapacity, designFlow, status || 1]
    );
    res.json({ success: true, id: result.insertId });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.put('/api/station/:id', async (req, res) => {
  try {
    const { stationCode, stationName, address, designCapacity, designFlow, status } = req.body;
    await pool.query(
      'UPDATE stations SET stationCode=?, stationName=?, address=?, designCapacity=?, designFlow=?, status=? WHERE id=?',
      [stationCode, stationName, address, designCapacity, designFlow, status, req.params.id]
    );
    res.json({ success: true });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.delete('/api/station/:id', async (req, res) => {
  try {
    await pool.query('DELETE FROM stations WHERE id = ?', [req.params.id]);
    res.json({ success: true });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// 建筑物API
app.get('/api/building', async (req, res) => {
  try {
    const [rows] = await pool.query('SELECT * FROM buildings');
    res.json({ data: rows, total: rows.length });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.get('/api/building/:id', async (req, res) => {
  try {
    const [rows] = await pool.query('SELECT * FROM buildings WHERE id = ?', [req.params.id]);
    res.json(rows[0] || null);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.post('/api/building', async (req, res) => {
  try {
    const { buildingCode, buildingName, address, areaHeated, buildingType, heatTransferCoefficient, status } = req.body;
    const [result] = await pool.query(
      'INSERT INTO buildings (buildingCode, buildingName, address, areaHeated, buildingType, heatTransferCoefficient, status) VALUES (?, ?, ?, ?, ?, ?, ?)',
      [buildingCode, buildingName, address, areaHeated, buildingType, heatTransferCoefficient, status || 1]
    );
    res.json({ success: true, id: result.insertId });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.put('/api/building/:id', async (req, res) => {
  try {
    const { buildingCode, buildingName, address, areaHeated, buildingType, heatTransferCoefficient, status } = req.body;
    await pool.query(
      'UPDATE buildings SET buildingCode=?, buildingName=?, address=?, areaHeated=?, buildingType=?, heatTransferCoefficient=?, status=? WHERE id=?',
      [buildingCode, buildingName, address, areaHeated, buildingType, heatTransferCoefficient, status, req.params.id]
    );
    res.json({ success: true });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.delete('/api/building/:id', async (req, res) => {
  try {
    await pool.query('DELETE FROM buildings WHERE id = ?', [req.params.id]);
    res.json({ success: true });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// 热用户API
app.get('/api/heat-user', async (req, res) => {
  try {
    const [rows] = await pool.query('SELECT * FROM users');
    res.json({ data: rows, total: rows.length });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.get('/api/heat-user/:id', async (req, res) => {
  try {
    const [rows] = await pool.query('SELECT * FROM users WHERE id = ?', [req.params.id]);
    res.json(rows[0] || null);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.post('/api/heat-user', async (req, res) => {
  try {
    const { userCode, userName, buildingName, unitNo, roomNo, area, targetTemp, status } = req.body;
    const [result] = await pool.query(
      'INSERT INTO users (userCode, userName, buildingName, unitNo, roomNo, area, targetTemp, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)',
      [userCode, userName, buildingName, unitNo, roomNo, area, targetTemp, status || 1]
    );
    res.json({ success: true, id: result.insertId });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.put('/api/heat-user/:id', async (req, res) => {
  try {
    const { userCode, userName, buildingName, unitNo, roomNo, area, targetTemp, status } = req.body;
    await pool.query(
      'UPDATE users SET userCode=?, userName=?, buildingName=?, unitNo=?, roomNo=?, area=?, targetTemp=?, status=? WHERE id=?',
      [userCode, userName, buildingName, unitNo, roomNo, area, targetTemp, status, req.params.id]
    );
    res.json({ success: true });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.delete('/api/heat-user/:id', async (req, res) => {
  try {
    await pool.query('DELETE FROM users WHERE id = ?', [req.params.id]);
    res.json({ success: true });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// 告警API
app.get('/api/alarm', async (req, res) => {
  try {
    const level = req.query.level;
    let sql = 'SELECT * FROM alarms';
    let params = [];
    if (level && level !== 'all') {
      sql += ' WHERE alarmLevel = ?';
      params.push(level);
    }
    sql += ' ORDER BY createTime DESC';
    const [rows] = await pool.query(sql, params);
    res.json({ data: rows, total: rows.length });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.post('/api/alarm/:id/acknowledge', async (req, res) => {
  try {
    await pool.query('UPDATE alarms SET acknowledged = 1 WHERE id = ?', [req.params.id]);
    res.json({ success: true });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.put('/api/alarm/:id/process', async (req, res) => {
  try {
    const { handleType, remark } = req.body;
    await pool.query(
      'UPDATE alarms SET acknowledged = 1, handleType = ?, remark = ? WHERE id = ?',
      [handleType, remark, req.params.id]
    );
    res.json({ success: true });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// 设备API
app.get('/api/equipment', async (req, res) => {
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
    const [rows] = await pool.query(sql, params);
    res.json({ data: rows, total: rows.length });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.post('/api/equipment', async (req, res) => {
  try {
    const { code, name, type, station, status, efficiency, runtime } = req.body;
    const [result] = await pool.query(
      'INSERT INTO equipment (code, name, type, station, status, efficiency, runtime) VALUES (?, ?, ?, ?, ?, ?, ?)',
      [code, name, type, station, status || 1, efficiency || 0, runtime || 0]
    );
    res.json({ success: true, id: result.insertId });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.put('/api/equipment/:id', async (req, res) => {
  try {
    const { code, name, type, station, status, efficiency, runtime } = req.body;
    await pool.query(
      'UPDATE equipment SET code=?, name=?, type=?, station=?, status=?, efficiency=?, runtime=? WHERE id=?',
      [code, name, type, station, status, efficiency, runtime, req.params.id]
    );
    res.json({ success: true });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// 锅炉API
app.get('/api/boiler', async (req, res) => {
  try {
    const [rows] = await pool.query('SELECT * FROM boilers');
    res.json({ data: rows });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.put('/api/boiler/:id', async (req, res) => {
  try {
    const { name, status, loadRate, supplyTemp, returnTemp, efficiency } = req.body;
    await pool.query(
      'UPDATE boilers SET name=?, status=?, loadRate=?, supplyTemp=?, returnTemp=?, efficiency=? WHERE id=?',
      [name, status, loadRate, supplyTemp, returnTemp, efficiency, req.params.id]
    );
    res.json({ success: true });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// 仪表盘数据API
app.get('/api/dashboard/stats', async (req, res) => {
  try {
    const [[stationCount]] = await pool.query('SELECT COUNT(*) as count FROM stations');
    const [[buildingCount]] = await pool.query('SELECT COUNT(*) as count FROM buildings');
    const [[userCount]] = await pool.query('SELECT COUNT(*) as count FROM users');
    const [[alarmCount]] = await pool.query('SELECT COUNT(*) as count FROM alarms WHERE acknowledged = 0');
    res.json({
      stationCount: stationCount.count,
      buildingCount: buildingCount.count,
      userCount: userCount.count,
      alarmCount: alarmCount.count
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

app.get('/api/dashboard/station-status', async (req, res) => {
  try {
    const [[running]] = await pool.query('SELECT COUNT(*) as count FROM stations WHERE status = 1');
    const [[stopped]] = await pool.query('SELECT COUNT(*) as count FROM stations WHERE status = 0');
    res.json([
      { value: running.count || 0, name: '运行中', itemStyle: { color: '#67c23a' } },
      { value: stopped.count || 0, name: '停止', itemStyle: { color: '#909399' } },
      { value: 0, name: '故障', itemStyle: { color: '#f56c6c' } }
    ]);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.get('/api/dashboard/heat-load', (req, res) => {
  res.json(['00:00', '04:00', '08:00', '12:00', '16:00', '20:00'].map(() => Math.floor(Math.random() * 30) + 40));
});

app.get('/api/dashboard/alarms', async (req, res) => {
  try {
    const [rows] = await pool.query('SELECT * FROM alarms ORDER BY createTime DESC LIMIT 8');
    res.json(rows);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// 启动服务器
async function startServer() {
  try {
    await initDatabase();
    await insertDemoData();
    app.listen(PORT, '0.0.0.0', () => {
      console.log(`后端服务已启动: http://localhost:${PORT}`);
    });
  } catch (err) {
    console.error('启动失败:', err);
    process.exit(1);
  }
}

startServer();
