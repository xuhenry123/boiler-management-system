package com.boiler.dto.cost;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CostQueryRequest {
    private String energyType;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Long> stationIds;
    private String timeGranularity;
}
