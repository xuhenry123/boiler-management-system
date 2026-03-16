package com.boiler.entity.cost;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("cost_summary")
public class CostSummary implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long stationId;

    private String stationName;

    private String energyType;

    private String costType;

    private BigDecimal consumption;

    private BigDecimal unitPrice;

    private BigDecimal totalCost;

    private BigDecimal carbonEmission;

    private LocalDate statDate;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
