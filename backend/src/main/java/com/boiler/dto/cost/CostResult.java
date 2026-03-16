package com.boiler.dto.cost;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CostResult {
    private Long stationId;
    private String stationName;
    private String energyType;
    private BigDecimal consumption;
    private BigDecimal unitPrice;
    private BigDecimal totalCost;
    private LocalDateTime dataTime;
}
