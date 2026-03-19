-- 锅炉集中供热智慧管理系统 - SQLite 数据库初始化脚本

-- 建筑物信息表
CREATE TABLE IF NOT EXISTS building (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    building_code VARCHAR(50) NOT NULL UNIQUE,
    building_name VARCHAR(100) NOT NULL,
    address VARCHAR(200),
    area_total DECIMAL(10,2),
    area_heated DECIMAL(10,2),
    build_year INTEGER,
    building_type VARCHAR(20),
    insulation_material VARCHAR(50),
    window_area DECIMAL(8,2),
    floor_height DECIMAL(5,2),
    heat_transfer_coefficient DECIMAL(6,4),
    heat_capacity DECIMAL(10,2),
    station_id INTEGER,
    status INTEGER DEFAULT 1,
    create_time TEXT DEFAULT (datetime('now', 'localtime')),
    update_time TEXT DEFAULT (datetime('now', 'localtime'))
);

-- 热用户表
CREATE TABLE IF NOT EXISTS heat_user (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_code VARCHAR(50) NOT NULL UNIQUE,
    user_name VARCHAR(100),
    building_id INTEGER NOT NULL,
    unit_no VARCHAR(20),
    room_no VARCHAR(20),
    area DECIMAL(10,2),
    target_temp DECIMAL(5,2) DEFAULT 20.00,
    status INTEGER DEFAULT 1,
    create_time TEXT DEFAULT (datetime('now', 'localtime')),
    update_time TEXT DEFAULT (datetime('now', 'localtime')),
    FOREIGN KEY (building_id) REFERENCES building(id)
);

-- 室内温度数据表
CREATE TABLE IF NOT EXISTS indoor_temperature (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    temperature DECIMAL(5,2) NOT NULL,
    is_abnormal INTEGER DEFAULT 0,
    data_source VARCHAR(20) DEFAULT 'sensor',
    collect_time TEXT NOT NULL,
    create_time TEXT DEFAULT (datetime('now', 'localtime')),
    FOREIGN KEY (user_id) REFERENCES heat_user(id)
);

-- 室外温度数据表
CREATE TABLE IF NOT EXISTS outdoor_temperature (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    predict_time TEXT NOT NULL,
    temperature DECIMAL(5,2) NOT NULL,
    data_source VARCHAR(20),
    is_actual INTEGER DEFAULT 0,
    create_time TEXT DEFAULT (datetime('now', 'localtime'))
);

-- 供热需求数据表
CREATE TABLE IF NOT EXISTS heat_demand (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    demand_value DECIMAL(10,2) NOT NULL,
    predicted_value DECIMAL(10,2),
    outdoor_temp DECIMAL(5,2),
    calculate_time TEXT NOT NULL,
    create_time TEXT DEFAULT (datetime('now', 'localtime')),
    FOREIGN KEY (user_id) REFERENCES heat_user(id)
);

-- 用户行为画像表
CREATE TABLE IF NOT EXISTS user_behavior_profile (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL UNIQUE,
    preference_temp_min DECIMAL(5,2),
    preference_temp_max DECIMAL(5,2),
    typical_home_start TEXT,
    typical_home_end TEXT,
    away_pattern_count INTEGER DEFAULT 0,
    comfort_weight DECIMAL(3,2) DEFAULT 0.5,
    energy_weight DECIMAL(3,2) DEFAULT 0.5,
    last_analyze_time TEXT,
    create_time TEXT DEFAULT (datetime('now', 'localtime')),
    update_time TEXT DEFAULT (datetime('now', 'localtime')),
    FOREIGN KEY (user_id) REFERENCES heat_user(id)
);

-- 预测模型版本表
CREATE TABLE IF NOT EXISTS prediction_model (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    model_type VARCHAR(20) NOT NULL,
    model_version VARCHAR(50) NOT NULL,
    model_path VARCHAR(200),
    train_start_time TEXT,
    train_end_time TEXT,
    accuracy DECIMAL(5,4),
    mse DECIMAL(10,6),
    status INTEGER DEFAULT 1,
    create_time TEXT DEFAULT (datetime('now', 'localtime'))
);

