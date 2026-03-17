package com.boiler.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 热力站实体类
 * 对应数据库表 heat_station，存储热力站（换热站）的基本信息
 * 热力站是集中供热系统中的关键设施，负责将热源的热量传递给用户
 */
@Data
@TableName("heat_station")
public class HeatStation implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 热力站唯一标识ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 热力站编码，用于系统内部唯一标识 */
    private String stationCode;

    /** 热力站名称，便于识别和管理 */
    private String stationName;

    /** 地址，热力站的地理位置 */
    private String address;

    /** 设计容量，单位通常为MW（兆瓦），表示热力站的设计供热能力 */
    private BigDecimal designCapacity;

    /** 设计流量，单位通常为t/h（吨/小时），表示设计工况下的水流量 */
    private BigDecimal designFlow;

    /** 一次侧供水温度，单位为℃，高温热水进入热力站的温度 */
    private BigDecimal primarySupplyTemp;

    /** 一次侧回水温度，单位为℃，高温热水离开热力站返回热源的温度 */
    private BigDecimal primaryReturnTemp;

    /** 二次侧供水温度，单位为℃，向用户供热的热水温度 */
    private BigDecimal secondarySupplyTemp;

    /** 二次侧回水温度，单位为℃，用户侧返回的热水温度 */
    private BigDecimal secondaryReturnTemp;

    /** 状态，0-停运，1-运行中，2-故障 */
    private Integer status;

    /** 创建时间，记录数据插入时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间，记录数据最后更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
