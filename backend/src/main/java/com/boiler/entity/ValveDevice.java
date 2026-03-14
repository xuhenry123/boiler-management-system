package com.boiler.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("valve_device")
public class ValveDevice implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String valveCode;
    private String valveName;
    private String valveType;
    private Long nodeId;
    private String manufacturer;
    private String model;
    private BigDecimal diameter;
    private BigDecimal openRatio;
    private BigDecimal positionLimitMin;
    private BigDecimal positionLimitMax;
    private BigDecimal responseTime;
    private String controlProtocol;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
