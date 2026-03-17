package com.boiler.mapper.steam;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boiler.entity.steam.SteamBoiler;
import org.apache.ibatis.annotations.Mapper;

/**
 * 蒸汽锅炉数据访问层接口
 * 继承MyBatis Plus的BaseMapper，提供对steam_boiler表的基本增删改查操作
 */
@Mapper
public interface SteamBoilerMapper extends BaseMapper<SteamBoiler> {
}
