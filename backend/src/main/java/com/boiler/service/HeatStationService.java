package com.boiler.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boiler.entity.HeatStation;
import com.boiler.mapper.HeatStationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HeatStationService {

    private final HeatStationMapper heatStationMapper;

    public Page<HeatStation> listStations(Integer page, Integer size, String stationName) {
        Page<HeatStation> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<HeatStation> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(stationName)) {
            wrapper.like(HeatStation::getStationName, stationName);
        }
        wrapper.orderByDesc(HeatStation::getId);
        return heatStationMapper.selectPage(pageParam, wrapper);
    }

    public HeatStation getStationById(Long id) {
        return heatStationMapper.selectById(id);
    }

    public List<HeatStation> listAllStations() {
        return heatStationMapper.selectList(null);
    }

    public List<HeatStation> listActiveStations() {
        LambdaQueryWrapper<HeatStation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HeatStation::getStatus, 1);
        return heatStationMapper.selectList(wrapper);
    }

    public boolean saveStation(HeatStation station) {
        return heatStationMapper.insert(station) > 0;
    }

    public boolean updateStation(HeatStation station) {
        return heatStationMapper.updateById(station) > 0;
    }
}
