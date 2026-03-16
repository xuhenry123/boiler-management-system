package com.boiler.entity.anomaly;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("anomaly_record")
public class AnomalyRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String deviceId;

    private String deviceName;

    private String metricType;

    private BigDecimal metricValue;

    private BigDecimal threshold;

    private BigDecimal anomalyScore;

    private String anomalyType;

    private String detectionMethod;

    private Integer severity;

    private String description;

    private String advice;

    private LocalDateTime detectedAt;

    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