-- 告警记录表
CREATE TABLE IF NOT EXISTS alarm_record (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    alarm_type VARCHAR(20) NOT NULL,
    user_id INTEGER,
    building_id INTEGER,
    alarm_level INTEGER,
    alarm_message VARCHAR(500),
    alarm_value DECIMAL(10,2),
    threshold_value DECIMAL(10,2),
    start_time TEXT,
    end_time TEXT,
    acknowledged INTEGER DEFAULT 0,
    ack_time TEXT,
    create_time TEXT DEFAULT (datetime('now', 'localtime'))
);

-- 换热站信息表
CREATE TABLE IF NOT EXISTS heat_station (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    station_code VARCHAR(50) NOT NULL UNIQUE,
    station_name VARCHAR(100) NOT NULL,
    address VARCHAR(200),
    design_capacity DECIMAL(10,2),
    design_flow DECIMAL(10,2),
    primary_supply_temp DECIMAL(5,2),
    primary_return_temp DECIMAL(5,2),
    secondary_supply_temp DECIMAL(5,2),
    secondary_return_temp DECIMAL(5,2),
    status INTEGER DEFAULT 1,
    create_time TEXT DEFAULT (datetime('now', 'localtime')),
    update_time TEXT DEFAULT (datetime('now', 'localtime'))
);

-- 换热站实时数据表
CREATE TABLE IF NOT EXISTS station_realtime_data (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    station_id INTEGER NOT NULL,
    primary_supply_pressure DECIMAL(7,2),
    primary_return_pressure DECIMAL(7,2),
    primary_supply_temp DECIMAL(5,2),
    primary_return_temp DECIMAL(5,2),
    secondary_supply_pressure DECIMAL(7,2),
    secondary_return_pressure DECIMAL(7,2),
    secondary_supply_temp DECIMAL(5,2),
    secondary_return_temp DECIMAL(5,2),
    primary_flow DECIMAL(10,2),
    secondary_flow DECIMAL(10,2),
    pump_speed_ratio DECIMAL(5,4),
    valve_open_ratio DECIMAL(5,4),
    heat_power DECIMAL(10,2),
    collect_time TEXT NOT NULL,
    create_time TEXT DEFAULT (datetime('now', 'localtime')),
    FOREIGN KEY (station_id) REFERENCES heat_station(id)
);

-- PID控制参数表
CREATE TABLE IF NOT EXISTS pid_parameters (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    station_id INTEGER NOT NULL,
    control_type VARCHAR(20) NOT NULL,
    kp DECIMAL(8,4),
    ki DECIMAL(8,4),
    kd DECIMAL(8,4),
    setpoint DECIMAL(8,2),
    output_min DECIMAL(8,2),
    output_max DECIMAL(8,2),
    status INTEGER DEFAULT 1,
    create_time TEXT DEFAULT (datetime('now', 'localtime')),
    update_time TEXT DEFAULT (datetime('now', 'localtime')),
    FOREIGN KEY (station_id) REFERENCES heat_station(id)
);

-- 管网节点表
CREATE TABLE IF NOT EXISTS pipe_network_node (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    node_code VARCHAR(50) NOT NULL UNIQUE,
    node_name VARCHAR(100) NOT NULL,
    node_type VARCHAR(20) NOT NULL,
    station_id INTEGER,
    building_id INTEGER,
    pipe_diameter DECIMAL(6,2),
    pipe_length DECIMAL(8,2),
    elevation DECIMAL(6,2),
    x_coord DECIMAL(10,6),
    y_coord DECIMAL(10,6),
    status INTEGER DEFAULT 1,
    create_time TEXT DEFAULT (datetime('now', 'localtime')),
    update_time TEXT DEFAULT (datetime('now', 'localtime'))
);

