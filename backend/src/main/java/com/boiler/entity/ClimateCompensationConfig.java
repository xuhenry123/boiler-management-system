package com.boiler.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("climate_compensation_config")
public class ClimateCompensationConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long stationId;

    private String configName;

    private String compensationMode;

    private String curveType;

    private BigDecimal outdoorTempMin;

    private BigDecimal outdoorTempMax;

    private BigDecimal supplyTemp;

    private BigDecimal returnTemp;

    private BigDecimal indoorTargetTemp;

    private Integer isActive;

    private Integer isEnabled;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
