package com.boiler.entity.cost;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("efficiency_metrics")
public class EfficiencyMetrics implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long stationId;

    private String stationName;

    private BigDecimal efficiencyValue;

    private BigDecimal energyConsumption;

    private BigDecimal costPerUnit;

    private BigDecimal carbonPerUnit;

    private LocalDate statDate;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
