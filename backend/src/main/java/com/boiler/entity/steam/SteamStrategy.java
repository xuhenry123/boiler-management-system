package com.boiler.entity.steam;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.ioSerializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 蒸汽策略实体类
 * 对应数据库表 steam_strategy
 * 存储蒸汽锅炉的运行策略配置，如调度计划、功率目标等
 */
@Data
@TableName("steam_strategy")
public class SteamStrategy implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 策略唯一标识ID，使用UUID生成 */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 锅炉ID，关联到对应的蒸汽锅炉 */
    private String boilerId;

    /** 策略类型，如"节能策略"、"最优效率策略"、"成本优先策略" */
    private String strategyType;

    /** 目标功率，单位通常为MW或t/h */
    private BigDecimal targetPower;

    /** 调度计划，JSON格式存储调度安排 */
    private String schedule;

    /** 状态，策略是否启用 */
    private String status;

    /** 创建时间，记录数据插入时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间，记录数据最后更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
