package com.boiler.entity.anomaly;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 告警配置实体类
 * 对应数据库表 alert_config
 * 定义系统中各类告警规则的配置，包括阈值、告警级别、通知方式等
 */
@Data
@TableName("alert_config")
public class AlertConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 配置唯一标识ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 配置名称，便于识别和管理 */
    private String configName;

    /** 设备类型，如"热力站"、"锅炉"、"阀门"等 */
    private String deviceType;

    /** 指标类型，如"温度"、"压力"、"流量"等 */
    private String metricType;

    /** 阈值下限，低于此值触发告警 */
    private BigDecimal thresholdMin;

    /** 阈值上限，高于此值触发告警 */
    private BigDecimal thresholdMax;

    /** 告警级别，1-提示，2-一般，3-严重，4-紧急 */
    private Integer severity;

    /** 告警使能，1-启用，0-禁用 */
    private Integer alertEnabled;

    /** 微信通知使能，1-启用，0-禁用 */
    private Integer wechatEnabled;

    /** 告警消息，告警触发时发送的消息内容 */
    private String alertMessage;

    /** 建议措施，处理告警的建议操作 */
    private String advice;

    /** 状态，0-停用，1-启用 */
    private Integer status;

    /** 创建时间，记录数据插入时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间，记录数据最后更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
