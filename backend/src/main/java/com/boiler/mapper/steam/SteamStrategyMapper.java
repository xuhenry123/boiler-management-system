package com.boiler.mapper.steam;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boiler.entity.steam.SteamStrategy;
import org.apache.ibatis.annotations.Mapper;

/**
 * 蒸汽策略数据访问层接口
 * 继承MyBatis Plus的BaseMapper，提供对steam_strategy表的基本增删改查操作
 */
@Mapper
public interface SteamStrategyMapper extends BaseMapper<SteamStrategy> {
}
