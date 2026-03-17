package com.boiler.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boiler.entity.Boiler;
import org.apache.ibatis.annotations.Mapper;

/**
 * 锅炉设备数据访问层接口
 * 继承MyBatis Plus的BaseMapper，提供对boiler表的基本增删改查操作
 */
@Mapper
public interface BoilerMapper extends BaseMapper<Boiler> {
}
