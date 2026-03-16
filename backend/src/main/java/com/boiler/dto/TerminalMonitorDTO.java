package com.boiler.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TerminalMonitorDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long buildingId;
    private String buildingName;
    private BigDecimal avgIndoorTemp;
    private BigDecimal targetTemp;
    private BigDecimal deviation;
    private Integer sampleCount;
    private Integer abnormalCount;
    private LocalDateTime lastUpdate;
}
