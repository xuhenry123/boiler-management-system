package com.boiler.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boiler.entity.IndoorTemperature;
import org.apache.ibatis.annotations.Mapper;

/**
 * 室内温度数据访问层接口
 * 继承MyBatis Plus的BaseMapper，提供对indoor_temperature表的基本增删改查操作
 */
@Mapper
public interface IndoorTemperatureMapper extends BaseMapper<IndoorTemperature> {
}
