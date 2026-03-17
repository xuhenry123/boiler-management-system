package com.boiler.entity.cost;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 能源价格实体类
 * 对应数据库表 energy_price
 * 存储各类能源（如电、天然气、煤炭等）的价格信息，用于成本计算
 */
@Data
@TableName("energy_price")
public class EnergyPrice implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 价格记录唯一标识ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 能源类型，如"电力"、"天然气"、"煤炭"、"燃油"等 */
    private String energyType;

    /** 单价，能源的单位价格 */
    private BigDecimal unitPrice;

    /** 单位，如"元/kWh"、"元/m³"、"元/吨" */
    private String unit;

    /** 生效日期，价格开始生效的日期 */
    private LocalDate effectiveDate;

    /** 过期日期，价格失效的日期 */
    private LocalDate expiredDate;

    /** 创建时间，记录数据插入时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
