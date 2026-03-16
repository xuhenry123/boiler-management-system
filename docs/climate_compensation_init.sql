-- 气候补偿模块数据库表结构

-- 气候补偿配置表
CREATE TABLE IF NOT EXISTS climate_compensation_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    station_id BIGINT COMMENT '换热站ID',
    config_name VARCHAR(50) COMMENT '配置名称',
    compensation_mode VARCHAR(20) COMMENT '补偿模式: fixed-固定温度, climate-气候补偿, time-时间补偿, smart-智能补偿',
    curve_type VARCHAR(20) COMMENT '曲线类型: standard-标准, energy_saving-节能, comfort-舒适, custom-自定义',
    outdoor_temp_min DECIMAL(5,2) COMMENT '室外温度下限(°C)',
    outdoor_temp_max DECIMAL(5,2) COMMENT '室外温度上限(°C)',
    supply_temp DECIMAL(5,2) COMMENT '供水温度(°C)',
    return_temp DECIMAL(5,2) COMMENT '回水温度(°C)',
    indoor_target_temp DECIMAL(5,2) COMMENT '室内目标温度(°C)',
    is_active TINYINT DEFAULT 0 COMMENT '是否激活: 0-未激活, 1-激活',
    is_enabled TINYINT DEFAULT 1 COMMENT '是否启用: 0-禁用, 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='气候补偿配置表';

-- 气候补偿曲线数据点表
CREATE TABLE IF NOT EXISTS climate_compensation_curve (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    config_id BIGINT NOT NULL COMMENT '配置ID',
    curve_name VARCHAR(50) COMMENT '曲线名称',
    outdoor_temp DECIMAL(5,2) NOT NULL COMMENT '室外温度(°C)',
    supply_temp DECIMAL(5,2) NOT NULL COMMENT '供水温度(°C)',
    return_temp DECIMAL(5,2) COMMENT '回水温度(°C)',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_config_id (config_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='气候补偿曲线数据点表';

-- 气候补偿效果记录表
CREATE TABLE IF NOT EXISTS climate_compensation_effect (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    station_id BIGINT COMMENT '换热站ID',
    record_date DATETIME COMMENT '记录日期',
    outdoor_temp DECIMAL(5,2) COMMENT '室外温度(°C)',
    target_supply_temp DECIMAL(5,2) COMMENT '目标供水温度(°C)',
    actual_supply_temp DECIMAL(5,2) COMMENT '实际供水温度(°C)',
    energy_before DECIMAL(10,2) COMMENT '补偿前能耗',
    energy_after DECIMAL(10,2) COMMENT '补偿后能耗',
    savings_rate DECIMAL(5,2) COMMENT '节能率(%)',
    data_type VARCHAR(20) COMMENT '数据类型: week-周, month-月, season-季',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_station_id (station_id),
    INDEX idx_record_date (record_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='气候补偿效果记录表';

-- 插入默认配置数据
INSERT INTO climate_compensation_config (station_id, config_name, compensation_mode, curve_type, outdoor_temp_min, outdoor_temp_max, supply_temp, return_temp, indoor_target_temp, is_active, is_enabled)
VALUES 
(1, '标准补偿', 'climate', 'standard', -20, 10, 52, 38, 20, 1, 1),
(1, '节能模式', 'climate', 'energy_saving', -15, 5, 48, 35, 20, 0, 1),
(1, '舒适模式', 'climate', 'comfort', -10, 15, 58, 42, 22, 0, 1),
(1, '夜间模式', 'time', 'standard', -5, 10, 45, 33, 18, 0, 1);

-- 插入默认曲线数据
INSERT INTO climate_compensation_curve (config_id, curve_name, outdoor_temp, supply_temp, return_temp, sort_order)
SELECT 1, '标准曲线', -20, 75, 55, 1 FROM DUAL
UNION ALL SELECT 1, '标准曲线', -15, 68, 50, 2 FROM DUAL
UNION ALL SELECT 1, '标准曲线', -10, 60, 44, 3 FROM DUAL
UNION ALL SELECT 1, '标准曲线', -5, 52, 38, 4 FROM DUAL
UNION ALL SELECT 1, '标准曲线', 0, 45, 33, 5 FROM DUAL
UNION ALL SELECT 1, '标准曲线', 5, 40, 30, 6 FROM DUAL
UNION ALL SELECT 1, '标准曲线', 10, 35, 28, 7 FROM DUAL;

-- 插入默认效果数据
INSERT INTO climate_compensation_effect (station_id, record_date, outdoor_temp, target_supply_temp, actual_supply_temp, energy_before, energy_after, savings_rate, data_type)
VALUES 
(1, DATE_SUB(CURDATE(), INTERVAL 6 DAY), -8, 52, 45, 52, 45, 13.5, 'week'),
(1, DATE_SUB(CURDATE(), INTERVAL 5 DAY), -10, 55, 42, 48, 42, 12.5, 'week'),
(1, DATE_SUB(CURDATE(), INTERVAL 4 DAY), -6, 50, 48, 55, 48, 12.7, 'week'),
(1, DATE_SUB(CURDATE(), INTERVAL 3 DAY), -5, 48, 44, 50, 44, 12.0, 'week'),
(1, DATE_SUB(CURDATE(), INTERVAL 2 DAY), -3, 45, 40, 45, 40, 11.1, 'week'),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), -2, 42, 37, 42, 37, 11.9, 'week'),
(1, CURDATE(), -4, 44, 40, 46, 40, 13.0, 'week');
