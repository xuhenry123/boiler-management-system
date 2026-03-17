package com.boiler.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boiler.entity.ClimateCompensationConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 气候补偿配置数据访问层接口
 * 继承MyBatis Plus的BaseMapper，提供对climate_compensation_config表的基本增删改查操作
 */
@Mapper
public interface ClimateCompensationConfigMapper extends BaseMapper<ClimateCompensationConfig> {
}
