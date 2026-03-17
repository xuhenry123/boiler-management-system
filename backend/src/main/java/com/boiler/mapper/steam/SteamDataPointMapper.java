package com.boiler.mapper.steam;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boiler.entity.steam.SteamDataPoint;
import org.apache.ibatis.annotations.Mapper;

/**
 * 蒸汽数据点数据访问层接口
 * 继承MyBatis Plus的BaseMapper，提供对steam_data_point表的基本增删改查操作
 */
@Mapper
public interface SteamDataPointMapper extends BaseMapper<SteamDataPoint> {
}
