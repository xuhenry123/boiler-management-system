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

@Service
@RequiredArgsConstructor
public class TemperatureService {

    private final IndoorTemperatureMapper temperatureMapper;

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

    public IndoorTemperature getLatestTemperature(Long userId) {
        LambdaQueryWrapper<IndoorTemperature> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IndoorTemperature::getUserId, userId);
        wrapper.orderByDesc(IndoorTemperature::getCollectTime);
        wrapper.last("LIMIT 1");
        return temperatureMapper.selectOne(wrapper);
    }

    public BigDecimal getLatestTempValue(Long userId) {
        IndoorTemperature temp = getLatestTemperature(userId);
        return temp != null ? temp.getTemperature() : null;
    }

    public boolean saveTemperature(IndoorTemperature temperature) {
        return temperatureMapper.insert(temperature) > 0;
    }

    public boolean batchSaveTemperature(List<IndoorTemperature> temperatures) {
        for (IndoorTemperature temp : temperatures) {
            temperatureMapper.insert(temp);
        }
        return true;
    }
}
