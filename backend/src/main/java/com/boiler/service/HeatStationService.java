package com.boiler.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boiler.entity.HeatStation;
import com.boiler.mapper.HeatStationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 热力站业务逻辑层
 * 负责热力站相关的业务逻辑处理，包括查询、添加、修改等操作
 */
@Service
@RequiredArgsConstructor
public class HeatStationService {

    private final HeatStationMapper heatStationMapper;

    /**
     * 分页查询热力站列表
     * @param page 页码
     * @param size 每页数量
     * @param stationName 热力站名称（模糊查询）
     * @return 分页结果
     */
    public Page<HeatStation> listStations(Integer page, Integer size, String stationName) {
        Page<HeatStation> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<HeatStation> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(stationName)) {
            wrapper.like(HeatStation::getStationName, stationName);
        }
        wrapper.orderByDesc(HeatStation::getId);
        return heatStationMapper.selectPage(pageParam, wrapper);
    }

    /**
     * 根据ID查询热力站
     * @param id 热力站ID
     * @return 热力站信息
     */
    public HeatStation getStationById(Long id) {
        return heatStationMapper.selectById(id);
    }

    /**
     * 查询所有热力站
     * @return 热力站列表
     */
    public List<HeatStation> listAllStations() {
        return heatStationMapper.selectList(null);
    }

    /**
     * 查询运行中的热力站
     * @return 运行中的热力站列表
     */
    public List<HeatStation> listActiveStations() {
        LambdaQueryWrapper<HeatStation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HeatStation::getStatus, 1);
        return heatStationMapper.selectList(wrapper);
    }

    /**
     * 新增热力站
     * @param station 热力站信息
     * @return 是否成功
     */
    public boolean saveStation(HeatStation station) {
        return heatStationMapper.insert(station) > 0;
    }

    /**
     * 更新热力站信息
     * @param station 热力站信息
     * @return 是否成功
     */
    public boolean updateStation(HeatStation station) {
        return heatStationMapper.updateById(station) > 0;
    }
}
