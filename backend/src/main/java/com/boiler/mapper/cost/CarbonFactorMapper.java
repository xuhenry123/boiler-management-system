package com.boiler.mapper.cost;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boiler.entity.cost.CarbonFactor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDate;

@Mapper
public interface CarbonFactorMapper extends BaseMapper<CarbonFactor> {
    
    CarbonFactor selectEffectiveFactor(
            @Param("energyType") String energyType,
            @Param("effectiveDate") LocalDate effectiveDate);
}
