package com.boiler.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boiler.entity.HeatingCurveConfig;
import com.boiler.service.HeatingCurveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 供热曲线控制器
 * 处理供热曲线相关的HTTP请求，提供RESTful API接口
 */
@RestController
@RequestMapping("/api/heating-curve")
@RequiredArgsConstructor
@CrossOrigin
public class HeatingCurveController {

    private final HeatingCurveService heatingCurveService;

    /**
     * 分页获取供热曲线列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listHeatingCurves(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long stationId) {
        
        Page<HeatingCurveConfig> pageResult = heatingCurveService.listHeatingCurves(page, size, stationId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("data", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        result.put("pages", pageResult.getPages());
        
        return ResponseEntity.ok(result);
    }

    /**
     * 根据ID获取供热曲线
     */
    @GetMapping("/{id}")
    public ResponseEntity<HeatingCurveConfig> getHeatingCurveById(@PathVariable Long id) {
        HeatingCurveConfig config = heatingCurveService.getHeatingCurveById(id);
        return ResponseEntity.ok(config);
    }

    /**
     * 获取热力站的激活曲线
     */
    @GetMapping("/active")
    public ResponseEntity<HeatingCurveConfig> getActiveCurve(@RequestParam Long stationId) {
        HeatingCurveConfig config = heatingCurveService.getActiveCurve(stationId);
        return ResponseEntity.ok(config);
    }

    /**
     * 保存供热曲线配置
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> saveHeatingCurve(@RequestBody HeatingCurveConfig config) {
        boolean success = heatingCurveService.saveHeatingCurve(config);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        if (success) {
            result.put("id", config.getId());
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 更新供热曲线配置
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateHeatingCurve(
            @PathVariable Long id, 
            @RequestBody HeatingCurveConfig config) {
        config.setId(id);
        boolean success = heatingCurveService.updateHeatingCurve(config);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除供热曲线配置
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteHeatingCurve(@PathVariable Long id) {
        boolean success = heatingCurveService.deleteHeatingCurve(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return ResponseEntity.ok(result);
    }

    /**
     * 应用供热曲线
     */
    @PostMapping("/apply")
    public ResponseEntity<Map<String, Object>> applyHeatingCurve(
            @RequestParam Long stationId,
            @RequestBody HeatingCurveConfig config) {
        config.setStationId(stationId);
        config.setIsActive(1);
        boolean success = heatingCurveService.saveHeatingCurve(config);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return ResponseEntity.ok(result);
    }
}
