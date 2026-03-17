package com.boiler.service.anomaly;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boiler.entity.anomaly.AlertConfig;
import com.boiler.entity.anomaly.AlertRecord;
import com.boiler.mapper.anomaly.AlertConfigMapper;
import com.boiler.mapper.anomaly.AlertRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 告警业务逻辑层
 * 负责告警配置和告警记录相关的业务逻辑处理
 */
@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertConfigMapper alertConfigMapper;
    private final AlertRecordMapper alertRecordMapper;

    /**
     * 分页查询告警配置列表
     * @param page 页码
     * @param size 每页数量
     * @param deviceType 设备类型
     * @param metricType 指标类型
     * @return 分页结果
     */
    public Page<AlertConfig> listAlertConfigs(Integer page, Integer size, String deviceType, String metricType) {
        Page<AlertConfig> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<AlertConfig> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(deviceType)) {
            wrapper.eq(AlertConfig::getDeviceType, deviceType);
        }
        if (StringUtils.hasText(metricType)) {
            wrapper.eq(AlertConfig::getMetricType, metricType);
        }
        
        wrapper.orderByDesc(AlertConfig::getId);
        return alertConfigMapper.selectPage(pageParam, wrapper);
    }

    /**
     * 根据ID查询告警配置
     * @param id 配置ID
     * @return 配置信息
     */
    public AlertConfig getAlertConfigById(Long id) {
        return alertConfigMapper.selectById(id);
    }

    /**
     * 保存告警配置
     * @param config 配置信息
     * @return 是否成功
     */
    public boolean saveAlertConfig(AlertConfig config) {
        config.setStatus(1);
        return alertConfigMapper.insert(config) > 0;
    }

    /**
     * 更新告警配置
     * @param config 配置信息
     * @return 是否成功
     */
    public boolean updateAlertConfig(AlertConfig config) {
        return alertConfigMapper.updateById(config) > 0;
    }

    /**
     * 删除告警配置
     * @param id 配置ID
     * @return 是否成功
     */
    public boolean deleteAlertConfig(Long id) {
        return alertConfigMapper.deleteById(id) > 0;
    }

    /**
     * 获取所有启用的告警配置
     * @return 启用配置列表
     */
    public List<AlertConfig> getEnabledConfigs() {
        LambdaQueryWrapper<AlertConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlertConfig::getStatus, 1)
               .eq(AlertConfig::getAlertEnabled, 1);
        return alertConfigMapper.selectList(wrapper);
    }

    /**
     * 分页查询告警记录列表
     * @param page 页码
     * @param size 每页数量
     * @param alertLevel 告警级别
     * @param acknowledged 是否已确认
     * @return 分页结果
     */
    public Page<AlertRecord> listAlertRecords(Integer page, Integer size, String alertLevel, Boolean acknowledged) {
        Page<AlertRecord> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<AlertRecord> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(alertLevel)) {
            wrapper.eq(AlertRecord::getAlertLevel, alertLevel);
        }
        if (acknowledged != null) {
            wrapper.eq(AlertRecord::getAcknowledged, acknowledged);
        }
        
        wrapper.orderByDesc(AlertRecord::getAlertTime);
        return alertRecordMapper.selectPage(pageParam, wrapper);
    }

    /**
     * 创建告警记录
     * @param alert 告警信息
     * @return 告警记录ID
     */
    public Long createAlert(AlertRecord alert) {
        alert.setAlertTime(LocalDateTime.now());
        alert.setAcknowledged(false);
        alertRecordMapper.insert(alert);
        return alert.getId();
    }

    /**
     * 确认告警
     * @param id 告警ID
     * @param acknowledgedBy 确认人
     * @return 是否成功
     */
    public boolean acknowledgeAlert(Long id, String acknowledgedBy) {
        AlertRecord alert = new AlertRecord();
        alert.setId(id);
        alert.setAcknowledged(true);
        alert.setAcknowledgedBy(acknowledgedBy);
        alert.setAcknowledgedAt(LocalDateTime.now());
        return alertRecordMapper.updateById(alert) > 0;
    }

    /**
     * 统计未确认告警数量
     * @return 未确认告警数量
     */
    public Long countUnacknowledged() {
        LambdaQueryWrapper<AlertRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlertRecord::getAcknowledged, false);
        return alertRecordMapper.selectCount(wrapper);
    }
}