-- 管网管段表
CREATE TABLE IF NOT EXISTS pipe_segment (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    segment_code VARCHAR(50) NOT NULL UNIQUE,
    segment_name VARCHAR(100),
    start_node_id INTEGER NOT NULL,
    end_node_id INTEGER NOT NULL,
    pipe_type VARCHAR(20),
    diameter DECIMAL(6,2),
    length DECIMAL(8,2),
    roughness DECIMAL(6,4),
    design_flow DECIMAL(10,2),
    design_velocity DECIMAL(5,2),
    status INTEGER DEFAULT 1,
    create_time TEXT DEFAULT (datetime('now', 'localtime')),
    update_time TEXT DEFAULT (datetime('now', 'localtime')),
    FOREIGN KEY (start_node_id) REFERENCES pipe_network_node(id),
    FOREIGN KEY (end_node_id) REFERENCES pipe_network_node(id)
);

-- 阀门设备表
CREATE TABLE IF NOT EXISTS valve_device (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    valve_code VARCHAR(50) NOT NULL UNIQUE,
    valve_name VARCHAR(100) NOT NULL,
    valve_type VARCHAR(20) NOT NULL,
    node_id INTEGER NOT NULL,
    manufacturer VARCHAR(100),
    model VARCHAR(50),
    diameter DECIMAL(6,2),
    open_ratio DECIMAL(5,4) DEFAULT 0,
    position_limit_min DECIMAL(5,4) DEFAULT 0,
    position_limit_max DECIMAL(5,4) DEFAULT 1,
    response_time DECIMAL(6,2),
    control_protocol VARCHAR(20),
    status INTEGER DEFAULT 1,
    create_time TEXT DEFAULT (datetime('now', 'localtime')),
    update_time TEXT DEFAULT (datetime('now', 'localtime')),
    FOREIGN KEY (node_id) REFERENCES pipe_network_node(id)
);

-- 平衡优化结果表
CREATE TABLE IF NOT EXISTS balance_optimization_result (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    station_id INTEGER NOT NULL,
    optimization_type VARCHAR(20) NOT NULL,
    valve_open_ratios TEXT,
    objective_value DECIMAL(10,4),
    iteration_count INTEGER,
    execution_time INTEGER,
    calculate_time TEXT NOT NULL,
    status INTEGER DEFAULT 1,
    create_time TEXT DEFAULT (datetime('now', 'localtime'))
);

-- 锅炉信息表
CREATE TABLE IF NOT EXISTS boiler (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    boiler_code VARCHAR(50) NOT NULL UNIQUE,
    boiler_name VARCHAR(100) NOT NULL,
    boiler_type VARCHAR(20),
    manufacturer VARCHAR(100),
    model VARCHAR(50),
    rated_capacity DECIMAL(10,2),
    rated_pressure DECIMAL(6,2),
    design_efficiency DECIMAL(5,4),
    fuel_type VARCHAR(20),
    status INTEGER DEFAULT 0,
    create_time TEXT DEFAULT (datetime('now', 'localtime')),
    update_time TEXT DEFAULT (datetime('now', 'localtime'))
);

-- 锅炉运行数据表
CREATE TABLE IF NOT EXISTS boiler_realtime_data (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    boiler_id INTEGER NOT NULL,
    supply_water_temp DECIMAL(5,2),
    return_water_temp DECIMAL(5,2),
    flue_gas_temp DECIMAL(5,2),
    air_temp DECIMAL(5,2),
    oxygen_content DECIMAL(5,2),
    load_ratio DECIMAL(5,4),
    fuel_consumption DECIMAL(10,2),
    power_consumption DECIMAL(10,2),
    steam_pressure DECIMAL(6,2),
    efficiency DECIMAL(5,4),
    collect_time TEXT NOT NULL,
    create_time TEXT DEFAULT (datetime('now', 'localtime')),
    FOREIGN KEY (boiler_id) REFERENCES boiler(id)
);

