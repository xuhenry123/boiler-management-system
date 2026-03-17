package com.boiler.mapper.steam;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boiler.entity.steam.EnergyConsumption;
import org.apache.ibatis.annotations.Mapper;

/**
 * 蒸汽系统能耗数据访问层接口
 * 继承MyBatis Plus的BaseMapper，提供对energy_consumption表的基本增删改查操作
 */
@Mapper
public interface EnergyConsumptionMapper extends BaseMapper<EnergyConsumption> {
}
