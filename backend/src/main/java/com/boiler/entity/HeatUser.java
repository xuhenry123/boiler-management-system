package com.boiler.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 热用户实体类
 * 对应数据库表 heat_user，存储供热用户的详细信息
 * 热用户可以是居民、商业用户或工业用户
 */
@Data
@TableName("heat_user")
public class HeatUser implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 热用户唯一标识ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 用户编码，用于系统内部唯一标识 */
    private String userCode;

    /** 用户名称，通常为用户姓名或单位名称 */
    private String userName;

    /** 所属建筑ID，关联到对应的建筑物 */
    private Long buildingId;

    /** 单元号，用户所在的建筑单元号 */
    private String unitNo;

    /** 房号，用户所在的具体房间号 */
    private String roomNo;

    /** 供热面积，单位为㎡，用户的实际供热面积 */
    private BigDecimal area;

    /** 目标温度，单位为℃，用户设定的期望室内温度 */
    private BigDecimal targetTemp;

    /** 状态，0-停供，1-供热中，2-欠费 */
    private Integer status;

    /** 创建时间，记录数据插入时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间，记录数据最后更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
