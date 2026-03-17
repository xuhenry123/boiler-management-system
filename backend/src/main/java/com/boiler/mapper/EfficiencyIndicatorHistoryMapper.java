package com.boiler.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boiler.entity.EfficiencyIndicatorHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 能效指标历史数据访问层接口
 * 继承MyBatis Plus的BaseMapper，提供对efficiency_indicator_history表的基本增删改查操作
 */
@Mapper
public interface EfficiencyIndicatorHistoryMapper extends BaseMapper<EfficiencyIndicatorHistory> {
}
