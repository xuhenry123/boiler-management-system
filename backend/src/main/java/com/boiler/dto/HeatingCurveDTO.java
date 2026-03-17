package com.boiler.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 供热曲线数据传输对象
 * 用于传递供热曲线的配置和优化数据
 */
@Data
public class HeatingCurveDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 当前供热曲线控制点列表 */
    private List<CurvePoint> currentCurve;

    /** 优化后的供热曲线控制点列表 */
    private List<CurvePoint> optimizedCurve;

    /** 预计节能率，单位为百分比 */
    private BigDecimal estimatedSavings;

    /** 分析说明 */
    private String analysis;

    /**
     * 曲线控制点内部类
     * 表示供热曲线上的一个控制点
     */
    @Data
    public static class CurvePoint implements Serializable {
        /** 室外温度，单位为℃ */
        private BigDecimal outdoorTemp;

        /** 供水温度，单位为℃ */
        private BigDecimal supplyTemp;

        /** 回水温度，单位为℃ */
        private BigDecimal returnTemp;
    }
}
