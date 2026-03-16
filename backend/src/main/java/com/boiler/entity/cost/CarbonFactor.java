package com.boiler.entity.cost;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("carbon_factor")
public class CarbonFactor implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String energyType;

    private BigDecimal factorValue;

    private String factorUnit;

    private String source;

    private LocalDate effectiveDate;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
