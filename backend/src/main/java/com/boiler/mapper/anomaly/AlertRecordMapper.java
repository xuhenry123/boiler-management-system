package com.boiler.mapper.anomaly;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boiler.entity.anomaly.AlertRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AlertRecordMapper extends BaseMapper<AlertRecord> {
}
