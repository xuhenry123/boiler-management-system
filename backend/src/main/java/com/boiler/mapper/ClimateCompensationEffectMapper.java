package com.boiler.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boiler.entity.ClimateCompensationEffect;
import org.apache.ibatis.annotations.Mapper;

/**
 * 气候补偿效果数据访问层接口
 * 继承MyBatis Plus的BaseMapper，提供对climate_compensation_effect表的基本增删改查操作
 */
@Mapper
public interface ClimateCompensationEffectMapper extends BaseMapper<ClimateCompensationEffect> {
}
