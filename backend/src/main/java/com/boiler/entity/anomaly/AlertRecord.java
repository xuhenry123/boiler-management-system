package com.boiler.entity.anomaly;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 告警记录实体类
 * 对应数据库表 alert_record
 * 记录系统产生的所有告警信息，包括告警时间、处理状态等
 */
@Data
@TableName("alert_record")
public class AlertRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 告警记录唯一标识ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 异常记录ID，关联到对应的异常记录 */
    private Long anomalyId;

    /** 告警级别，如"提示"、"一般"、"严重"、"紧急" */
    private String alertLevel;

    /** 告警消息，告警的具体内容描述 */
    private String alertMessage;

    /** 告警时间，告警触发的时间 */
    private LocalDateTime alertTime;

    /** 是否已确认，true-已确认，false-未确认 */
    private Boolean acknowledged;

    /** 确认人，确认告警的处理人员 */
    private String acknowledgedBy;

    /** 确认时间，告警被确认的时间 */
    private LocalDateTime acknowledgedAt;

    /** 备注，额外的说明信息 */
    private String remark;

    /** 创建时间，记录数据插入时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间，记录数据最后更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
