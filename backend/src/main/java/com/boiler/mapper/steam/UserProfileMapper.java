package com.boiler.mapper.steam;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boiler.entity.steam.UserProfile;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户档案数据访问层接口
 * 继承MyBatis Plus的BaseMapper，提供对user_profile表的基本增删改查操作
 */
@Mapper
public interface UserProfileMapper extends BaseMapper<UserProfile> {
}
