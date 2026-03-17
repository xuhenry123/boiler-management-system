package com.boiler.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 锅炉实体类
 * 对应数据库表 boiler，存储锅炉设备的基本信息
 */
@Data
@TableName("boiler")
public class Boiler implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 锅炉唯一标识ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 锅炉编码，用于系统内部唯一标识 */
    private String boilerCode;

    /** 锅炉名称，便于识别和管理 */
    private String boilerName;

    /** 锅炉类型，如燃气锅炉、燃煤锅炉、电锅炉等 */
    private String boilerType;

    /** 制造商，锅炉设备的生产厂家 */
    private String manufacturer;

    /** 型号，锅炉的具体型号规格 */
    private String model;

    /** 额定容量，单位通常为MW（兆瓦） */
    private BigDecimal ratedCapacity;

    /** 额定压力，单位通常为MPa（兆帕） */
    private BigDecimal ratedPressure;

    /** 设计效率，锅炉的设计热效率百分比 */
    private BigDecimal designEfficiency;

    /** 燃料类型，如天然气、煤炭、电能等 */
    private String fuelType;

    /** 状态，0-停运，1-运行中，2-维修中 */
    private Integer status;

    /** 创建时间，记录数据插入时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间，记录数据最后更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
