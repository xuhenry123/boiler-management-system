package com.boiler.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("climate_compensation_effect")
public class ClimateCompensationEffect implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long stationId;

    private LocalDateTime recordDate;

    private BigDecimal outdoorTemp;

    private BigDecimal targetSupplyTemp;

    private BigDecimal actualSupplyTemp;

    private BigDecimal energyBefore;

    private BigDecimal energyAfter;

    private BigDecimal savingsRate;

    private String dataType;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
