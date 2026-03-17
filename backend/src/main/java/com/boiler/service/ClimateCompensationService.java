package com.boiler.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boiler.entity.ClimateCompensationConfig;
import com.boiler.entity.ClimateCompensationCurve;
import com.boiler.entity.ClimateCompensationEffect;
import com.boiler.mapper.ClimateCompensationConfigMapper;
import com.boiler.mapper.ClimateCompensationCurveMapper;
import com.boiler.mapper.ClimateCompensationEffectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 气候补偿业务逻辑层
 * 负责气候补偿相关的业务逻辑处理，包括配置管理、曲线管理、效果分析等
 */
@Service
@RequiredArgsConstructor
public class ClimateCompensationService {

    private final ClimateCompensationConfigMapper configMapper;
    private final ClimateCompensationCurveMapper curveMapper;
    private final ClimateCompensationEffectMapper effectMapper;

    /**
     * 分页查询气候补偿配置列表
     * @param page 页码
     * @param size 每页数量
     * @param stationId 热力站ID
     * @return 分页结果
     */
    public Page<ClimateCompensationConfig> listConfigs(Integer page, Integer size, Long stationId) {
        Page<ClimateCompensationConfig> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<ClimateCompensationConfig> wrapper = new LambdaQueryWrapper<>();
        if (stationId != null) {
            wrapper.eq(ClimateCompensationConfig::getStationId, stationId);
        }
        wrapper.orderByDesc(ClimateCompensationConfig::getId);
        return configMapper.selectPage(pageParam, wrapper);
    }

    /**
     * 根据ID查询气候补偿配置
     * @param id 配置ID
     * @return 配置信息
     */
    public ClimateCompensationConfig getConfigById(Long id) {
        return configMapper.selectById(id);
    }

