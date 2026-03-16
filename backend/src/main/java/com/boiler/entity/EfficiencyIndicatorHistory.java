package com.boiler.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("efficiency_indicator_history")
public class EfficiencyIndicatorHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long stationId;

    @TableField("cop")
    private BigDecimal cop;

    @TableField("heat_loss_rate")
    private BigDecimal heatLossRate;

    @TableField("supply_balance")
    private BigDecimal supplyBalance;

    @TableField("avg_indoor_temp")
    private BigDecimal avgIndoorTemp;

    @TableField("heat_input")
    private BigDecimal heatInput;

    @TableField("heat_output")
    private BigDecimal heatOutput;

    @TableField("record_date")
    private LocalDate recordDate;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
