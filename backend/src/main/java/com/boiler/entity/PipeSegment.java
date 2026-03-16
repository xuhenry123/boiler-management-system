package com.boiler.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("pipe_segment")
public class PipeSegment implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String segmentCode;
    private String segmentName;
    private Long startNodeId;
    private Long endNodeId;
    private String startNodeName;
    private String endNodeName;
    private BigDecimal length;
    private BigDecimal diameter;
    private BigDecimal roughness;
    private BigDecimal designFlow;
    private BigDecimal designVelocity;
    private BigDecimal pressureLoss;
    private String material;
    private String insulationType;
    private BigDecimal thermalConductivity;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
