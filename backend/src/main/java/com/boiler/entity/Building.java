package com.boiler.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 建筑实体类
 * 对应数据库表 building，存储供热区域内建筑物的信息
 * 包含建筑的热工参数，用于供热负荷计算和能耗分析
 */
@Data
@TableName("building")
public class Building implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 建筑唯一标识ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 建筑编码，用于系统内部唯一标识 */
    private String buildingCode;

    /** 建筑名称，便于识别和管理 */
    private String buildingName;

    /** 地址，建筑物的地理位置 */
    private String address;

    /** 总面积，单位为㎡（平方米），建筑物的总建筑面积 */
    private BigDecimal areaTotal;

    /** 采暖面积，单位为㎡，需要供热的有效面积 */
    private BigDecimal areaHeated;

    /** 建造年份，建筑物的建成年份，用于评估建筑年代和保温性能 */
    private Integer buildYear;

    /** 建筑类型，如住宅、办公、商业、工业等 */
    private String buildingType;

    /** 保温材料，建筑外墙使用的保温材料类型 */
    private String insulationMaterial;

    /** 窗户面积，单位为㎡，用于计算热负荷 */
    private BigDecimal windowArea;

    /** 层高，单位为m（米），建筑物的层高 */
    private BigDecimal floorHeight;

    /** 传热系数，单位为W/(㎡·K)，墙体或窗户的传热性能指标 */
    private BigDecimal heatTransferCoefficient;

    /** 热容量，单位为kJ/K，建筑物的热惰性指标 */
    private BigDecimal heatCapacity;

    /** 所属热力站ID，关联到对应的热力站 */
    private Long stationId;

    /** 状态，0-停供，1-供热中，2-维修中 */
    private Integer status;

    /** 创建时间，记录数据插入时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间，记录数据最后更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
