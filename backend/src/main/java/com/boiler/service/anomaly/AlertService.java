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

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertConfigMapper alertConfigMapper;
    private final AlertRecordMapper alertRecordMapper;

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

    public AlertConfig getAlertConfigById(Long id) {
        return alertConfigMapper.selectById(id);
    }

    public boolean saveAlertConfig(AlertConfig config) {
        config.setStatus(1);
        return alertConfigMapper.insert(config) > 0;
    }

    public boolean updateAlertConfig(AlertConfig config) {
        return alertConfigMapper.updateById(config) > 0;
    }

    public boolean deleteAlertConfig(Long id) {
        return alertConfigMapper.deleteById(id) > 0;
    }

    public List<AlertConfig> getEnabledConfigs() {
        LambdaQueryWrapper<AlertConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlertConfig::getStatus, 1)
               .eq(AlertConfig::getAlertEnabled, 1);
        return alertConfigMapper.selectList(wrapper);
    }

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

    public Long createAlert(AlertRecord alert) {
        alert.setAlertTime(LocalDateTime.now());
        alert.setAcknowledged(false);
        alertRecordMapper.insert(alert);
        return alert.getId();
    }

    public boolean acknowledgeAlert(Long id, String acknowledgedBy) {
        AlertRecord alert = new AlertRecord();
        alert.setId(id);
        alert.setAcknowledged(true);
        alert.setAcknowledgedBy(acknowledgedBy);
        alert.setAcknowledgedAt(LocalDateTime.now());
        return alertRecordMapper.updateById(alert) > 0;
    }

    public Long countUnacknowledged() {
        LambdaQueryWrapper<AlertRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlertRecord::getAcknowledged, false);
        return alertRecordMapper.selectCount(wrapper);
    }
}
