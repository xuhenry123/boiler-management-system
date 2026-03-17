package com.boiler.entity.anomaly;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.ioSerializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 异常记录实体类
 * 对应数据库表 anomaly_record
 * 记录系统中检测到的各类异常情况，包括设备故障、数据异常等
 */
@Data
@TableName("anomaly_record")
public class AnomalyRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 异常记录唯一标识ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 设备ID，关联到异常的设备 */
    private String deviceId;

    /** 设备名称，便于识别异常设备 */
    private String deviceName;

    /** 指标类型，如"温度"、"压力"、"流量"、"能耗"等 */
    private String metricType;

    /** 指标值，异常发生时的指标实际值 */
    private BigDecimal metricValue;

    /** 阈值，异常判定的阈值或正常范围边界 */
    private BigDecimal threshold;

    /** 异常分数，0-1之间，表示异常的严重程度 */
    private BigDecimal anomalyScore;

    /** 异常类型，如"超阈值"、"趋势异常"、"模式异常"等 */
    private String anomalyType;

    /** 检测方法，如"阈值检测"、"统计检测"、"机器学习检测"等 */
    private String detectionMethod;

    /** 严重程度，1-轻微，2-一般，3-严重，4-紧急 */
    private Integer severity;

    /** 描述，异常的具体描述信息 */
    private String description;

    /** 建议，处理异常的建议措施 */
    private String advice;

    /** 检测时间，异常被检测到的时间 */
    private LocalDateTime detectedAt;

    /** 状态，如"待处理"、"处理中"、"已解决" */
    private String status;

    /** 创建时间，记录数据插入时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间，记录数据最后更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
