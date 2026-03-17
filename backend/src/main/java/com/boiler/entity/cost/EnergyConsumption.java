package com.boiler.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 能耗实体类
 * 对应数据库表 energy_consumption
 * 存储各热力站的能源消耗数据，用于能耗统计和分析
 */
@Data
@TableName("energy_consumption")
public class EnergyConsumption implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 能耗记录唯一标识ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 热力站ID，关联到对应的热力站 */
    private Long stationId;

    /** 热力站名称，便于显示和识别 */
    private String stationName;

    /** 能源类型，如"电力"、"天然气"、"煤炭"等 */
    private String energyType;

    /** 消耗量，能源的消耗数量 */
    private BigDecimal consumption;

    /** 单位，如"kWh"、"m³"、"吨" */
    private String unit;

    /** 记录日期，数据对应的日期 */
    private LocalDate recordDate;

    /** 记录小时，0-23，表示数据对应的小时 */
    private Integer recordHour;

    /** 创建时间，记录数据插入时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间，记录数据最后更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
