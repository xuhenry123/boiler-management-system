package com.boiler.mapper.cost;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boiler.entity.cost.EnergyConsumption;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface EnergyConsumptionMapper extends BaseMapper<EnergyConsumption> {
    
    List<EnergyConsumption> selectByDateRangeAndStation(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("stationIds") List<Long> stationIds,
            @Param("energyType") String energyType);
    
    List<EnergyConsumption> selectByStationAndMonth(
            @Param("stationId") Long stationId,
            @Param("year") Integer year,
            @Param("month") Integer month,
            @Param("energyType") String energyType);
}
