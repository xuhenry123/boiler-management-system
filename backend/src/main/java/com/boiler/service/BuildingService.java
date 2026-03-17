package com.boiler.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boiler.entity.Building;
import com.boiler.mapper.BuildingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 建筑业务逻辑层
 * 负责建筑相关的业务逻辑处理，包括查询、添加、修改、删除等操作
 */
@Service
@RequiredArgsConstructor
public class BuildingService {

    private final BuildingMapper buildingMapper;

    /**
     * 分页查询建筑列表
     * @param page 页码
     * @param size 每页数量
     * @param buildingName 建筑名称（模糊查询）
     * @param stationId 热力站ID
     * @return 分页结果
     */
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

    /**
     * 根据ID查询建筑
     * @param id 建筑ID
     * @return 建筑信息
     */
    public Building getBuildingById(Long id) {
        return buildingMapper.selectById(id);
    }

    /**
     * 根据热力站ID查询建筑列表
     * @param stationId 热力站ID
     * @return 建筑列表
     */
    public List<Building> listBuildingsByStationId(Long stationId) {
        LambdaQueryWrapper<Building> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Building::getStationId, stationId);
        wrapper.eq(Building::getStatus, 1);
        return buildingMapper.selectList(wrapper);
    }

    /**
     * 新增建筑
     * @param building 建筑信息
     * @return 是否成功
     */
    public boolean saveBuilding(Building building) {
        return buildingMapper.insert(building) > 0;
    }

    /**
     * 更新建筑信息
     * @param building 建筑信息
     * @return 是否成功
     */
    public boolean updateBuilding(Building building) {
        return buildingMapper.updateById(building) > 0;
    }

    /**
     * 删除建筑
     * @param id 建筑ID
     * @return 是否成功
     */
    public boolean deleteBuilding(Long id) {
        return buildingMapper.deleteById(id) > 0;
    }
}
