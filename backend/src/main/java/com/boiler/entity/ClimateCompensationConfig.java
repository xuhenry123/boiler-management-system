package com.boiler.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 气候补偿配置实体类
 * 对应数据库表 climate_compensation_config
 * 气候补偿是一种根据室外温度调节供热温度的节能技术
 */
@Data
@TableName("climate_compensation_config")
public class ClimateCompensationConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 配置唯一标识ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 所属热力站ID，关联到对应的热力站 */
    private Long stationId;

    /** 配置名称，便于识别和管理 */
    private String configName;

    /** 补偿模式，如"分时段补偿"、"室外温度补偿"等 */
    private String compensationMode;

    /** 曲线类型，如"线性"、"多项式"等 */
    private String curveType;

    /** 室外温度下限，单位为℃，补偿曲线的最低室外温度点 */
    private BigDecimal outdoorTempMin;

    /** 室外温度上限，单位为℃，补偿曲线的最高室外温度点 */
    private BigDecimal outdoorTempMax;

    /** 供水温度，单位为℃，对应的设计供水温度 */
    private BigDecimal supplyTemp;

    /** 回水温度，单位为℃，对应的设计回水温度 */
    private BigDecimal returnTemp;

    /** 室内目标温度，单位为℃，用户设定的目标室温 */
    private BigDecimal indoorTargetTemp;

    /** 是否激活，1-激活，0-未激活 */
    private Integer isActive;

    /** 是否启用，1-启用，0-禁用 */
    private Integer isEnabled;

    /** 创建时间，记录数据插入时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间，记录数据最后更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
