package com.boiler.mapper.cost;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boiler.entity.cost.EfficiencyMetrics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface EfficiencyMetricsMapper extends BaseMapper<EfficiencyMetrics> {
    
    List<EfficiencyMetrics> selectByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("stationIds") List<Long> stationIds);
    
    List<EfficiencyMetrics> selectByStation(
            @Param("stationId") Long stationId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
