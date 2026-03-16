package com.boiler.mapper.cost;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boiler.entity.cost.EnergyPrice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface EnergyPriceMapper extends BaseMapper<EnergyPrice> {
    
    EnergyPrice selectEffectivePrice(
            @Param("energyType") String energyType,
            @Param("effectiveDate") LocalDate effectiveDate);
    
    List<EnergyPrice> selectByDateRange(
            @Param("energyType") String energyType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
