package com.boiler.entity.steam;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("steam_data_point")
public class SteamDataPoint implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String boilerId;

    private BigDecimal steamPressure;

    private BigDecimal steamTemperature;

    private BigDecimal steamFlow;

    private BigDecimal waterConsumption;

    private BigDecimal electricityConsumption;

    private BigDecimal efficiency;

    private LocalDateTime dataTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
