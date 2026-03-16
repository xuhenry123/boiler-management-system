package com.boiler.entity.steam;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("steam_boiler")
public class SteamBoiler implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String name;

    private String enterpriseId;

    private String enterpriseName;

    private BigDecimal capacity;

    private String model;

    private String status;

    private BigDecimal maxPressure;

    private BigDecimal minPressure;

    private BigDecimal ratedTemperature;

    private LocalDateTime installDate;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
