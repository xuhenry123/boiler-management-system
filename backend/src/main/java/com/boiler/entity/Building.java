package com.boiler.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("building")
public class Building implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String buildingCode;
    private String buildingName;
    private String address;
    private BigDecimal areaTotal;
    private BigDecimal areaHeated;
    private Integer buildYear;
    private String buildingType;
    private String insulationMaterial;
    private BigDecimal windowArea;
    private BigDecimal floorHeight;
    private BigDecimal heatTransferCoefficient;
    private BigDecimal heatCapacity;
    private Long stationId;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