-- 系统用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(200) NOT NULL,
    real_name VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    dept_id INTEGER,
    role_id INTEGER,
    status INTEGER DEFAULT 1,
    create_time TEXT DEFAULT (datetime('now', 'localtime')),
    update_time TEXT DEFAULT (datetime('now', 'localtime'))
);

-- 操作日志表
CREATE TABLE IF NOT EXISTS operation_log (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username VARCHAR(50),
    operation_module VARCHAR(50),
    operation_type VARCHAR(20),
    operation_desc VARCHAR(200),
    request_url VARCHAR(200),
    request_method VARCHAR(10),
    request_params TEXT,
    response_result TEXT,
    execution_time INTEGER,
    ip_address VARCHAR(50),
    create_time TEXT DEFAULT (datetime('now', 'localtime'))
);

-- 监测数据表
CREATE TABLE IF NOT EXISTS monitor_data (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    device_id VARCHAR(50) NOT NULL,
    device_type VARCHAR(50),
    metric_type VARCHAR(50),
    metric_value DECIMAL(10,4),
    timestamp TEXT,
    created_at TEXT DEFAULT (datetime('now', 'localtime'))
);

-- 异常记录表
CREATE TABLE IF NOT EXISTS anomaly_record (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    device_id VARCHAR(50) NOT NULL,
    device_name VARCHAR(100),
    metric_type VARCHAR(50),
    metric_value DECIMAL(10,4),
    threshold DECIMAL(10,4),
    anomaly_score DECIMAL(5,4),
    anomaly_type VARCHAR(50),
    detection_method VARCHAR(50),
    severity INTEGER DEFAULT 1,
    description VARCHAR(500),
    advice VARCHAR(500),
    detected_at TEXT,
    status VARCHAR(20) DEFAULT 'PENDING',
    create_time TEXT DEFAULT (datetime('now', 'localtime')),
    update_time TEXT DEFAULT (datetime('now', 'localtime'))
);

-- 告警配置表
CREATE TABLE IF NOT EXISTS alert_config (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    config_name VARCHAR(100) NOT NULL,
    device_type VARCHAR(50),
    metric_type VARCHAR(50),
    threshold_min DECIMAL(10,4),
    threshold_max DECIMAL(10,4),
    severity INTEGER DEFAULT 2,
    alert_enabled INTEGER DEFAULT 1,
    wechat_enabled INTEGER DEFAULT 1,
    alert_message VARCHAR(500),
    advice VARCHAR(500),
    status INTEGER DEFAULT 1,
    create_time TEXT DEFAULT (datetime('now', 'localtime')),
    update_time TEXT DEFAULT (datetime('now', 'localtime'))
);

-- 告警记录表
CREATE TABLE IF NOT EXISTS alert_record (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    anomaly_id INTEGER,
    alert_level VARCHAR(20),
    alert_message VARCHAR(500),
    alert_time TEXT,
    acknowledged INTEGER DEFAULT 0,
    acknowledged_by VARCHAR(50),
    acknowledged_at TEXT,
    remark VARCHAR(500),
    create_time TEXT DEFAULT (datetime('now', 'localtime')),
    update_time TEXT DEFAULT (datetime('now', 'localtime'))
);

-- 蒸汽锅炉设备表
CREATE TABLE IF NOT EXISTS steam_boiler (
    id VARCHAR(32) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    enterprise_id VARCHAR(32) NOT NULL,
    enterprise_name VARCHAR(100),
    capacity DECIMAL(10,2),
    model VARCHAR(100),
    status VARCHAR(20) DEFAULT 'offline',
    max_pressure DECIMAL(10,2),
    min_pressure DECIMAL(10,2),
    rated_temperature DECIMAL(10,2),
    install_date TEXT,
    create_time TEXT DEFAULT (datetime('now', 'localtime')),
    update_time TEXT DEFAULT (datetime('now', 'localtime'))
);

