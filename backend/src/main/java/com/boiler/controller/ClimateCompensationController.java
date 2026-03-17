package com.boiler.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boiler.entity.ClimateCompensationConfig;
import com.boiler.entity.ClimateCompensationCurve;
import com.boiler.entity.ClimateCompensationEffect;
import com.boiler.service.ClimateCompensationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 气候补偿控制器
 * 处理气候补偿相关的HTTP请求，提供RESTful API接口
 */
@RestController
@RequestMapping("/api/climate")
@RequiredArgsConstructor
@CrossOrigin
public class ClimateCompensationController {

    private final ClimateCompensationService climateCompensationService;

    /**
     * 分页获取气候补偿配置列表
     * @param page 页码，默认1
     * @param size 每页数量，默认10
     * @param stationId 热力站ID
     * @return 分页结果
     */
    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> listConfigs(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long stationId) {
        
        Page<ClimateCompensationConfig> pageResult = climateCompensationService.listConfigs(page, size, stationId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("data", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        result.put("pages", pageResult.getPages());
        
        return ResponseEntity.ok(result);
    }

    /**
     * 根据ID获取气候补偿配置
     * @param id 配置ID
     * @return 配置信息
     */
    @GetMapping("/config/{id}")
    public ResponseEntity<ClimateCompensationConfig> getConfigById(@PathVariable Long id) {
        ClimateCompensationConfig config = climateCompensationService.getConfigById(id);
        return ResponseEntity.ok(config);
    }

    /**
     * 获取当前激活的气候补偿配置
     * @param stationId 热力站ID
     * @return 激活的配置信息
     */
    @GetMapping("/config/active")
    public ResponseEntity<ClimateCompensationConfig> getActiveConfig(@RequestParam(required = false) Long stationId) {
        ClimateCompensationConfig config = climateCompensationService.getActiveConfig(stationId != null ? stationId : 1L);
        return ResponseEntity.ok(config);
    }

    /**
     * 保存气候补偿配置
     * @param config 配置信息
     * @return 操作结果
     */
    @PostMapping("/config")
    public ResponseEntity<Map<String, Object>> saveConfig(@RequestBody ClimateCompensationConfig config) {
        boolean success = climateCompensationService.saveConfig(config);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        if (success) {
            result.put("id", config.getId());
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 更新气候补偿配置
     * @param id 配置ID
     * @param config 配置信息
     * @return 操作结果
     */
    @PutMapping("/config/{id}")
    public ResponseEntity<Map<String, Object>> updateConfig(
            @PathVariable Long id, 
            @RequestBody ClimateCompensationConfig config) {
        config.setId(id);
        boolean success = climateCompensationService.updateConfig(config);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除气候补偿配置
     * @param id 配置ID
     * @return 操作结果
     */
    @DeleteMapping("/config/{id}")
    public ResponseEntity<Map<String, Object>> deleteConfig(@PathVariable Long id) {
        boolean success = climateCompensationService.deleteConfig(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取补偿曲线
     * 根据配置ID或热力站ID获取对应的补偿曲线
     * @param configId 配置ID
     * @param stationId 热力站ID
     * @return 曲线数据
     */
    @GetMapping("/curve")
    public ResponseEntity<Map<String, Object>> getCurves(
            @RequestParam(required = false) Long configId,
            @RequestParam(required = false) Long stationId) {
        
        ClimateCompensationConfig config = null;
        if (configId != null) {
            config = climateCompensationService.getConfigById(configId);
        } else if (stationId != null) {
            config = climateCompensationService.getActiveConfig(stationId);
        } else {
            config = climateCompensationService.getActiveConfig(1L);
        }
        
        List<ClimateCompensationCurve> curves = null;
        if (config != null) {
            curves = climateCompensationService.getCurvesByConfigId(config.getId());
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("config", config);
        result.put("curves", curves != null ? curves : getDefaultCurves());
        
        return ResponseEntity.ok(result);
    }

    /**
     * 获取默认补偿曲线
     * 当没有配置曲线时返回默认曲线
     * @return 默认曲线列表
     */
    private List<ClimateCompensationCurve> getDefaultCurves() {
        return List.of(
            createCurve(-20L, new BigDecimal("-20"), new BigDecimal("75"), new BigDecimal("55")),
            createCurve(-15L, new BigDecimal("-15"), new BigDecimal("68"), new BigDecimal("50")),
            createCurve(-10L, new BigDecimal("-10"), new BigDecimal("60"), new BigDecimal("44")),
            createCurve(-5L, new BigDecimal("-5"), new BigDecimal("52"), new BigDecimal("38")),
            createCurve(0L, new BigDecimal("0"), new BigDecimal("45"), new BigDecimal("33")),
            createCurve(5L, new BigDecimal("5"), new BigDecimal("40"), new BigDecimal("30")),
            createCurve(10L, new BigDecimal("10"), new BigDecimal("35"), new BigDecimal("28"))
        );
    }

    /**
     * 创建曲线对象
     * @param id 曲线ID
     * @param outdoorTemp 室外温度
     * @param supplyTemp 供水温度
     * @param returnTemp 回水温度
     * @return 曲线对象
     */
    private ClimateCompensationCurve createCurve(Long id, BigDecimal outdoorTemp, 
            BigDecimal supplyTemp, BigDecimal returnTemp) {
        ClimateCompensationCurve curve = new ClimateCompensationCurve();
        curve.setId(id);
        curve.setOutdoorTemp(outdoorTemp);
        curve.setSupplyTemp(supplyTemp);
        curve.setReturnTemp(returnTemp);
        return curve;
    }

    /**
     * 保存补偿曲线
     * @param configId 配置ID
     * @param curves 曲线列表
     * @return 操作结果
     */
    @PostMapping("/curve")
    public ResponseEntity<Map<String, Object>> saveCurves(
            @RequestParam Long configId,
            @RequestBody List<ClimateCompensationCurve> curves) {
        boolean success = climateCompensationService.saveCurves(configId, curves);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取补偿效果数据
     * @param stationId 热力站ID
     * @param dataType 数据类型
     * @param days 查询天数
     * @return 效果数据列表
     */
    @GetMapping("/effect")
    public ResponseEntity<Map<String, Object>> getEffects(
            @RequestParam(required = false) Long stationId,
            @RequestParam(required = false) String dataType,
            @RequestParam(required = false, defaultValue = "7") Integer days) {
        
        List<ClimateCompensationEffect> effects = climateCompensationService.listEffects(
                stationId != null ? stationId : 1L, dataType, days);
        
        Map<String, Object> result = new HashMap<>();
        result.put("data", effects.isEmpty() ? getDefaultEffects() : effects);
        
        return ResponseEntity.ok(result);
    }

    /**
     * 获取默认效果数据
     * @return 默认效果列表
     */
    private List<ClimateCompensationEffect> getDefaultEffects() {
        return List.of(
            createEffect(1L, "周一", new BigDecimal("-8"), new BigDecimal("52"), new BigDecimal("45"), new BigDecimal("52"), new BigDecimal("13.5")),
            createEffect(2L, "周二", new BigDecimal("-10"), new BigDecimal("55"), new BigDecimal("42"), new BigDecimal("48"), new BigDecimal("12.5")),
            createEffect(3L, "周三", new BigDecimal("-6"), new BigDecimal("50"), new BigDecimal("48"), new BigDecimal("55"), new BigDecimal("12.7")),
            createEffect(4L, "周四", new BigDecimal("-5"), new BigDecimal("48"), new BigDecimal("44"), new BigDecimal("50"), new BigDecimal("12.0")),
            createEffect(5L, "周五", new BigDecimal("-3"), new BigDecimal("45"), new BigDecimal("40"), new BigDecimal("45"), new BigDecimal("11.1")),
            createEffect(6L, "周六", new BigDecimal("-2"), new BigDecimal("42"), new BigDecimal("37"), new BigDecimal("42"), new BigDecimal("11.9")),
            createEffect(7L, "周日", new BigDecimal("-4"), new BigDecimal("44"), new BigDecimal("40"), new BigDecimal("46"), new BigDecimal("13.0"))
        );
    }

    /**
     * 创建效果对象
     * @param id 效果ID
     * @param day 日期
     * @param outdoorTemp 室外温度
     * @param targetTemp 目标温度
     * @param actualTemp 实际温度
     * @param before 补偿前能耗
     * @param after 补偿后能耗
     * @return 效果对象
     */
    private ClimateCompensationEffect createEffect(Long id, String day, BigDecimal outdoorTemp,
            BigDecimal targetTemp, BigDecimal actualTemp,
            BigDecimal before, BigDecimal after) {
        ClimateCompensationEffect effect = new ClimateCompensationEffect();
        effect.setId(id);
        effect.setOutdoorTemp(outdoorTemp);
        effect.setTargetSupplyTemp(targetTemp);
        effect.setActualSupplyTemp(actualTemp);
        effect.setEnergyBefore(before);
        effect.setEnergyAfter(after);
        effect.setSavingsRate(after.multiply(new BigDecimal("100")).divide(before, 2, BigDecimal.ROUND_HALF_UP));
        return effect;
    }

    /**
     * 获取气候补偿统计信息
     * @param stationId 热力站ID
     * @return 统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics(@RequestParam(required = false) Long stationId) {
        Map<String, Object> stats = climateCompensationService.getStatistics(stationId != null ? stationId : 1L);
        return ResponseEntity.ok(stats);
    }
}
