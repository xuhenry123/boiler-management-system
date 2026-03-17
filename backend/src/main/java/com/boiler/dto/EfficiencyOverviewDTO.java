package com.boiler.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 能效概览数据传输对象
 * 用于传递热力站能效指标的概览数据
 */
@Data
public class EfficiencyOverviewDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 能效比（COP），供热系统性能系数 */
    private BigDecimal cop;

    /** 热损耗率，供热过程中的热量损失百分比 */
    private BigDecimal heatLossRate;

    /** 供热平衡率，反映供热系统的平衡性 */
    private BigDecimal supplyBalance;

    /** 平均室温，单位为℃ */
    private BigDecimal avgIndoorTemp;

    /** 总供热量（输入），单位为GJ或MWh */
    private BigDecimal totalHeatInput;

    /** 总供热量（输出），单位为GJ或MWh */
    private BigDecimal totalHeatOutput;

    /** 目标温度达标率，用户设定温度达标的百分比 */
    private BigDecimal targetTempCompliance;
}
