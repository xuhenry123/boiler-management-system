package com.boiler.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class EfficiencyComparisonDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private MetricData realtime;
    private MetricData history;
    private ComparisonData comparison;

    @Data
    public static class MetricData implements Serializable {
        private BigDecimal cop;
        private BigDecimal heatLossRate;
        private BigDecimal supplyBalance;
        private String period;
    }

    @Data
    public static class ComparisonData implements Serializable {
        private String copChange;
        private String heatLossChange;
        private String balanceChange;
    }
}
