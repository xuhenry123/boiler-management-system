package com.boiler.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 热力站排名数据传输对象
 * 用于传递热力站能效指标排名数据
 */
@Data
public class StationRankingDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 排名序号 */
    private Integer rank;

    /** 热力站ID */
    private Long stationId;

    /** 热力站名称 */
    private String stationName;

    /** 能效比（COP） */
    private BigDecimal cop;

    /** 热损耗率 */
    private BigDecimal heatLossRate;

    /** 供热平衡率 */
    private BigDecimal supplyBalance;

    /** 趋势，up-上升/down-下降/stable-稳定 */
    private String trend;
}
