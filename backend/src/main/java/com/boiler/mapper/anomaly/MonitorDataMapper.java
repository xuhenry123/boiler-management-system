package com.boiler.mapper.anomaly;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boiler.entity.anomaly.MonitorData;
import org.apache.ibatis.annotations.Mapper;

/**
 * 监控数据数据访问层接口
 * 继承MyBatis Plus的BaseMapper，提供对monitor_data表的基本增删改查操作
 */
@Mapper
public interface MonitorDataMapper extends BaseMapper<MonitorData> {
}
