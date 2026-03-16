package com.boiler.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ResidentTemperatureDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long buildingId;
    private Long userId;
    private String roomCode;
    private BigDecimal temperature;
    private String dataSource;
    private LocalDateTime collectTime;
    private String remark;
}
