package com.boiler.entity.cost;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 能效指标实体类
 * 对应数据库表 efficiency_metrics
 * 存储热力站的能效指标数据，用于能效评估和成本分析
 */
@Data
@TableName("efficiency_metrics")
public class EfficiencyMetrics implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 记录唯一标识ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 热力站ID，关联到对应的热力站 */
    private Long stationId;

    /** 热力站名称，便于显示和识别 */
    private String stationName;

    /** 能效值，如热效率、COP等 */
    private BigDecimal efficiencyValue;

    /** 能耗，单位为kWh或GJ */
    private BigDecimal energyConsumption;

    /** 单位成本，如元/kWh或元/GJ */
    private BigDecimal costPerUnit;

    /** 单位碳排放，如kgCO2/kWh */
    private BigDecimal carbonPerUnit;

    /** 统计日期，数据对应的日期 */
    private LocalDate statDate;

    /** 创建时间，记录数据插入时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
