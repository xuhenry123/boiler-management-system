package com.boiler.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 能效指标历史实体类
 * 对应数据库表 efficiency_indicator_history
 * 存储热力站的能效指标历史数据，用于能效分析和优化
 */
@Data
@TableName("efficiency_indicator_history")
public class EfficiencyIndicatorHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 记录唯一标识ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 热力站ID，关联到对应的热力站 */
    private Long stationId;

    /** 能效比（COP），供热系统的性能系数 */
    @TableField("cop")
    private BigDecimal cop;

    /** 热损耗率，供热过程中的热量损失百分比 */
    @TableField("heat_loss_rate")
    private BigDecimal heatLossRate;

    /** 供热平衡率，反映供热系统的平衡性 */
    @TableField("supply_balance")
    private BigDecimal supplyBalance;

    /** 平均室温，单位为℃，用户侧的平均室内温度 */
    @TableField("avg_indoor_temp")
    private BigDecimal avgIndoorTemp;

    /** 供热量（输入），单位为GJ或MWh */
    @TableField("heat_input")
    private BigDecimal heatInput;

    /** 供热量（输出），单位为GJ或MWh */
    @TableField("heat_output")
    private BigDecimal heatOutput;

    /** 记录日期，数据对应的日期 */
    @TableField("record_date")
    private LocalDate recordDate;

    /** 创建时间，记录数据插入时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
