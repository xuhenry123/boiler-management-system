package com.boiler.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 住户温度实体类
 * 对应数据库表 resident_temperature，存储居民用户室内温度详细数据
 * 与IndoorTemperature相比，增加了建筑和房间的关联信息
 */
@Data
@TableName("resident_temperature")
public class ResidentTemperature implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 记录唯一标识ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 建筑ID，关联到对应的建筑物 */
    private Long buildingId;

    /** 用户ID，关联到对应的热用户 */
    private Long userId;

    /** 房间编码，标识具体的房间位置 */
    private String roomCode;

    /** 室内温度值，单位为℃ */
    private BigDecimal temperature;

    /** 数据来源，如"智能温控器"、"人工抄表"、"物联网设备"等 */
    private String dataSource;

    /** 采集时间，数据采集的具体时间 */
    private LocalDateTime collectTime;

    /** 备注，额外的说明信息 */
    private String remark;

    /** 创建时间，记录数据插入时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
