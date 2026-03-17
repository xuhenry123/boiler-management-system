package com.boiler.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 室内温度实体类
 * 对应数据库表 indoor_temperature，存储用户室内温度监测数据
 * 用于监测和分析用户室温是否达标
 */
@Data
@TableName("indoor_temperature")
public class IndoorTemperature implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 记录唯一标识ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 热用户ID，关联到对应的热用户 */
    private Long userId;

    /** 室内温度值，单位为℃ */
    private BigDecimal temperature;

    /** 是否异常，1-异常，0-正常 */
    private Integer isAbnormal;

    /** 数据来源，如"智能温控器"、"人工抄表"等 */
    private String dataSource;

    /** 采集时间，数据采集的具体时间 */
    private LocalDateTime collectTime;

    /** 创建时间，记录数据插入时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
