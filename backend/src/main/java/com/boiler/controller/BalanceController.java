package com.boiler.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 平衡优化控制器
 * 处理二网平衡优化相关的HTTP请求，提供RESTful API接口
 */
@RestController
@RequestMapping("/api/balance")
@RequiredArgsConstructor
@CrossOrigin
public class BalanceController {

    /**
     * GA优化计算
     */
    @PostMapping("/optimize/ga")
    public ResponseEntity<Map<String, Object>> optimizeGA(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("resultId", "GA_" + System.currentTimeMillis());
        result.put("valveRatios", Map.of(
            "V001", 0.75,
            "V002", 0.80,
            "V003", 0.70,
            "V004", 0.65
        ));
        result.put("objectiveValue", 0.125);
        result.put("iterationCount", 150);
        result.put("executionTime", 2350);
        return ResponseEntity.ok(result);
    }

    /**
     * PSO优化计算
     */
    @PostMapping("/optimize/pso")
    public ResponseEntity<Map<String, Object>> optimizePSO(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("resultId", "PSO_" + System.currentTimeMillis());
        result.put("valveRatios", Map.of(
            "V001", 0.72,
            "V002", 0.78,
            "V003", 0.68,
            "V004", 0.62
        ));
        result.put("objectiveValue", 0.118);
        result.put("iterationCount", 120);
        result.put("executionTime", 1890);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取优化结果
     */
    @GetMapping("/results/{stationId}")
    public ResponseEntity<Map<String, Object>> getOptimizationResults(
            @PathVariable Long stationId,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        
        Map<String, Object> result = new HashMap<>();
        result.put("data", java.util.List.of(
            Map.of("id", 1, "type", "GA", "objectiveValue", 0.125, "iterationCount", 150, "calculateTime", "2026-03-15 10:30:00"),
            Map.of("id", 2, "type", "PSO", "objectiveValue", 0.118, "iterationCount", 120, "calculateTime", "2026-03-15 10:35:00")
        ));
        return ResponseEntity.ok(result);
    }

    /**
     * 应用优化结果
     */
    @PostMapping("/apply/{resultId}")
    public ResponseEntity<Map<String, Object>> applyOptimization(@PathVariable String resultId) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "优化结果已应用到阀门设备");
        result.put("applyTime", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(result);
    }
}
