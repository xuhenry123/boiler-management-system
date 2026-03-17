package com.boiler.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 气候补偿效果实体类
 * 对应数据库表 climate_compensation_effect
 * 记录气候补偿技术的应用效果，用于评估节能效益
 */
@Data
@TableName("climate_compensation_effect")
public class ClimateCompensationEffect implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 记录唯一标识ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 热力站ID，关联到对应的热力站 */
    private Long stationId;

    /** 记录时间，数据记录的时间点 */
    private LocalDateTime recordDate;

    /** 室外温度，单位为℃ */
    private BigDecimal outdoorTemp;

    /** 目标供水温度，单位为℃，根据补偿曲线计算的目标温度 */
    private BigDecimal targetSupplyTemp;

    /** 实际供水温度，单位为℃，实际运行中的供水温度 */
    private BigDecimal actualSupplyTemp;

    /** 补偿前能耗，单位为kWh或GJ */
    private BigDecimal energyBefore;

    /** 补偿后能耗，单位为kWh或GJ */
    private BigDecimal energyAfter;

    /** 节能率，节能百分比 */
    private BigDecimal savingsRate;

    /** 数据类型，如"日"、"月"、"年" */
    private String dataType;

    /** 创建时间，记录数据插入时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间，记录数据最后更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
