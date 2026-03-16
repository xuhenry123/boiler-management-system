package com.boiler.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("balance_strategy")
public class BalanceStrategy implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String networkType;
    private String strategyName;
    private Long stationId;
    private BigDecimal objectiveValue;
    private Integer iterationCount;
    private Long executionTime;
    private String optimizeAlgorithm;
    private String valveAdjustments;
    private String status;
    private String description;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
