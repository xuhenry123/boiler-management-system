package com.boiler.entity.anomaly;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("alert_config")
public class AlertConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String configName;

    private String deviceType;

    private String metricType;

    private BigDecimal thresholdMin;

    private BigDecimal thresholdMax;

    private Integer severity;

    private Integer alertEnabled;

    private Integer wechatEnabled;

    private String alertMessage;

    private String advice;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
