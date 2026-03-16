package com.boiler.dto.cost;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class StatisticsResult {
    private List<StationStatItem> stations;
    private BigDecimal totalConsumption;
    private BigDecimal totalCost;
    private BigDecimal totalCarbon;
    private BigDecimal averageEfficiency;
}

@Data
class StationStatItem {
    private Long stationId;
    private String stationName;
    private BigDecimal consumption;
    private BigDecimal cost;
    private BigDecimal carbon;
    private BigDecimal efficiency;
}
