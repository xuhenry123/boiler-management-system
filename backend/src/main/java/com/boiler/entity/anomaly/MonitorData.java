package com.boiler.entity.anomaly;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 监控数据实体类
 * 对应数据库表 monitor_data
 * 存储从各种监控设备采集的原始数据，用于异常检测和分析
 */
@Data
@TableName("monitor_data")
public class MonitorData implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 监控数据唯一标识ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 设备ID，关联到数据来源的设备 */
    private String deviceId;

    /** 设备类型，如"温度传感器"、"压力传感器"、"流量计"等 */
    private String deviceType;

    /** 指标类型，如"温度"、"压力"、"流量"、"功率"等 */
    private String metricType;

    /** 指标值，采集到的具体数值 */
    private BigDecimal metricValue;

    /** 时间戳，数据采集的时间点 */
    private LocalDateTime timestamp;

    /** 创建时间，记录数据插入时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
