package com.boiler.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class StationRankingDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer rank;
    private Long stationId;
    private String stationName;
    private BigDecimal cop;
    private BigDecimal heatLossRate;
    private BigDecimal supplyBalance;
    private String trend;
}
