package com.boiler.entity.cost;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 碳排放因子实体类
 * 对应数据库表 carbon_factor
 * 存储各类能源的碳排放因子，用于计算碳排放量和碳交易
 */
@Data
@TableName("carbon_factor")
public class CarbonFactor implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 碳排放因子唯一标识ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 能源类型，如"电力"、"天然气"、"煤炭"等 */
    private String energyType;

    /** 因子值，单位能源消耗产生的碳排放量 */
    private BigDecimal factorValue;

    /** 因子单位，如"kgCO2/kWh"、"kgCO2/m³" */
    private String factorUnit;

    /** 数据来源，因子值的来源或依据 */
    private String source;

    /** 生效日期，该因子值开始生效的日期 */
    private LocalDate effectiveDate;

    /** 创建时间，记录数据插入时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
