package com.boiler.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boiler.entity.ValveDevice;
import org.apache.ibatis.annotations.Mapper;

/**
 * 阀门设备数据访问层接口
 * 继承MyBatis Plus的BaseMapper，提供对valve_device表的基本增删改查操作
 */
@Mapper
public interface ValveDeviceMapper extends BaseMapper<ValveDevice> {
}
