package com.boiler.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boiler.entity.ClimateCompensationConfig;
import com.boiler.entity.ClimateCompensationCurve;
import com.boiler.entity.ClimateCompensationEffect;
import com.boiler.service.ClimateCompensationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/climate")
@RequiredArgsConstructor
@CrossOrigin
public class ClimateCompensationController {

    private final ClimateCompensationService climateCompensationService;

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

    @GetMapping("/config/{id}")
    public ResponseEntity<ClimateCompensationConfig> getConfigById(@PathVariable Long id) {
        ClimateCompensationConfig config = climateCompensationService.getConfigById(id);
        return ResponseEntity.ok(config);
    }

    @GetMapping("/config/active")
    public ResponseEntity<ClimateCompensationConfig> getActiveConfig(@RequestParam(required = false) Long stationId) {
        ClimateCompensationConfig config = climateCompensationService.getActiveConfig(stationId != null ? stationId : 1L);
        return ResponseEntity.ok(config);
    }

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

    @DeleteMapping("/config/{id}")
    public ResponseEntity<Map<String, Object>> deleteConfig(@PathVariable Long id) {
        boolean success = climateCompensationService.deleteConfig(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return ResponseEntity.ok(result);
    }

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

    private List<ClimateCompensationCurve> getDefaultCurves() {
        return List.of(
            createCurve(-20L, new java.math.BigDecimal("-20"), new java.math.BigDecimal("75"), new java.math.BigDecimal("55")),
            createCurve(-15L, new java.math.BigDecimal("-15"), new java.math.BigDecimal("68"), new java.math.BigDecimal("50")),
            createCurve(-10L, new java.math.BigDecimal("-10"), new java.math.BigDecimal("60"), new java.math.BigDecimal("44")),
            createCurve(-5L, new java.math.BigDecimal("-5"), new java.math.BigDecimal("52"), new java.math.BigDecimal("38")),
            createCurve(0L, new java.math.BigDecimal("0"), new java.math.BigDecimal("45"), new java.math.BigDecimal("33")),
            createCurve(5L, new java.math.BigDecimal("5"), new java.math.BigDecimal("40"), new java.math.BigDecimal("30")),
            createCurve(10L, new java.math.BigDecimal("10"), new java.math.BigDecimal("35"), new java.math.BigDecimal("28"))
        );
    }

    private ClimateCompensationCurve createCurve(Long id, java.math.BigDecimal outdoorTemp, 
            java.math.BigDecimal supplyTemp, java.math.BigDecimal returnTemp) {
        ClimateCompensationCurve curve = new ClimateCompensationCurve();
        curve.setId(id);
        curve.setOutdoorTemp(outdoorTemp);
        curve.setSupplyTemp(supplyTemp);
        curve.setReturnTemp(returnTemp);
        return curve;
    }

    @PostMapping("/curve")
    public ResponseEntity<Map<String, Object>> saveCurves(
            @RequestParam Long configId,
            @RequestBody List<ClimateCompensationCurve> curves) {
        boolean success = climateCompensationService.saveCurves(configId, curves);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return ResponseEntity.ok(result);
    }

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

    private List<ClimateCompensationEffect> getDefaultEffects() {
        return List.of(
            createEffect(1L, "周一", new java.math.BigDecimal("-8"), new java.math.BigDecimal("52"), new java.math.BigDecimal("45"), new java.math.BigDecimal("52"), new java.math.BigDecimal("13.5")),
            createEffect(2L, "周二", new java.math.BigDecimal("-10"), new java.math.BigDecimal("55"), new java.math.BigDecimal("42"), new java.math.BigDecimal("48"), new java.math.BigDecimal("12.5")),
            createEffect(3L, "周三", new java.math.BigDecimal("-6"), new java.math.BigDecimal("50"), new java.math.BigDecimal("48"), new java.math.BigDecimal("55"), new java.math.BigDecimal("12.7")),
            createEffect(4L, "周四", new java.math.BigDecimal("-5"), new java.math.BigDecimal("48"), new java.math.BigDecimal("44"), new java.math.BigDecimal("50"), new java.math.BigDecimal("12.0")),
            createEffect(5L, "周五", new java.math.BigDecimal("-3"), new java.math.BigDecimal("45"), new java.math.BigDecimal("40"), new java.math.BigDecimal("45"), new java.math.BigDecimal("11.1")),
            createEffect(6L, "周六", new java.math.BigDecimal("-2"), new java.math.BigDecimal("42"), new java.math.BigDecimal("37"), new java.math.BigDecimal("42"), new java.math.BigDecimal("11.9")),
            createEffect(7L, "周日", new java.math.BigDecimal("-4"), new java.math.BigDecimal("44"), new java.math.BigDecimal("40"), new java.math.BigDecimal("46"), new java.math.BigDecimal("13.0"))
        );
    }

    private ClimateCompensationEffect createEffect(Long id, String day, java.math.BigDecimal outdoorTemp,
            java.math.BigDecimal targetTemp, java.math.BigDecimal actualTemp,
            java.math.BigDecimal before, java.math.BigDecimal after) {
        ClimateCompensationEffect effect = new ClimateCompensationEffect();
        effect.setId(id);
        effect.setOutdoorTemp(outdoorTemp);
        effect.setTargetSupplyTemp(targetTemp);
        effect.setActualSupplyTemp(actualTemp);
        effect.setEnergyBefore(before);
        effect.setEnergyAfter(after);
        effect.setSavingsRate(after.multiply(new java.math.BigDecimal("100")).divide(before, 2, java.math.BigDecimal.ROUND_HALF_UP));
        return effect;
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics(@RequestParam(required = false) Long stationId) {
        Object stats = climateCompensationService.getStatistics(stationId != null ? stationId : 1L);
        return ResponseEntity.ok((Map<String, Object>) stats);
    }
}
