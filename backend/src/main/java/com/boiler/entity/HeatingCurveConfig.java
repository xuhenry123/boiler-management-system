package com.boiler.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 供热曲线配置实体类
 * 对应数据库表 heating_curve_config
 * 供热曲线描述室外温度与供热参数（供水温度、回水温度等）的对应关系
 */
@Data
@TableName("heating_curve_config")
public class HeatingCurveConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 配置唯一标识ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 所属热力站ID，关联到对应的热力站 */
    private Long stationId;

    /** 曲线类型，如"标准曲线"、"优化曲线"等 */
    private String curveType;

    /** 曲线数据，JSON格式存储曲线的控制点数据 */
    private String curveData;

    /** 是否激活，1-激活，0-未激活 */
    private Integer isActive;

    /** 生效时间，该配置开始生效的时间 */
    private LocalDateTime effectiveTime;

    /** 创建时间，记录数据插入时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间，记录数据最后更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
