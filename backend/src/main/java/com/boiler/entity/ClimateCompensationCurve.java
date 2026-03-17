package com.boiler.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 气候补偿曲线实体类
 * 对应数据库表 climate_compensation_curve
 * 存储气候补偿曲线上的各个控制点，描述室外温度与供热温度的对应关系
 */
@Data
@TableName("climate_compensation_curve")
public class ClimateCompensationCurve implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 曲线点唯一标识ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 所属配置ID，关联到对应的气候补偿配置 */
    private Long configId;

    /** 曲线名称，便于识别和管理 */
    private String curveName;

    /** 室外温度值，单位为℃，补偿曲线上的室外温度控制点 */
    private BigDecimal outdoorTemp;

    /** 供水温度，单位为℃，对应室外温度下的供水温度设定值 */
    private BigDecimal supplyTemp;

    /** 回水温度，单位为℃，对应室外温度下的回水温度设定值 */
    private BigDecimal returnTemp;

    /** 排序序号，控制点在曲线中的顺序 */
    private Integer sortOrder;

    /** 创建时间，记录数据插入时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间，记录数据最后更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
