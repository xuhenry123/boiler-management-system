package com.boiler.entity.anomaly;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("monitor_data")
public class MonitorData implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String deviceId;

    private String deviceType;

    private String metricType;

    private BigDecimal metricValue;

    private LocalDateTime timestamp;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
