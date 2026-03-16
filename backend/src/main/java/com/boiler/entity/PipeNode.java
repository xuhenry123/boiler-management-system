package com.boiler.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("pipe_node")
public class PipeNode implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String nodeCode;
    private String nodeName;
    private String nodeType;
    private Long parentId;
    private BigDecimal temperature;
    private BigDecimal pressure;
    private BigDecimal flow;
    private BigDecimal elevation;
    private BigDecimal designTemp;
    private BigDecimal designPressure;
    private Integer sortOrder;
    private String location;
    private String description;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
