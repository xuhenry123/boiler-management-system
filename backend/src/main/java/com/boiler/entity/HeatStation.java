package com.boiler.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("heat_station")
public class HeatStation implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String stationCode;
    private String stationName;
    private String address;
    private BigDecimal designCapacity;
    private BigDecimal designFlow;
    private BigDecimal primarySupplyTemp;
    private BigDecimal primaryReturnTemp;
    private BigDecimal secondarySupplyTemp;
    private BigDecimal secondaryReturnTemp;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
