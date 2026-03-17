package com.boiler.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 阀门设备实体类
 * 对应数据库表 valve_device，存储供热系统中的阀门设备信息
 * 阀门是调节供热流量的重要设备，用于控制各支路的供热量
 */
@Data
@TableName("valve_device")
public class ValveDevice implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 阀门唯一标识ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 阀门编码，用于系统内部唯一标识 */
    private String valveCode;

    /** 阀门名称，便于识别和管理 */
    private String valveName;

    /** 阀门类型，如"调节阀"、"截止阀"、"平衡阀"等 */
    private String valveType;

    /** 所属节点ID，关联到供热系统中的具体节点 */
    private Long nodeId;

    /** 制造商，阀门设备的生产厂家 */
    private String manufacturer;

    /** 型号，阀门设备的具体型号规格 */
    private String model;

    /** 口径，单位为mm（毫米），阀门管道的直径 */
    private BigDecimal diameter;

    /** 开度，0-1之间的比例值，表示当前阀门开启程度 */
    private BigDecimal openRatio;

    /** 最小位置限制，阀门可开启的最小位置 */
    private BigDecimal positionLimitMin;

    /** 最大位置限制，阀门可开启的最大位置 */
    private BigDecimal positionLimitMax;

    /** 响应时间，单位为秒，阀门从全开到全关所需时间 */
    private BigDecimal responseTime;

    /** 控制协议，如"Modbus"、"4-20mA"、"0-10V"等 */
    private String controlProtocol;

    /** 状态，0-故障，1-正常，2-维修中 */
    private Integer status;

    /** 创建时间，记录数据插入时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间，记录数据最后更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
