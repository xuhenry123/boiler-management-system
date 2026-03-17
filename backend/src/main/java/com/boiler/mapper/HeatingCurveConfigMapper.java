package com.boiler.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boiler.entity.HeatingCurveConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 供热曲线配置数据访问层接口
 * 继承MyBatis Plus的BaseMapper，提供对heating_curve_config表的基本增删改查操作
 */
@Mapper
public interface HeatingCurveConfigMapper extends BaseMapper<HeatingCurveConfig> {
}
