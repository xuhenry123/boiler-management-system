package com.boiler.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boiler.entity.Building;
import com.boiler.mapper.BuildingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuildingService {

    private final BuildingMapper buildingMapper;

    public Page<Building> listBuildings(Integer page, Integer size, String buildingName, Long stationId) {
        Page<Building> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Building> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(buildingName)) {
            wrapper.like(Building::getBuildingName, buildingName);
        }
        if (stationId != null) {
            wrapper.eq(Building::getStationId, stationId);
        }
        wrapper.orderByDesc(Building::getId);
        return buildingMapper.selectPage(pageParam, wrapper);
    }

    public Building getBuildingById(Long id) {
        return buildingMapper.selectById(id);
    }

    public List<Building> listBuildingsByStationId(Long stationId) {
        LambdaQueryWrapper<Building> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Building::getStationId, stationId);
        wrapper.eq(Building::getStatus, 1);
        return buildingMapper.selectList(wrapper);
    }

    public boolean saveBuilding(Building building) {
        return buildingMapper.insert(building) > 0;
    }

    public boolean updateBuilding(Building building) {
        return buildingMapper.updateById(building) > 0;
    }

    public boolean deleteBuilding(Long id) {
        return buildingMapper.deleteById(id) > 0;
    }
}
