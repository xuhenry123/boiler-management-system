package com.boiler.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boiler.entity.HeatingCurveConfig;
import com.boiler.mapper.HeatingCurveConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 供热曲线业务逻辑层
 * 负责供热曲线配置相关的业务逻辑处理
 */
@Service
@RequiredArgsConstructor
public class HeatingCurveService {

    private final HeatingCurveConfigMapper heatingCurveConfigMapper;

    /**
     * 分页查询供热曲线配置列表
     */
    public Page<HeatingCurveConfig> listHeatingCurves(Integer page, Integer size, Long stationId) {
        Page<HeatingCurveConfig> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<HeatingCurveConfig> wrapper = new LambdaQueryWrapper<>();
        if (stationId != null) {
            wrapper.eq(HeatingCurveConfig::getStationId, stationId);
        }
        wrapper.orderByDesc(HeatingCurveConfig::getId);
        return heatingCurveConfigMapper.selectPage(pageParam, wrapper);
    }

    /**
     * 根据ID查询供热曲线配置
     */
    public HeatingCurveConfig getHeatingCurveById(Long id) {
        return heatingCurveConfigMapper.selectById(id);
    }

    /**
     * 获取热力站的激活曲线
     */
    public HeatingCurveConfig getActiveCurve(Long stationId) {
        LambdaQueryWrapper<HeatingCurveConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HeatingCurveConfig::getStationId, stationId)
               .eq(HeatingCurveConfig::getIsActive, 1);
        return heatingCurveConfigMapper.selectOne(wrapper);
    }

    /**
     * 保存供热曲线配置
     */
    public boolean saveHeatingCurve(HeatingCurveConfig config) {
        return heatingCurveConfigMapper.insert(config) > 0;
    }

    /**
     * 更新供热曲线配置
     */
    public boolean updateHeatingCurve(HeatingCurveConfig config) {
        return heatingCurveConfigMapper.updateById(config) > 0;
    }

    /**
     * 删除供热曲线配置
     */
    public boolean deleteHeatingCurve(Long id) {
        return heatingCurveConfigMapper.deleteById(id) > 0;
    }
}
