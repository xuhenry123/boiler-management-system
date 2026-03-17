package com.boiler.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boiler.entity.IndoorTemperature;
import com.boiler.mapper.IndoorTemperatureMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 温度业务逻辑层
 * 负责室内温度相关的业务逻辑处理，包括温度查询、数据采集等操作
 */
@Service
@RequiredArgsConstructor
public class TemperatureService {

    private final IndoorTemperatureMapper temperatureMapper;

    /**
     * 分页查询室内温度记录
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param page 页码
     * @param size 每页数量
     * @return 分页结果
     */
    public Page<IndoorTemperature> listTemperatures(Long userId, LocalDateTime startTime, 
                                                     LocalDateTime endTime, Integer page, Integer size) {
        Page<IndoorTemperature> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<IndoorTemperature> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(IndoorTemperature::getUserId, userId);
        }
        if (startTime != null) {
            wrapper.ge(IndoorTemperature::getCollectTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(IndoorTemperature::getCollectTime, endTime);
        }
        wrapper.orderByDesc(IndoorTemperature::getCollectTime);
        return temperatureMapper.selectPage(pageParam, wrapper);
    }

    /**
     * 获取用户最新温度记录
     * @param userId 用户ID
     * @return 最新温度记录
     */
    public IndoorTemperature getLatestTemperature(Long userId) {
        LambdaQueryWrapper<IndoorTemperature> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IndoorTemperature::getUserId, userId);
        wrapper.orderByDesc(IndoorTemperature::getCollectTime);
        wrapper.last("LIMIT 1");
        return temperatureMapper.selectOne(wrapper);
    }

    /**
     * 获取用户最新温度值
     * @param userId 用户ID
     * @return 最新温度值
     */
    public BigDecimal getLatestTempValue(Long userId) {
        IndoorTemperature temp = getLatestTemperature(userId);
        return temp != null ? temp.getTemperature() : null;
    }

    /**
     * 保存温度记录
     * @param temperature 温度记录
     * @return 是否成功
     */
    public boolean saveTemperature(IndoorTemperature temperature) {
        return temperatureMapper.insert(temperature) > 0;
    }

    /**
     * 批量保存温度记录
     * @param temperatures 温度记录列表
     * @return 是否成功
     */
    public boolean batchSaveTemperature(List<IndoorTemperature> temperatures) {
        for (IndoorTemperature temp : temperatures) {
            temperatureMapper.insert(temp);
        }
        return true;
    }
}
