package com.boiler.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("boiler")
public class Boiler implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String boilerCode;
    private String boilerName;
    private String boilerType;
    private String manufacturer;
    private String model;
    private BigDecimal ratedCapacity;
    private BigDecimal ratedPressure;
    private BigDecimal designEfficiency;
    private String fuelType;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
