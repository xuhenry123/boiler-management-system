package com.boiler.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class EfficiencyTrendDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<String> dates;
    private List<BigDecimal> cop;
    private List<BigDecimal> heatLossRate;
    private List<BigDecimal> supplyBalance;
}
