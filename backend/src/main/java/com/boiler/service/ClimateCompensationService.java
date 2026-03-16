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
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClimateCompensationService {

    private final ClimateCompensationConfigMapper configMapper;
    private final ClimateCompensationCurveMapper curveMapper;
    private final ClimateCompensationEffectMapper effectMapper;

    public Page<ClimateCompensationConfig> listConfigs(Integer page, Integer size, Long stationId) {
        Page<ClimateCompensationConfig> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<ClimateCompensationConfig> wrapper = new LambdaQueryWrapper<>();
        if (stationId != null) {
            wrapper.eq(ClimateCompensationConfig::getStationId, stationId);
        }
        wrapper.orderByDesc(ClimateCompensationConfig::getId);
        return configMapper.selectPage(pageParam, wrapper);
    }

    public ClimateCompensationConfig getConfigById(Long id) {
        return configMapper.selectById(id);
    }

    public ClimateCompensationConfig getActiveConfig(Long stationId) {
        LambdaQueryWrapper<ClimateCompensationConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClimateCompensationConfig::getStationId, stationId)
               .eq(ClimateCompensationConfig::getIsActive, 1);
        return configMapper.selectOne(wrapper);
    }

    @Transactional
    public boolean saveConfig(ClimateCompensationConfig config) {
        if (config.getIsActive() != null && config.getIsActive() == 1) {
            clearActiveConfigs(config.getStationId());
        }
        return configMapper.insert(config) > 0;
    }

    @Transactional
    public boolean updateConfig(ClimateCompensationConfig config) {
        if (config.getIsActive() != null && config.getIsActive() == 1) {
            clearActiveConfigs(config.getStationId());
        }
        return configMapper.updateById(config) > 0;
    }

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

    public boolean deleteConfig(Long id) {
        return configMapper.deleteById(id) > 0;
    }

    public List<ClimateCompensationCurve> getCurvesByConfigId(Long configId) {
        LambdaQueryWrapper<ClimateCompensationCurve> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClimateCompensationCurve::getConfigId, configId)
               .orderByAsc(ClimateCompensationCurve::getSortOrder);
        return curveMapper.selectList(wrapper);
    }

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

    public Object getStatistics(Long stationId) {
        ClimateCompensationConfig activeConfig = getActiveConfig(stationId);
        ClimateCompensationEffect latestEffect = null;
        
        LambdaQueryWrapper<ClimateCompensationEffect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClimateCompensationEffect::getStationId, stationId)
               .orderByDesc(ClimateCompensationEffect::getRecordDate)
               .last("LIMIT 1");
        latestEffect = effectMapper.selectOne(wrapper);

        BigDecimal savingsRate = calculateSavingsRate(stationId);

        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("currentMode", activeConfig != null ? activeConfig.getConfigName() : "未配置");
        result.put("outdoorTemp", latestEffect != null ? latestEffect.getOutdoorTemp() : new BigDecimal("-5.0"));
        result.put("supplyTemp", latestEffect != null ? latestEffect.getActualSupplyTemp() : new BigDecimal("52.0"));
        result.put("savingsRate", savingsRate);
        
        return result;
    }
}
