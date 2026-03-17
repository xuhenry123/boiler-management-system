package com.boiler.entity.steam;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 蒸汽系统能耗实体类
 * 对应数据库表 energy_consumption
 * 存储蒸汽用户的能耗数据，用于能耗统计和成本分析
 */
@Data
@TableName("energy_consumption")
public class EnergyConsumption implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 能耗记录唯一标识ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 企业ID，关联到对应的企业 */
    private String enterpriseId;

    /** 企业名称，便于显示和识别 */
    private String enterpriseName;

    /** 蒸汽消耗量，单位通常为t（吨） */
    private BigDecimal steamAmount;

    /** 水消耗量，单位通常为t（吨） */
    private BigDecimal waterAmount;

    /** 电消耗量，单位通常为kWh */
    private BigDecimal electricityAmount;

    /** 标准煤消耗量，单位通常为t（吨），用于能耗折标 */
    private BigDecimal standardCoal;

    /** 记录日期，数据对应的日期 */
    private LocalDate recordDate;

    /** 创建时间，记录数据插入时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间，记录数据最后更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
