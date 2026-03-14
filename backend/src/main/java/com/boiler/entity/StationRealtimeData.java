package com.boiler.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("station_realtime_data")
public class StationRealtimeData implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long stationId;
    private BigDecimal primarySupplyPressure;
    private BigDecimal primaryReturnPressure;
    private BigDecimal primarySupplyTemp;
    private BigDecimal primaryReturnTemp;
    private BigDecimal secondarySupplyPressure;
    private BigDecimal secondaryReturnPressure;
    private BigDecimal secondarySupplyTemp;
    private BigDecimal secondaryReturnTemp;
    private BigDecimal primaryFlow;
    private BigDecimal secondaryFlow;
    private BigDecimal pumpSpeedRatio;
    private BigDecimal valveOpenRatio;
    private BigDecimal heatPower;
    private LocalDateTime collectTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
