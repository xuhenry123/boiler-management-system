package com.boiler.dto.cost;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RankItem {
    private Integer rank;
    private Long stationId;
    private String stationName;
    private BigDecimal value;
    private BigDecimal trend;
}
