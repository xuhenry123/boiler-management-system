package com.boiler.entity.steam;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 蒸汽锅炉实体类
 * 对应数据库表 steam_boiler
 * 存储蒸汽锅炉设备的基本信息，用于蒸汽供热系统的管理
 */
@Data
@TableName("steam_boiler")
public class SteamBoiler implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 锅炉唯一标识ID，使用UUID生成 */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 锅炉名称，便于识别和管理 */
    private String name;

    /** 企业ID，关联到所属企业 */
    private String enterpriseId;

    /** 企业名称，便于显示和识别 */
    private String enterpriseName;

    /** 容量，单位通常为t/h（吨/小时），表示蒸汽产量 */
    private BigDecimal capacity;

    /** 型号，锅炉的具体型号规格 */
    private String model;

    /** 状态，如"运行中"、"停运"、"维修" */
    private String status;

    /** 最高工作压力，单位为MPa */
    private BigDecimal maxPressure;

    /** 最低工作压力，单位为MPa */
    private BigDecimal minPressure;

    /** 额定温度，单位为℃，锅炉的设计工作温度 */
    private BigDecimal ratedTemperature;

    /** 安装日期，锅炉的安装时间 */
    private LocalDateTime installDate;

    /** 创建时间，记录数据插入时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间，记录数据最后更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
