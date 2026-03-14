package com.boiler.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("indoor_temperature")
public class IndoorTemperature implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;
    private BigDecimal temperature;
    private Integer isAbnormal;
    private String dataSource;
    private LocalDateTime collectTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
