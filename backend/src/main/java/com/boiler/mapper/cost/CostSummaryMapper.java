package com.boiler.mapper.cost;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boiler.entity.cost.CostSummary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface CostSummaryMapper extends BaseMapper<CostSummary> {
    
    List<CostSummary> selectByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("stationIds") List<Long> stationIds,
            @Param("costType") String costType);
    
    CostSummary selectByStationAndDate(
            @Param("stationId") Long stationId,
            @Param("statDate") LocalDate statDate,
            @Param("costType") String costType,
            @Param("energyType") String energyType);
}
