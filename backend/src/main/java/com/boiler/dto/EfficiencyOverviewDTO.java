package com.boiler.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class EfficiencyOverviewDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private BigDecimal cop;
    private BigDecimal heatLossRate;
    private BigDecimal supplyBalance;
    private BigDecimal avgIndoorTemp;
    private BigDecimal totalHeatInput;
    private BigDecimal totalHeatOutput;
    private BigDecimal targetTempCompliance;
}