    /**
     * 获取热力站当前激活的配置
     * @param stationId 热力站ID
     * @return 激活的配置信息
     */
    public ClimateCompensationConfig getActiveConfig(Long stationId) {
        LambdaQueryWrapper<ClimateCompensationConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClimateCompensationConfig::getStationId, stationId)
               .eq(ClimateCompensationConfig::getIsActive, 1);
        return configMapper.selectOne(wrapper);
    }

    /**
     * 保存气候补偿配置
     * 如果配置激活，则先清除该热力站的其他激活配置
     * @param config 配置信息
     * @return 是否成功
     */
    @Transactional
    public boolean saveConfig(ClimateCompensationConfig config) {
        if (config.getIsActive() != null && config.getIsActive() == 1) {
            clearActiveConfigs(config.getStationId());
        }
        return configMapper.insert(config) > 0;
    }

    /**
     * 更新气候补偿配置
     * 如果配置激活，则先清除该热力站的其他激活配置
     * @param config 配置信息
     * @return 是否成功
     */
    @Transactional
    public boolean updateConfig(ClimateCompensationConfig config) {
        if (config.getIsActive() != null && config.getIsActive() == 1) {
            clearActiveConfigs(config.getStationId());
        }
        return configMapper.updateById(config) > 0;
    }

    /**
     * 清除热力站的所有激活配置
     * @param stationId 热力站ID
     */
    private void clearActiveConfigs(Long stationId) {
        LambdaQueryWrapper<ClimateCompensationConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClimateCompensationConfig::getStationId, stationId)
               .eq(ClimateCompensationConfig::getIsActive, 1);
        List<ClimateCompensationConfig> activeConfigs = configMapper.selectList(wrapper);
        for (ClimateCompensationConfig config : activeConfigs) {
            config.setIsActive(0);
            configMapper.updateById(config);
        }
    }

    /**
     * 删除气候补偿配置
     * @param id 配置ID
     * @return 是否成功
     */
    public boolean deleteConfig(Long id) {
        return configMapper.deleteById(id) > 0;
    }

    /**
     * 获取配置下的所有曲线点
     * @param configId 配置ID
     * @return 曲线点列表
     */
    public List<ClimateCompensationCurve> getCurvesByConfigId(Long configId) {
        LambdaQueryWrapper<ClimateCompensationCurve> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClimateCompensationCurve::getConfigId, configId)
               .orderByAsc(ClimateCompensationCurve::getSortOrder);
        return curveMapper.selectList(wrapper);
    }

    /**
     * 保存补偿曲线
     * 先删除原有曲线，再保存新曲线
     * @param configId 配置ID
     * @param curves 曲线点列表
     * @return 是否成功
     */
    @Transactional
    public boolean saveCurves(Long configId, List<ClimateCompensationCurve> curves) {
        LambdaQueryWrapper<ClimateCompensationCurve> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClimateCompensationCurve::getConfigId, configId);
        curveMapper.delete(wrapper);

        for (int i = 0; i < curves.size(); i++) {
            ClimateCompensationCurve curve = curves.get(i);
            curve.setConfigId(configId);
            curve.setSortOrder(i);
            curveMapper.insert(curve);
        }
        return true;
    }

    /**
     * 查询气候补偿效果列表
     * @param stationId 热力站ID
     * @param dataType 数据类型
     * @param days 查询天数
     * @return 效果列表
     */
    public List<ClimateCompensationEffect> listEffects(Long stationId, String dataType, Integer days) {
        LambdaQueryWrapper<ClimateCompensationEffect> wrapper = new LambdaQueryWrapper<>();
        if (stationId != null) {
            wrapper.eq(ClimateCompensationEffect::getStationId, stationId);
        }
        if (dataType != null) {
            wrapper.eq(ClimateCompensationEffect::getDataType, dataType);
        }
        wrapper.orderByDesc(ClimateCompensationEffect::getRecordDate);
        if (days != null && days > 0) {
            wrapper.last("LIMIT " + days);
        }
        return effectMapper.selectList(wrapper);
    }

    /**
     * 计算节能率
     * 根据最近30天的数据计算平均节能率
     * @param stationId 热力站ID
     * @return 节能率百分比
     */
    public BigDecimal calculateSavingsRate(Long stationId) {
        LambdaQueryWrapper<ClimateCompensationEffect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClimateCompensationEffect::getStationId, stationId)
               .orderByDesc(ClimateCompensationEffect::getRecordDate)
               .last("LIMIT 30");
        List<ClimateCompensationEffect> effects = effectMapper.selectList(wrapper);
        
        if (effects.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal totalBefore = BigDecimal.ZERO;
        BigDecimal totalAfter = BigDecimal.ZERO;
        
        for (ClimateCompensationEffect effect : effects) {
            if (effect.getEnergyBefore() != null) {
                totalBefore = totalBefore.add(effect.getEnergyBefore());
            }
            if (effect.getEnergyAfter() != null) {
                totalAfter = totalAfter.add(effect.getEnergyAfter());
            }
        }
        
        if (totalBefore.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        return totalBefore.subtract(totalAfter)
                .multiply(new BigDecimal("100"))
                .divide(totalBefore, 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 获取当前室外温度
     * @param stationId 热力站ID
     * @return 配置对象（包含室外温度）
     */
    public ClimateCompensationConfig getCurrentOutdoorTemp(Long stationId) {
        LambdaQueryWrapper<ClimateCompensationEffect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClimateCompensationEffect::getStationId, stationId)
               .orderByDesc(ClimateCompensationEffect::getRecordDate)
               .last("LIMIT 1");
        ClimateCompensationEffect latest = effectMapper.selectOne(wrapper);
        
        if (latest != null && latest.getOutdoorTemp() != null) {
            ClimateCompensationConfig config = new ClimateCompensationConfig();
            config.setOutdoorTempMin(latest.getOutdoorTemp());
            return config;
        }
        return null;
    }

    /**
     * 获取气候补偿统计信息
     * @param stationId 热力站ID
     * @return 统计信息Map
     */
    public Map<String, Object> getStatistics(Long stationId) {
        ClimateCompensationConfig activeConfig = getActiveConfig(stationId);
        ClimateCompensationEffect latestEffect = null;
        
        LambdaQueryWrapper<ClimateCompensationEffect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClimateCompensationEffect::getStationId, stationId)
               .orderByDesc(ClimateCompensationEffect::getRecordDate)
               .last("LIMIT 1");
        latestEffect = effectMapper.selectOne(wrapper);

        BigDecimal savingsRate = calculateSavingsRate(stationId);

        Map<String, Object> result = new HashMap<>();
        result.put("currentMode", activeConfig != null ? activeConfig.getConfigName() : "未配置");
        result.put("outdoorTemp", latestEffect != null ? latestEffect.getOutdoorTemp() : new BigDecimal("-5.0"));
        result.put("supplyTemp", latestEffect != null ? latestEffect.getActualSupplyTemp() : new BigDecimal("52.0"));
        result.put("savingsRate", savingsRate);
        
        return result;
    }
}
