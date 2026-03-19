-- 锅炉集中供热智慧管理系统 - 测试数据初始化脚本

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

-- 插入室内温度测试数据
INSERT INTO indoor_temperature (user_id, temperature, is_abnormal, data_source, collect_time) VALUES
(1, 19.5, 0, 'sensor', datetime('now', '-1 hours')),
(1, 19.8, 0, 'sensor', datetime('now', '-2 hours')),
(1, 20.0, 0, 'sensor', datetime('now', '-3 hours')),
(2, 20.5, 0, 'sensor', datetime('now', '-1 hours')),
(2, 20.8, 0, 'sensor', datetime('now', '-2 hours')),
(2, 21.0, 0, 'sensor', datetime('now', '-3 hours')),
(3, 19.0, 0, 'sensor', datetime('now', '-1 hours')),
(3, 19.2, 0, 'sensor', datetime('now', '-2 hours')),
(3, 19.5, 0, 'sensor', datetime('now', '-3 hours')),
(4, 21.5, 0, 'sensor', datetime('now', '-1 hours')),
(4, 21.8, 0, 'sensor', datetime('now', '-2 hours')),
(4, 22.0, 0, 'sensor', datetime('now', '-3 hours'));

-- 插入室外温度测试数据
INSERT INTO outdoor_temperature (predict_time, temperature, data_source, is_actual) VALUES
(datetime('now', '-3 hours'), -3.5, 'weather_api', 1),
(datetime('now', '-2 hours'), -3.0, 'weather_api', 1),
(datetime('now', '-1 hours'), -2.5, 'weather_api', 1),
(datetime('now'), -2.0, 'weather_api', 1),
(datetime('now', '+1 hours'), -1.5, 'forecast', 0),
(datetime('now', '+2 hours'), -1.0, 'forecast', 0),
(datetime('now', '+3 hours'), -0.5, 'forecast', 0),
(datetime('now', '+4 hours'), 0.0, 'forecast', 0),
(datetime('now', '+5 hours'), 0.5, 'forecast', 0),
(datetime('now', '+6 hours'), 1.0, 'forecast', 0);

-- 插入锅炉测试数据
INSERT INTO boiler (boiler_code, boiler_name, boiler_type, manufacturer, model, rated_capacity, rated_pressure, design_efficiency, status) VALUES
('BLR001', '1号燃气锅炉', 'gas', '某锅炉厂', 'WNS10-1.0', 10.00, 1.00, 0.95, 1),
('BLR002', '2号燃气锅炉', 'gas', '某锅炉厂', 'WNS10-1.0', 10.00, 1.00, 0.95, 1),
('BLR003', '3号燃气锅炉', 'gas', '某锅炉厂', 'WNS15-1.0', 15.00, 1.00, 0.94, 0);

-- 插入预测模型测试数据
INSERT INTO prediction_model (model_type, model_version, accuracy, mse, status) VALUES
('lstm', 'v1.0.0', 0.9235, 0.562, 1),
('simple', 'v1.0.0', 0.8500, 1.230, 0);

-- 插入系统用户测试数据（密码为 admin123）
INSERT INTO sys_user (username, password, real_name, phone, role_id, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', '13800138000', 1, 1),
('operator', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '操作员', '13800138001', 2, 1);

-- 插入默认告警配置数据
INSERT INTO alert_config (config_name, device_type, metric_type, threshold_min, threshold_max, severity, alert_enabled, wechat_enabled, alert_message, advice) VALUES
('温度上限告警', 'temperature_sensor', 'temperature', NULL, 30.00, 3, 1, 1, '温度超过上限 {threshold}°C', '检查供热系统是否过热'),
('温度下限告警', 'temperature_sensor', 'temperature', 14.00, NULL, 3, 1, 1, '温度低于下限 {threshold}°C', '检查供热系统是否正常'),
('压力上限告警', 'pressure_sensor', 'pressure', NULL, 1.60, 3, 1, 1, '压力超过上限 {threshold}MPa', '检查管网是否超压'),
('压力下限告警', 'pressure_sensor', 'pressure', 0.40, NULL, 3, 1, 1, '压力低于下限 {threshold}MPa', '检查管网是否泄漏'),
('流量异常告警', 'flow_meter', 'flow', 0.50, 10.00, 2, 1, 1, '流量超出正常范围', '检查管网平衡');
