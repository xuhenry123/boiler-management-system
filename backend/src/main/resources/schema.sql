-- 锅炉集中供热智慧管理系统 - 数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS boiler_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE boiler_management;

-- ===============================
-- 热用户按需供热模块表结构
-- ===============================

-- 建筑物信息表
CREATE TABLE IF NOT EXISTS building (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    building_code VARCHAR(50) NOT NULL UNIQUE COMMENT '建筑编码',
    building_name VARCHAR(100) NOT NULL COMMENT '建筑名称',
    address VARCHAR(200) COMMENT '地址',
    area_total DECIMAL(10,2) COMMENT '建筑面积(㎡)',
    area_heated DECIMAL(10,2) COMMENT '供热面积(㎡)',
    build_year INT COMMENT '建造年份',
    building_type VARCHAR(20) COMMENT '建筑类型: residential/commercial/industrial',
    insulation_material VARCHAR(50) COMMENT '保温材料',
    window_area DECIMAL(8,2) COMMENT '窗户面积(㎡)',
    floor_height DECIMAL(5,2) COMMENT '层高(m)',
    heat_transfer_coefficient DECIMAL(6,4) COMMENT '传热系数K',
    heat_capacity DECIMAL(10,2) COMMENT '热容(kJ/℃)',
    station_id BIGINT COMMENT '关联换热站ID',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_building_code (building_code),
    INDEX idx_station_id (station_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='建筑物信息表';

-- 热用户表
CREATE TABLE IF NOT EXISTS heat_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_code VARCHAR(50) NOT NULL UNIQUE COMMENT '用户编码',
    user_name VARCHAR(100) COMMENT '用户名称',
    building_id BIGINT NOT NULL COMMENT '建筑物ID',
    unit_no VARCHAR(20) COMMENT '单元号',
    room_no VARCHAR(20) COMMENT '房间号',
    area DECIMAL(10,2) COMMENT '供热面积(㎡)',
    target_temp DECIMAL(5,2) DEFAULT 20.00 COMMENT '目标温度(℃)',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (building_id) REFERENCES building(id),
    INDEX idx_user_code (user_code),
    INDEX idx_building_id (building_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='热用户表';

-- 室内温度数据表
CREATE TABLE IF NOT EXISTS indoor_temperature (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    temperature DECIMAL(5,2) NOT NULL COMMENT '温度值(℃)',
    is_abnormal TINYINT DEFAULT 0 COMMENT '是否异常: 0-正常 1-异常',
    data_source VARCHAR(20) DEFAULT 'sensor' COMMENT '数据来源: sensor/manual',
    collect_time DATETIME NOT NULL COMMENT '采集时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES heat_user(id),
    INDEX idx_user_time (user_id, collect_time),
    INDEX idx_collect_time (collect_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='室内温度数据表';

-- 室外温度数据表
CREATE TABLE IF NOT EXISTS outdoor_temperature (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    predict_time DATETIME NOT NULL COMMENT '预测时间点',
    temperature DECIMAL(5,2) NOT NULL COMMENT '温度值(℃)',
    data_source VARCHAR(20) COMMENT '数据来源: api/manual',
    is_actual TINYINT DEFAULT 0 COMMENT '是否实际值: 0-预测 1-实际',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_predict_time (predict_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='室外温度数据表';

-- 供热需求数据表
CREATE TABLE IF NOT EXISTS heat_demand (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    demand_value DECIMAL(10,2) NOT NULL COMMENT '需求值(kW)',
    predicted_value DECIMAL(10,2) COMMENT '预测值(kW)',
    outdoor_temp DECIMAL(5,2) COMMENT '室外温度(℃)',
    calculate_time DATETIME NOT NULL COMMENT '计算时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES heat_user(id),
    INDEX idx_user_time (user_id, calculate_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供热需求数据表';

-- 用户行为画像表
CREATE TABLE IF NOT EXISTS user_behavior_profile (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL UNIQUE COMMENT '用户ID',
    preference_temp_min DECIMAL(5,2) COMMENT '偏好温度下限(℃)',
    preference_temp_max DECIMAL(5,2) COMMENT '偏好温度上限(℃)',
    typical_home_start TIME COMMENT '通常在家开始时间',
    typical_home_end TIME COMMENT '通常在家结束时间',
    away_pattern_count INT DEFAULT 0 COMMENT '离家模式次数',
    comfort_weight DECIMAL(3,2) DEFAULT 0.5 COMMENT '舒适度权重',
    energy_weight DECIMAL(3,2) DEFAULT 0.5 COMMENT '能耗权重',
    last_analyze_time DATETIME COMMENT '最后分析时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES heat_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行为画像表';

-- 预测模型版本表
CREATE TABLE IF NOT EXISTS prediction_model (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    model_type VARCHAR(20) NOT NULL COMMENT '模型类型: lstm/simple',
    model_version VARCHAR(50) NOT NULL COMMENT '模型版本',
    model_path VARCHAR(200) COMMENT '模型文件路径',
    train_start_time DATETIME COMMENT '训练开始时间',
    train_end_time DATETIME COMMENT '训练结束时间',
    accuracy DECIMAL(5,4) COMMENT '准确率',
    mse DECIMAL(10,6) COMMENT '均方误差',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-当前',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预测模型版本表';

-- 告警记录表
CREATE TABLE IF NOT EXISTS alarm_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    alarm_type VARCHAR(20) NOT NULL COMMENT '告警类型: low_temp/high_temp/predict_error',
    user_id BIGINT COMMENT '用户ID',
    building_id BIGINT COMMENT '建筑ID',
    alarm_level TINYINT COMMENT '告警级别: 1-Info 2-Warning 3-Critical',
    alarm_message VARCHAR(500) COMMENT '告警消息',
    alarm_value DECIMAL(10,2) COMMENT '告警值',
    threshold_value DECIMAL(10,2) COMMENT '阈值',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    acknowledged TINYINT DEFAULT 0 COMMENT '是否确认: 0-未确认 1-已确认',
    ack_time DATETIME COMMENT '确认时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_building_id (building_id),
    INDEX idx_alarm_type (alarm_type),
    INDEX idx_alarm_level (alarm_level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警记录表';

-- ===============================
-- 换热站自控模块表结构
-- ===============================

-- 换热站信息表
CREATE TABLE IF NOT EXISTS heat_station (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    station_code VARCHAR(50) NOT NULL UNIQUE COMMENT '换热站编码',
    station_name VARCHAR(100) NOT NULL COMMENT '换热站名称',
    address VARCHAR(200) COMMENT '地址',
    design_capacity DECIMAL(10,2) COMMENT '设计能力(MW)',
    design_flow DECIMAL(10,2) COMMENT '设计流量(t/h)',
    primary_supply_temp DECIMAL(5,2) COMMENT '一次侧供水温度(℃)',
    primary_return_temp DECIMAL(5,2) COMMENT '一次侧回水温度(℃)',
    secondary_supply_temp DECIMAL(5,2) COMMENT '二次侧供水温度(℃)',
    secondary_return_temp DECIMAL(5,2) COMMENT '二次侧回水温度(℃)',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-停运 1-运行 2-调试',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_station_code (station_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='换热站信息表';

-- 换热站实时数据表
CREATE TABLE IF NOT EXISTS station_realtime_data (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    station_id BIGINT NOT NULL COMMENT '换热站ID',
    primary_supply_pressure DECIMAL(7,2) COMMENT '一次侧供水压力(MPa)',
    primary_return_pressure DECIMAL(7,2) COMMENT '一次侧回水压力(MPa)',
    primary_supply_temp DECIMAL(5,2) COMMENT '一次侧供水温度(℃)',
    primary_return_temp DECIMAL(5,2) COMMENT '一次侧回水温度(℃)',
    secondary_supply_pressure DECIMAL(7,2) COMMENT '二次侧供水压力(MPa)',
    secondary_return_pressure DECIMAL(7,2) COMMENT '二次侧回水压力(MPa)',
    secondary_supply_temp DECIMAL(5,2) COMMENT '二次侧供水温度(℃)',
    secondary_return_temp DECIMAL(5,2) COMMENT '二次侧回水温度(℃)',
    primary_flow DECIMAL(10,2) COMMENT '一次侧流量(t/h)',
    secondary_flow DECIMAL(10,2) COMMENT '二次侧流量(t/h)',
    pump_speed_ratio DECIMAL(5,4) COMMENT '循环泵转速比',
    valve_open_ratio DECIMAL(5,4) COMMENT '电动调节阀开度',
    heat_power DECIMAL(10,2) COMMENT '换热功率(MW)',
    collect_time DATETIME NOT NULL COMMENT '采集时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (station_id) REFERENCES heat_station(id),
    INDEX idx_station_time (station_id, collect_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='换热站实时数据表';

-- PID控制参数表
CREATE TABLE IF NOT EXISTS pid_parameters (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    station_id BIGINT NOT NULL COMMENT '换热站ID',
    control_type VARCHAR(20) NOT NULL COMMENT '控制类型: temp/pressure/flow',
    kp DECIMAL(8,4) COMMENT '比例系数Kp',
    ki DECIMAL(8,4) COMMENT '积分系数Ki',
    kd DECIMAL(8,4) COMMENT '微分系数Kd',
    setpoint DECIMAL(8,2) COMMENT '设定值',
    output_min DECIMAL(8,2) COMMENT '输出下限',
    output_max DECIMAL(8,2) COMMENT '输出上限',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (station_id) REFERENCES heat_station(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='PID控制参数表';

-- ===============================
-- 二网平衡模块表结构
-- ===============================

-- 管网节点表
CREATE TABLE IF NOT EXISTS pipe_network_node (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    node_code VARCHAR(50) NOT NULL UNIQUE COMMENT '节点编码',
    node_name VARCHAR(100) NOT NULL COMMENT '节点名称',
    node_type VARCHAR(20) NOT NULL COMMENT '节点类型: source/station/building/valve',
    station_id BIGINT COMMENT '换热站ID',
    building_id BIGINT COMMENT '建筑物ID',
    pipe_diameter DECIMAL(6,2) COMMENT '管道直径(mm)',
    pipe_length DECIMAL(8,2) COMMENT '管道长度(m)',
    elevation DECIMAL(6,2) COMMENT '标高(m)',
    x_coord DECIMAL(10,6) COMMENT 'X坐标',
    y_coord DECIMAL(10,6) COMMENT 'Y坐标',
    status TINYINT DEFAULT 1 COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_node_code (node_code),
    INDEX idx_node_type (node_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管网节点表';

-- 管网管段表
CREATE TABLE IF NOT EXISTS pipe_segment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    segment_code VARCHAR(50) NOT NULL UNIQUE COMMENT '管段编码',
    segment_name VARCHAR(100) COMMENT '管段名称',
    start_node_id BIGINT NOT NULL COMMENT '起点节点ID',
    end_node_id BIGINT NOT NULL COMMENT '终点节点ID',
    pipe_type VARCHAR(20) COMMENT '管道类型',
    diameter DECIMAL(6,2) COMMENT '管径(mm)',
    length DECIMAL(8,2) COMMENT '长度(m)',
    roughness DECIMAL(6,4) COMMENT '粗糙度',
    design_flow DECIMAL(10,2) COMMENT '设计流量(t/h)',
    design_velocity DECIMAL(5,2) COMMENT '设计流速(m/s)',
    status TINYINT DEFAULT 1 COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (start_node_id) REFERENCES pipe_network_node(id),
    FOREIGN KEY (end_node_id) REFERENCES pipe_network_node(id),
    INDEX idx_segment_code (segment_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管网管段表';

-- 阀门设备表
CREATE TABLE IF NOT EXISTS valve_device (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    valve_code VARCHAR(50) NOT NULL UNIQUE COMMENT '阀门编码',
    valve_name VARCHAR(100) NOT NULL COMMENT '阀门名称',
    valve_type VARCHAR(20) NOT NULL COMMENT '阀门类型: control/isolation/balancing',
    node_id BIGINT NOT NULL COMMENT '所属节点ID',
    manufacturer VARCHAR(100) COMMENT '生产厂家',
    model VARCHAR(50) COMMENT '型号',
    diameter DECIMAL(6,2) COMMENT '口径(mm)',
    open_ratio DECIMAL(5,4) DEFAULT 0 COMMENT '开度(0-1)',
    position_limit_min DECIMAL(5,4) DEFAULT 0 COMMENT '最小开度',
    position_limit_max DECIMAL(5,4) DEFAULT 1 COMMENT '最大开度',
    response_time DECIMAL(6,2) COMMENT '响应时间(s)',
    control_protocol VARCHAR(20) COMMENT '控制协议: mqtt/modbus',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-故障 1-正常 2-调试',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (node_id) REFERENCES pipe_network_node(id),
    INDEX idx_valve_code (valve_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='阀门设备表';

-- 平衡优化结果表
CREATE TABLE IF NOT EXISTS balance_optimization_result (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    station_id BIGINT NOT NULL COMMENT '换热站ID',
    optimization_type VARCHAR(20) NOT NULL COMMENT '优化类型: ga/pso/manual',
    valve_open_ratios TEXT COMMENT '阀门开度JSON',
    objective_value DECIMAL(10,4) COMMENT '目标函数值',
    iteration_count INT COMMENT '迭代次数',
    execution_time INT COMMENT '执行时间(ms)',
    calculate_time DATETIME NOT NULL COMMENT '计算时间',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-失败 1-成功',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_station_time (station_id, calculate_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='平衡优化结果表';

-- ===============================
-- 热源调控模块表结构
-- ===============================

-- 锅炉信息表
CREATE TABLE IF NOT EXISTS boiler (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    boiler_code VARCHAR(50) NOT NULL UNIQUE COMMENT '锅炉编码',
    boiler_name VARCHAR(100) NOT NULL COMMENT '锅炉名称',
    boiler_type VARCHAR(20) COMMENT '锅炉类型: gas/oil/coal/electric',
    manufacturer VARCHAR(100) COMMENT '生产厂家',
    model VARCHAR(50) COMMENT '型号',
    rated_capacity DECIMAL(10,2) COMMENT '额定出力(MW)',
    rated_pressure DECIMAL(6,2) COMMENT '额定压力(MPa)',
    design_efficiency DECIMAL(5,4) COMMENT '设计效率',
    fuel_type VARCHAR(20) COMMENT '燃料类型',
    status TINYINT DEFAULT 0 COMMENT '状态: 0-停机 1-运行 2-调试 3-故障',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_boiler_code (boiler_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='锅炉信息表';

-- 锅炉运行数据表
CREATE TABLE IF NOT EXISTS boiler_realtime_data (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    boiler_id BIGINT NOT NULL COMMENT '锅炉ID',
    supply_water_temp DECIMAL(5,2) COMMENT '供水温度(℃)',
    return_water_temp DECIMAL(5,2) COMMENT '回水温度(℃)',
    flue_gas_temp DECIMAL(5,2) COMMENT '排烟温度(℃)',
    air_temp DECIMAL(5,2) COMMENT '助燃空气温度(℃)',
    oxygen_content DECIMAL(5,2) COMMENT '氧含量(%)',
    load_ratio DECIMAL(5,4) COMMENT '负荷率',
    fuel_consumption DECIMAL(10,2) COMMENT '燃料消耗量',
    power_consumption DECIMAL(10,2) COMMENT '用电量(kWh)',
    steam_pressure DECIMAL(6,2) COMMENT '蒸汽压力(MPa)',
    efficiency DECIMAL(5,4) COMMENT '运行效率',
    collect_time DATETIME NOT NULL COMMENT '采集时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (boiler_id) REFERENCES boiler(id),
    INDEX idx_boiler_time (boiler_id, collect_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='锅炉运行数据表';

-- ===============================
-- 系统管理表结构
-- ===============================

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(200) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    dept_id BIGINT COMMENT '部门ID',
    role_id BIGINT COMMENT '角色ID',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) COMMENT '操作用户',
    operation_module VARCHAR(50) COMMENT '操作模块',
    operation_type VARCHAR(20) COMMENT '操作类型',
    operation_desc VARCHAR(200) COMMENT '操作描述',
    request_url VARCHAR(200) COMMENT '请求URL',
    request_method VARCHAR(10) COMMENT '请求方法',
    request_params TEXT COMMENT '请求参数',
    response_result TEXT COMMENT '响应结果',
    execution_time INT COMMENT '执行时间(ms)',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_username (username),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- ===============================
-- 初始化测试数据
-- ===============================

-- 插入换热站测试数据
INSERT INTO heat_station (station_code, station_name, address, design_capacity, design_flow, primary_supply_temp, primary_return_temp, secondary_supply_temp, secondary_return_temp, status) VALUES
('HS001', '东城区换热站', '北京市东城区', 50.00, 800.00, 120.00, 70.00, 50.00, 40.00, 1),
('HS002', '西城区换热站', '北京市西城区', 40.00, 650.00, 120.00, 70.00, 50.00, 40.00, 1),
('HS003', '朝阳区换热站', '北京市朝阳区', 60.00, 1000.00, 120.00, 70.00, 50.00, 40.00, 1);

-- 插入建筑物测试数据
INSERT INTO building (building_code, building_name, address, area_total, area_heated, build_year, building_type, heat_transfer_coefficient, station_id, status) VALUES
('BLD001', '阳光花园小区1号楼', '东城区阳光路1号', 15000.00, 12000.00, 2015, 'residential', 1.20, 1, 1),
('BLD002', '阳光花园小区2号楼', '东城区阳光路2号', 18000.00, 15000.00, 2015, 'residential', 1.15, 1, 1),
('BLD003', '商业大厦A座', '西城区金融街8号', 25000.00, 20000.00, 2018, 'commercial', 0.90, 2, 1),
('BLD004', '科技园区办公楼1', '朝阳区中关村大街1号', 30000.00, 25000.00, 2020, 'commercial', 0.85, 3, 1);

-- 插入热用户测试数据
INSERT INTO heat_user (user_code, user_name, building_id, unit_no, room_no, area, target_temp, status) VALUES
('U001', '张三', 1, '1', '101', 120.00, 20.00, 1),
('U002', '李四', 1, '1', '102', 120.00, 21.00, 1),
('U003', '王五', 1, '1', '201', 130.00, 20.00, 1),
('U004', '赵六', 2, '2', '301', 140.00, 22.00, 1),
('U005', '钱七', 3, 'A', '1001', 200.00, 20.00, 1),
('U006', '孙八', 3, 'A', '1002', 180.00, 20.00, 1),
('U007', '周九', 4, 'B', '2001', 250.00, 21.00, 1),
('U008', '吴十', 4, 'B', '2002', 250.00, 20.00, 1);

-- 插入室内温度测试数据（过去24小时）
INSERT INTO indoor_temperature (user_id, temperature, is_abnormal, data_source, collect_time) VALUES
(1, 19.5, 0, 'sensor', NOW() - INTERVAL 1 HOUR),
(1, 19.8, 0, 'sensor', NOW() - INTERVAL 2 HOUR),
(1, 20.0, 0, 'sensor', NOW() - INTERVAL 3 HOUR),
(2, 20.5, 0, 'sensor', NOW() - INTERVAL 1 HOUR),
(2, 20.8, 0, 'sensor', NOW() - INTERVAL 2 HOUR),
(2, 21.0, 0, 'sensor', NOW() - INTERVAL 3 HOUR),
(3, 19.0, 0, 'sensor', NOW() - INTERVAL 1 HOUR),
(3, 19.2, 0, 'sensor', NOW() - INTERVAL 2 HOUR),
(3, 19.5, 0, 'sensor', NOW() - INTERVAL 3 HOUR),
(4, 21.5, 0, 'sensor', NOW() - INTERVAL 1 HOUR),
(4, 21.8, 0, 'sensor', NOW() - INTERVAL 2 HOUR),
(4, 22.0, 0, 'sensor', NOW() - INTERVAL 3 HOUR);

-- 插入室外温度测试数据
INSERT INTO outdoor_temperature (predict_time, temperature, data_source, is_actual) VALUES
(DATE_ADD(NOW(), INTERVAL -3 HOUR), -3.5, 'weather_api', 1),
(DATE_ADD(NOW(), INTERVAL -2 HOUR), -3.0, 'weather_api', 1),
(DATE_ADD(NOW(), INTERVAL -1 HOUR), -2.5, 'weather_api', 1),
(NOW(), -2.0, 'weather_api', 1),
(DATE_ADD(NOW(), INTERVAL 1 HOUR), -1.5, 'forecast', 0),
(DATE_ADD(NOW(), INTERVAL 2 HOUR), -1.0, 'forecast', 0),
(DATE_ADD(NOW(), INTERVAL 3 HOUR), -0.5, 'forecast', 0),
(DATE_ADD(NOW(), INTERVAL 4 HOUR), 0.0, 'forecast', 0),
(DATE_ADD(NOW(), INTERVAL 5 HOUR), 0.5, 'forecast', 0),
(DATE_ADD(NOW(), INTERVAL 6 HOUR), 1.0, 'forecast', 0);

-- 插入锅炉测试数据
INSERT INTO boiler (boiler_code, boiler_name, boiler_type, manufacturer, model, rated_capacity, rated_pressure, design_efficiency, status) VALUES
('BLR001', '1号燃气锅炉', 'gas', '某锅炉厂', 'WNS10-1.0', 10.00, 1.00, 0.95, 1),
('BLR002', '2号燃气锅炉', 'gas', '某锅炉厂', 'WNS10-1.0', 10.00, 1.00, 0.95, 1),
('BLR003', '3号燃气锅炉', 'gas', '某锅炉厂', 'WNS15-1.0', 15.00, 1.00, 0.94, 0);

-- 插入阀门设备测试数据
INSERT INTO valve_device (valve_code, valve_name, valve_type, node_id, manufacturer, model, diameter, open_ratio, control_protocol, status) VALUES
('V001', '1号楼入口调节阀', 'control', 1, '某阀门厂', CV3000, 150, 0.75, 'modbus', 1),
('V002', '2号楼入口调节阀', 'control', 2, '某阀门厂', CV3000, 150, 0.80, 'modbus', 1),
('V003', '3号楼入口调节阀', 'control', 3, '某阀门厂', CV3000, 200, 0.70, 'mqtt', 1),
('V004', '4号楼入口调节阀', 'control', 4, '某阀门厂', CV3000, 200, 0.65, 'mqtt', 1);

-- 插入预测模型测试数据
INSERT INTO prediction_model (model_type, model_version, accuracy, mse, status) VALUES
('lstm', 'v1.0.0', 0.9235, 0.562, 1),
('simple', 'v1.0.0', 0.8500, 1.230, 0);

-- 插入系统用户测试数据（密码为 admin123）
INSERT INTO sys_user (username, password, real_name, phone, role_id, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', '13800138000', 1, 1),
('operator', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '操作员', '13800138001', 2, 1);

-- 异常状态预测模块表结构
-- ===============================

-- 监测数据表
CREATE TABLE IF NOT EXISTS monitor_data (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    device_id VARCHAR(50) NOT NULL COMMENT '设备ID',
    device_type VARCHAR(50) COMMENT '设备类型: temperature_sensor/pressure_sensor/flow_meter/valve',
    metric_type VARCHAR(50) COMMENT '指标类型: temperature/pressure/flow',
    metric_value DECIMAL(10,4) COMMENT '指标值',
    timestamp DATETIME COMMENT '采集时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_device_id (device_id),
    INDEX idx_device_type (device_type),
    INDEX idx_metric_type (metric_type),
    INDEX idx_timestamp (timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='监测数据表';

-- 异常记录表
CREATE TABLE IF NOT EXISTS anomaly_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    device_id VARCHAR(50) NOT NULL COMMENT '设备ID',
    device_name VARCHAR(100) COMMENT '设备名称',
    metric_type VARCHAR(50) COMMENT '指标类型',
    metric_value DECIMAL(10,4) COMMENT '异常值',
    threshold DECIMAL(10,4) COMMENT '阈值',
    anomaly_score DECIMAL(5,4) COMMENT '异常分数',
    anomaly_type VARCHAR(50) COMMENT '异常类型: threshold/statistical/trend/prediction',
    detection_method VARCHAR(50) COMMENT '检测方法: rule/isolation_forest/lstm/prophet',
    severity INT DEFAULT 1 COMMENT '严重程度: 1-轻微 2-一般 3-严重',
    description VARCHAR(500) COMMENT '异常描述',
    advice VARCHAR(500) COMMENT '处理建议',
    detected_at DATETIME COMMENT '检测时间',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态: PENDING/CONFIRMED/RESOLVED',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_device_id (device_id),
    INDEX idx_metric_type (metric_type),
    INDEX idx_severity (severity),
    INDEX idx_status (status),
    INDEX idx_detected_at (detected_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='异常记录表';

-- 告警配置表
CREATE TABLE IF NOT EXISTS alert_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    config_name VARCHAR(100) NOT NULL COMMENT '配置名称',
    device_type VARCHAR(50) COMMENT '设备类型',
    metric_type VARCHAR(50) COMMENT '指标类型',
    threshold_min DECIMAL(10,4) COMMENT '最小阈值',
    threshold_max DECIMAL(10,4) COMMENT '最大阈值',
    severity INT DEFAULT 2 COMMENT '告警级别: 1-INFO 2-WARN 3-CRITICAL',
    alert_enabled TINYINT DEFAULT 1 COMMENT '是否启用告警',
    wechat_enabled TINYINT DEFAULT 1 COMMENT '是否启用微信通知',
    alert_message VARCHAR(500) COMMENT '告警消息模板',
    advice VARCHAR(500) COMMENT '处理建议',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_device_type (device_type),
    INDEX idx_metric_type (metric_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警配置表';

-- 告警记录表
CREATE TABLE IF NOT EXISTS alert_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    anomaly_id BIGINT COMMENT '关联异常ID',
    alert_level VARCHAR(20) COMMENT '告警级别: INFO/WARN/CRITICAL',
    alert_message VARCHAR(500) COMMENT '告警消息',
    alert_time DATETIME COMMENT '告警时间',
    acknowledged TINYINT DEFAULT 0 COMMENT '是否已确认: 0-否 1-是',
    acknowledged_by VARCHAR(50) COMMENT '确认人',
    acknowledged_at DATETIME COMMENT '确认时间',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_anomaly_id (anomaly_id),
    INDEX idx_alert_level (alert_level),
    INDEX idx_alert_time (alert_time),
    INDEX idx_acknowledged (acknowledged)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警记录表';

-- 插入默认告警配置数据
INSERT INTO alert_config (config_name, device_type, metric_type, threshold_min, threshold_max, severity, alert_enabled, wechat_enabled, alert_message, advice) VALUES
('温度上限告警', 'temperature_sensor', 'temperature', NULL, 30.00, 3, 1, 1, '温度超过上限 {threshold}°C', '检查供热系统是否过热'),
('温度下限告警', 'temperature_sensor', 'temperature', 14.00, NULL, 3, 1, 1, '温度低于下限 {threshold}°C', '检查供热系统是否正常'),
('压力上限告警', 'pressure_sensor', 'pressure', NULL, 1.60, 3, 1, 1, '压力超过上限 {threshold}MPa', '检查管网是否超压'),
('压力下限告警', 'pressure_sensor', 'pressure', 0.40, NULL, 3, 1, 1, '压力低于下限 {threshold}MPa', '检查管网是否泄漏'),
('流量异常告警', 'flow_meter', 'flow', 0.50, 10.00, 2, 1, 1, '流量超出正常范围', '检查管网平衡');

-- ===============================
-- 蒸汽管理模块表结构
-- ===============================

-- 蒸汽锅炉设备表
CREATE TABLE IF NOT EXISTS steam_boiler (
    id VARCHAR(32) PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '锅炉名称',
    enterprise_id VARCHAR(32) NOT NULL COMMENT '企业ID',
    enterprise_name VARCHAR(100) COMMENT '企业名称',
    capacity DECIMAL(10,2) COMMENT '额定蒸发量(吨/小时)',
    model VARCHAR(100) COMMENT '型号',
    status VARCHAR(20) DEFAULT 'offline' COMMENT '状态: online/offline/maintenance',
    max_pressure DECIMAL(10,2) COMMENT '最大工作压力(MPa)',
    min_pressure DECIMAL(10,2) COMMENT '最小工作压力(MPa)',
    rated_temperature DECIMAL(10,2) COMMENT '额定温度(°C)',
    install_date DATE COMMENT '安装日期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_enterprise_id (enterprise_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='蒸汽锅炉设备表';

-- 蒸汽数据点表（时序数据）
CREATE TABLE IF NOT EXISTS steam_data_point (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    boiler_id VARCHAR(32) NOT NULL COMMENT '锅炉ID',
    steam_pressure DECIMAL(10,2) COMMENT '蒸汽压力(MPa)',
    steam_temperature DECIMAL(10,2) COMMENT '蒸汽温度(°C)',
    steam_flow DECIMAL(10,2) COMMENT '蒸汽流量(吨/小时)',
    water_consumption DECIMAL(10,2) COMMENT '用水量(吨)',
    electricity_consumption DECIMAL(10,2) COMMENT '用电量(kWh)',
    efficiency DECIMAL(5,2) COMMENT '效率(%)',
    data_time TIMESTAMP NOT NULL COMMENT '数据时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_boiler_time (boiler_id, data_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='蒸汽数据点表';

-- 用户画像表
CREATE TABLE IF NOT EXISTS user_profile (
    id VARCHAR(32) PRIMARY KEY COMMENT '主键ID',
    enterprise_id VARCHAR(32) NOT NULL UNIQUE COMMENT '企业ID',
    enterprise_name VARCHAR(100) COMMENT '企业名称',
    production_pattern JSON COMMENT '生产规律特征',
    energy_features JSON COMMENT '能耗特征',
    profile_score INT DEFAULT 0 COMMENT '画像评分',
    last_updated DATETIME COMMENT '最后更新时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_enterprise_id (enterprise_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户画像表';

-- 能耗记录表
CREATE TABLE IF NOT EXISTS energy_consumption (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    enterprise_id VARCHAR(32) NOT NULL COMMENT '企业ID',
    enterprise_name VARCHAR(100) COMMENT '企业名称',
    steam_amount DECIMAL(10,2) COMMENT '蒸汽用量(吨)',
    water_amount DECIMAL(10,2) COMMENT '用水量(吨)',
    electricity_amount DECIMAL(10,2) COMMENT '用电量(kWh)',
    standard_coal DECIMAL(10,2) COMMENT '标煤(吨)',
    record_date DATE NOT NULL COMMENT '记录日期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_enterprise_date (enterprise_id, record_date),
    INDEX idx_enterprise_id (enterprise_id),
    INDEX idx_record_date (record_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='能耗记录表';

-- 锅炉运行策略表
CREATE TABLE IF NOT EXISTS steam_strategy (
    id VARCHAR(32) PRIMARY KEY COMMENT '主键ID',
    boiler_id VARCHAR(32) NOT NULL COMMENT '锅炉ID',
    strategy_type VARCHAR(20) COMMENT '策略类型: power/schedule/optimize',
    target_power DECIMAL(10,2) COMMENT '目标功率',
    schedule JSON COMMENT '运行计划',
    status VARCHAR(20) DEFAULT 'draft' COMMENT '状态: draft/active/suspended',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_boiler_id (boiler_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='锅炉运行策略表';

-- 蒸汽告警记录表
CREATE TABLE IF NOT EXISTS steam_alarm (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    boiler_id VARCHAR(32) COMMENT '锅炉ID',
    enterprise_id VARCHAR(32) COMMENT '企业ID',
    alarm_type VARCHAR(20) NOT NULL COMMENT '告警类型: pressure_high/pressure_low/temp_high/efficiency_low/offline',
    alarm_level TINYINT NOT NULL COMMENT '告警级别: 1-Info 2-Warning 3-Critical',
    alarm_message VARCHAR(500) COMMENT '告警消息',
    alarm_value DECIMAL(10,2) COMMENT '告警值',
    threshold_value DECIMAL(10,2) COMMENT '阈值',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    acknowledged TINYINT DEFAULT 0 COMMENT '是否确认: 0-未确认 1-已确认',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_boiler_id (boiler_id),
    INDEX idx_enterprise_id (enterprise_id),
    INDEX idx_alarm_type (alarm_type),
    INDEX idx_alarm_level (alarm_level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='蒸汽告警记录表';
