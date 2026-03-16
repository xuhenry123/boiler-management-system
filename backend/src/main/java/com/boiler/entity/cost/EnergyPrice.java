package com.boiler.entity.cost;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("energy_price")
public class EnergyPrice implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String energyType;

    private BigDecimal unitPrice;

    private String unit;

    private LocalDate effectiveDate;

    private LocalDate expiredDate;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
