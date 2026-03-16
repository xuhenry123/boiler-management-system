package com.boiler.dto.cost;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class RankQueryRequest {
    private String rankType;
    private String period;
    private List<Long> stationIds;
    private Integer topN;
}