-- 蒸汽数据点表
CREATE TABLE IF NOT EXISTS steam_data_point (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    boiler_id VARCHAR(32) NOT NULL,
    steam_pressure DECIMAL(10,2),
    steam_temperature DECIMAL(10,2),
    steam_flow DECIMAL(10,2),
    water_consumption DECIMAL(10,2),
    electricity_consumption DECIMAL(10,2),
    efficiency DECIMAL(5,2),
    data_time TEXT NOT NULL,
    create_time TEXT DEFAULT (datetime('now', 'localtime'))
);

-- 用户画像表
CREATE TABLE IF NOT EXISTS user_profile (
    id VARCHAR(32) PRIMARY KEY,
    enterprise_id VARCHAR(32) NOT NULL UNIQUE,
    enterprise_name VARCHAR(100),
    production_pattern TEXT,
    energy_features TEXT,
    profile_score INTEGER DEFAULT 0,
    last_updated TEXT,
    create_time TEXT DEFAULT (datetime('now', 'localtime')),
    update_time TEXT DEFAULT (datetime('now', 'localtime'))
);

-- 能耗记录表
CREATE TABLE IF NOT EXISTS energy_consumption (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    enterprise_id VARCHAR(32) NOT NULL,
    enterprise_name VARCHAR(100),
    steam_amount DECIMAL(10,2),
    water_amount DECIMAL(10,2),
    electricity_amount DECIMAL(10,2),
    standard_coal DECIMAL(10,2),
    record_date TEXT NOT NULL,
    create_time TEXT DEFAULT (datetime('now', 'localtime')),
    update_time TEXT DEFAULT (datetime('now', 'localtime'))
);

-- 锅炉运行策略表
CREATE TABLE IF NOT EXISTS steam_strategy (
    id VARCHAR(32) PRIMARY KEY,
    boiler_id VARCHAR(32) NOT NULL,
    strategy_type VARCHAR(20),
    target_power DECIMAL(10,2),
    schedule TEXT,
    status VARCHAR(20) DEFAULT 'draft',
    create_time TEXT DEFAULT (datetime('now', 'localtime')),
    update_time TEXT DEFAULT (datetime('now', 'localtime'))
);

-- 蒸汽告警记录表
CREATE TABLE IF NOT EXISTS steam_alarm (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    boiler_id VARCHAR(32),
    enterprise_id VARCHAR(32),
    alarm_type VARCHAR(20) NOT NULL,
    alarm_level INTEGER NOT NULL,
    alarm_message VARCHAR(500),
    alarm_value DECIMAL(10,2),
    threshold_value DECIMAL(10,2),
    start_time TEXT,
    end_time TEXT,
    acknowledged INTEGER DEFAULT 0,
    create_time TEXT DEFAULT (datetime('now', 'localtime'))
);

-- 气候补偿曲线表
CREATE TABLE IF NOT EXISTS climate_compensation_curve (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    curve_name VARCHAR(100) NOT NULL,
    outdoor_temp_min DECIMAL(5,2) NOT NULL,
    outdoor_temp_max DECIMAL(5,2) NOT NULL,
    supply_temp_target DECIMAL(5,2) NOT NULL,
    return_temp_target DECIMAL(5,2),
    status INTEGER DEFAULT 1,
    create_time TEXT DEFAULT (datetime('now', 'localtime')),
    update_time TEXT DEFAULT (datetime('now', 'localtime'))
);

-- 室内温控器表
CREATE TABLE IF NOT EXISTS resident_temperature (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    device_code VARCHAR(50) NOT NULL,
    current_temp DECIMAL(5,2),
    target_temp DECIMAL(5,2),
    valve_open_ratio DECIMAL(5,4),
    status INTEGER DEFAULT 1,
    create_time TEXT DEFAULT (datetime('now', 'localtime')),
    update_time TEXT DEFAULT (datetime('now', 'localtime'))
);

