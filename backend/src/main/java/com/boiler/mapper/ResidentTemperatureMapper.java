package com.boiler.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boiler.entity.ResidentTemperature;
import org.apache.ibatis.annotations.Mapper;

/**
 * 住户温度数据访问层接口
 * 继承MyBatis Plus的BaseMapper，提供对resident_temperature表的基本增删改查操作
 */
@Mapper
public interface ResidentTemperatureMapper extends BaseMapper<ResidentTemperature> {
}
