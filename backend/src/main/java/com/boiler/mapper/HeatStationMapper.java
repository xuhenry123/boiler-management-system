package com.boiler.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boiler.entity.HeatStation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 热力站数据访问层接口
 * 继承MyBatis Plus的BaseMapper，提供对heat_station表的基本增删改查操作
 */
@Mapper
public interface HeatStationMapper extends BaseMapper<HeatStation> {
}