-- 供热曲线配置表
CREATE TABLE IF NOT EXISTS heating_curve_config (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    config_name VARCHAR(100) NOT NULL,
    building_id INTEGER,
    outdoor_temp_design DECIMAL(5,2),
    indoor_temp_design DECIMAL(5,2),
    supply_temp_design DECIMAL(5,2),
    return_temp_design DECIMAL(5,2),
    slope DECIMAL(8,4),
    intercept DECIMAL(8,4),
    status INTEGER DEFAULT 1,
    create_time TEXT DEFAULT (datetime('now', 'localtime')),
    update_time TEXT DEFAULT (datetime('now', 'localtime'))
);

-- 气候补偿配置表
CREATE TABLE IF NOT EXISTS climate_compensation_config (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    config_name VARCHAR(100) NOT NULL,
    station_id INTEGER,
    compensation_type VARCHAR(20),
    kp DECIMAL(8,4),
    ki DECIMAL(8,4),
    kd DECIMAL(8,4),
    setpoint DECIMAL(5,2),
    output_min DECIMAL(5,2),
    output_max DECIMAL(5,2),
    status INTEGER DEFAULT 1,
    create_time TEXT DEFAULT (datetime('now', 'localtime')),
    update_time TEXT DEFAULT (datetime('now', 'localtime'))
);

-- 气候补偿效果记录表
CREATE TABLE IF NOT EXISTS climate_compensation_effect (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    station_id INTEGER NOT NULL,
    outdoor_temp DECIMAL(5,2),
    supply_temp_before DECIMAL(5,2),
    supply_temp_after DECIMAL(5,2),
    indoor_temp_avg DECIMAL(5,2),
    energy_saving_rate DECIMAL(5,4),
    calculate_time TEXT NOT NULL,
    create_time TEXT DEFAULT (datetime('now', 'localtime'))
);

-- 效率指标历史表
CREATE TABLE IF NOT EXISTS efficiency_indicator_history (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    station_id INTEGER NOT NULL,
    indicator_name VARCHAR(50) NOT NULL,
    indicator_value DECIMAL(10,4),
    indicator_unit VARCHAR(20),
    collect_time TEXT NOT NULL,
    create_time TEXT DEFAULT (datetime('now', 'localtime'))
);

-- 能耗价格配置表
CREATE TABLE IF NOT EXISTS energy_price (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    energy_type VARCHAR(20) NOT NULL,
    price DECIMAL(10,4) NOT NULL,
    unit VARCHAR(20),
    effective_date TEXT NOT NULL,
    status INTEGER DEFAULT 1,
    create_time TEXT DEFAULT (datetime('now', 'localtime'))
);

-- 能耗汇总表
CREATE TABLE IF NOT EXISTS cost_summary (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    station_id INTEGER,
    enterprise_id VARCHAR(32),
    summary_type VARCHAR(20) NOT NULL,
    period_type VARCHAR(20) NOT NULL,
    period_start TEXT NOT NULL,
    period_end TEXT NOT NULL,
    total_cost DECIMAL(12,2),
    energy_cost DECIMAL(12,2),
    power_cost DECIMAL(12,2),
    water_cost DECIMAL(12,2),
    other_cost DECIMAL(12,2),
    unit_cost DECIMAL(10,4),
    create_time TEXT DEFAULT (datetime('now', 'localtime'))
);

-- 碳排放因子表
CREATE TABLE IF NOT EXISTS carbon_factor (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    energy_type VARCHAR(20) NOT NULL,
    factor_value DECIMAL(10,6) NOT NULL,
    unit VARCHAR(20),
    source VARCHAR(100),
    effective_date TEXT NOT NULL,
    status INTEGER DEFAULT 1,
    create_time TEXT DEFAULT (datetime('now', 'localtime'))
);

-- 换热效率指标表
CREATE TABLE IF NOT EXISTS efficiency_metrics (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    station_id INTEGER NOT NULL,
    heat_transfer_efficiency DECIMAL(5,4),
    hydraulic_efficiency DECIMAL(5,4),
    pump_efficiency DECIMAL(5,4),
    comprehensive_efficiency DECIMAL(5,4),
    collect_time TEXT NOT NULL,
    create_time TEXT DEFAULT (datetime('now', 'localtime'))
);
