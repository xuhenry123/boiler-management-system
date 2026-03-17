package com.boiler.entity.cost;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 成本汇总实体类
 * 对应数据库表 cost_summary
 * 存储各热力站的能耗成本汇总数据，用于成本分析和报表
 */
@Data
@TableName("cost_summary")
public class CostSummary implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 汇总记录唯一标识ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 热力站ID，关联到对应的热力站 */
    private Long stationId;

    /** 热力站名称，便于显示和识别 */
    private String stationName;

    /** 能源类型，如"电力"、"天然气"等 */
    private String energyType;

    /** 成本类型，如"能耗成本"、"维护成本"、"人工成本" */
    private String costType;

    /** 消耗量，能源的消耗数量 */
    private BigDecimal consumption;

    /** 单价，能源的单价 */
    private BigDecimal unitPrice;

    /** 总成本，总费用 */
    private BigDecimal totalCost;

    /** 碳排放量，对应的碳排放量 */
    private BigDecimal carbonEmission;

    /** 统计日期，数据对应的日期 */
    private LocalDate statDate;

    /** 创建时间，记录数据插入时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
