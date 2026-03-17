package com.boiler.entity.steam;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 蒸汽数据点实体类
 * 对应数据库表 steam_data_point
 * 存储蒸汽锅炉的运行数据点，用于实时监控和历史分析
 */
@Data
@TableName("steam_data_point")
public class SteamDataPoint implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 数据点唯一标识ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 锅炉ID，关联到对应的蒸汽锅炉 */
    private String boilerId;

    /** 蒸汽压力，单位为MPa */
    private BigDecimal steamPressure;

    /** 蒸汽温度，单位为℃ */
    private BigDecimal steamTemperature;

    /** 蒸汽流量，单位通常为t/h */
    private BigDecimal steamFlow;

    /** 耗水量，单位通常为t/h */
    private BigDecimal waterConsumption;

    /** 耗电量，单位通常为kWh */
    private BigDecimal electricityConsumption;

    /** 效率，锅炉的热效率百分比 */
    private BigDecimal efficiency;

    /** 数据时间，数据采集的时间点 */
    private LocalDateTime dataTime;

    /** 创建时间，记录数据插入时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
