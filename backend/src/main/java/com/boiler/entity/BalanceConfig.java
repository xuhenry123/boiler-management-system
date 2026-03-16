package com.boiler.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("balance_config")
public class BalanceConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String networkType;
    private String configName;
    private BigDecimal supplyTempTarget;
    private BigDecimal returnTempTarget;
    private BigDecimal indoorTempTarget;
    private BigDecimal pressureTarget;
    private BigDecimal flowTarget;
    private BigDecimal tempTolerance;
    private BigDecimal pressureTolerance;
    private Integer optimizeAlgorithm;
    private Integer autoControlEnabled;
    private Integer adjustInterval;
    private String description;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
