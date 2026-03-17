package com.boiler.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boiler.entity.HeatUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 热用户数据访问层接口
 * 继承MyBatis Plus的BaseMapper，提供对heat_user表的基本增删改查操作
 */
@Mapper
public interface HeatUserMapper extends BaseMapper<HeatUser> {
}
