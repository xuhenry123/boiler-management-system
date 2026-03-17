package com.boiler.entity.steam;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户档案实体类
 * 对应数据库表 user_profile
 * 存储蒸汽用户的企业信息和用能特征，用于用能分析和优化
 */
@Data
@TableName("user_profile")
public class UserProfile implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 用户档案唯一标识ID，使用UUID生成 */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 企业ID，关联到对应的企业 */
    private String enterpriseId;

    /** 企业名称，便于显示和识别 */
    private String enterpriseName;

    /** 生产模式，如"连续生产"、"间歇生产"、"季节性生产" */
    private String productionPattern;

    /** 用能特征，如"高峰用电"、"平稳用电"、"可调节负荷" */
    private String energyFeatures;

    /** 档案评分，综合评分，用于用户分级管理 */
    private Integer profileScore;

    /** 最后更新时间，档案信息的最新更新时间 */
    private LocalDateTime lastUpdated;

    /** 创建时间，记录数据插入时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间，记录数据最后更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
