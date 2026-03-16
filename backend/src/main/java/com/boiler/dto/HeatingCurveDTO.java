package com.boiler.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class HeatingCurveDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<CurvePoint> currentCurve;
    private List<CurvePoint> optimizedCurve;
    private BigDecimal estimatedSavings;
    private String analysis;

    @Data
    public static class CurvePoint implements Serializable {
        private BigDecimal outdoorTemp;
        private BigDecimal supplyTemp;
        private BigDecimal returnTemp;
    }
}
