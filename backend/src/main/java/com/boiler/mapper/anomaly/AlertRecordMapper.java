package com.boiler.mapper.anomaly;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boiler.entity.anomaly.AlertRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 告警记录数据访问层接口
 * 继承MyBatis Plus的BaseMapper，提供对alert_record表的基本增删改查操作
 */
@Mapper
public interface AlertRecordMapper extends BaseMapper<AlertRecord> {
}
