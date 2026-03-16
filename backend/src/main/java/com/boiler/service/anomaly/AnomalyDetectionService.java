package com.boiler.service.anomaly;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boiler.entity.anomaly.AnomalyRecord;
import com.boiler.mapper.anomaly.AnomalyRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnomalyDetectionService {

    private final AnomalyRecordMapper anomalyRecordMapper;

    public Page<AnomalyRecord> listAnomalies(Integer page, Integer size, String type, Integer severity, String status) {
        Page<AnomalyRecord> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<AnomalyRecord> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(type)) {
            wrapper.eq(AnomalyRecord::getMetricType, type);
        }
        if (severity != null) {
            wrapper.eq(AnomalyRecord::getSeverity, severity);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(AnomalyRecord::getStatus, status);
        }
        
        wrapper.orderByDesc(AnomalyRecord::getDetectedAt);
        return anomalyRecordMapper.selectPage(pageParam, wrapper);
    }

    public AnomalyRecord getAnomalyById(Long id) {
        return anomalyRecordMapper.selectById(id);
    }

    public List<AnomalyRecord> listPendingAnomalies() {
        LambdaQueryWrapper<AnomalyRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AnomalyRecord::getStatus, "PENDING")
               .orderByDesc(AnomalyRecord::getSeverity)
               .orderByDesc(AnomalyRecord::getDetectedAt);
        return anomalyRecordMapper.selectList(wrapper);
    }

    public Long saveAnomaly(AnomalyRecord anomaly) {
        anomaly.setDetectedAt(LocalDateTime.now());
        anomaly.setStatus("PENDING");
        anomalyRecordMapper.insert(anomaly);
        return anomaly.getId();
    }

    public boolean updateAnomalyStatus(Long id, String status) {
        AnomalyRecord anomaly = new AnomalyRecord();
        anomaly.setId(id);
        anomaly.setStatus(status);
        return anomalyRecordMapper.updateById(anomaly) > 0;
    }

    public boolean confirmAnomaly(Long id, String remark) {
        AnomalyRecord anomaly = new AnomalyRecord();
        anomaly.setId(id);
        anomaly.setStatus("CONFIRMED");
        return anomalyRecordMapper.updateById(anomaly) > 0;
    }

    public boolean resolveAnomaly(Long id) {
        AnomalyRecord anomaly = new AnomalyRecord();
        anomaly.setId(id);
        anomaly.setStatus("RESOLVED");
        return anomalyRecordMapper.updateById(anomaly) > 0;
    }

    public List<AnomalyRecord> listAnomaliesByDevice(String deviceId) {
        LambdaQueryWrapper<AnomalyRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AnomalyRecord::getDeviceId, deviceId)
               .orderByDesc(AnomalyRecord::getDetectedAt);
        return anomalyRecordMapper.selectList(wrapper);
    }

    public Long countPending() {
        LambdaQueryWrapper<AnomalyRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AnomalyRecord::getStatus, "PENDING");
        return anomalyRecordMapper.selectCount(wrapper);
    }

    public Long countHighSeverity() {
        LambdaQueryWrapper<AnomalyRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AnomalyRecord::getStatus, "PENDING")
               .ge(AnomalyRecord::getSeverity, 3);
        return anomalyRecordMapper.selectCount(wrapper);
    }
}
