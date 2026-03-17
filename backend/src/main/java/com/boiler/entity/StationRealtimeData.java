package com.boiler.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 热力站实时数据实体类
 * 对应数据库表 station_realtime_data
 * 存储热力站的实时运行参数，用于实时监控和数据分析
 */
@Data
@TableName("station_realtime_data")
public class StationRealtimeData implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 记录唯一标识ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 热力站ID，关联到对应的热力站 */
    private Long stationId;

    /** 一次侧供水压力，单位为MPa */
    private BigDecimal primarySupplyPressure;

    /** 一次侧回水压力，单位为MPa */
    private BigDecimal primaryReturnPressure;

    /** 一次侧供水温度，单位为℃ */
    private BigDecimal primarySupplyTemp;

    /** 一次侧回水温度，单位为℃ */
    private BigDecimal primaryReturnTemp;

    /** 二次侧供水压力，单位为MPa */
    private BigDecimal secondarySupplyPressure;

    /** 二次侧回水压力，单位为MPa */
    private BigDecimal secondaryReturnPressure;

    /** 二次侧供水温度，单位为℃ */
    private BigDecimal secondarySupplyTemp;

    /** 二次侧回水温度，单位为℃ */
    private BigDecimal secondaryReturnTemp;

    /** 一次侧流量，单位为t/h */
    private BigDecimal primaryFlow;

    /** 二次侧流量，单位为t/h */
    private BigDecimal secondaryFlow;

    /** 泵转速比，0-1之间的比例值 */
    private BigDecimal pumpSpeedRatio;

    /** 阀门开度，0-1之间的比例值 */
    private BigDecimal valveOpenRatio;

    /** 供热功率，单位为MW */
    private BigDecimal heatPower;

    /** 采集时间，数据采集的具体时间 */
    private LocalDateTime collectTime;

    /** 创建时间，记录数据插入时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
