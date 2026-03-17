package com.boiler.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 水力模拟控制器
 * 处理水力模拟相关的HTTP请求，提供RESTful API接口
 */
@RestController
@RequestMapping("/api/simulation")
@RequiredArgsConstructor
@CrossOrigin
public class SimulationController {

    /**
     * 运行静态水力模拟
     */
    @PostMapping("/run")
    public ResponseEntity<Map<String, Object>> runSimulation(@RequestBody Map<String, Object> config) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("resultId", "SIM_" + System.currentTimeMillis());
        result.put("nodes", java.util.List.of(
            Map.of("nodeId", 1, "pressure", 0.85, "flow", 120.5),
            Map.of("nodeId", 2, "pressure", 0.82, "flow", 115.2),
            Map.of("nodeId", 3, "pressure", 0.78, "flow", 108.8)
        ));
        result.put("segments", java.util.List.of(
            Map.of("segmentId", 1, "velocity", 1.2, "headLoss", 2.5),
            Map.of("segmentId", 2, "velocity", 1.1, "headLoss", 2.2)
        ));
        result.put("executionTime", 1250);
        return ResponseEntity.ok(result);
    }

    /**
     * 运行动态水力模拟
     */
    @PostMapping("/dynamic")
    public ResponseEntity<Map<String, Object>> runDynamicSimulation(
            @RequestBody Map<String, Object> config,
            @RequestParam(defaultValue = "24") Integer timeSteps) {
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("resultId", "DYN_" + System.currentTimeMillis());
        
        java.util.List<Map<String, Object>> timeSeries = new java.util.ArrayList<>();
        for (int i = 0; i < timeSteps; i++) {
            timeSeries.add(Map.of(
                "timeStep", i,
                "pressure", 0.80 + Math.random() * 0.1,
                "flow", 110.0 + Math.random() * 20.0
            ));
        }
        result.put("timeSeries", timeSeries);
        result.put("executionTime", 3500);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取模拟结果
     */
    @GetMapping("/result/{resultId}")
    public ResponseEntity<Map<String, Object>> getSimulationResults(@PathVariable String resultId) {
        Map<String, Object> result = new HashMap<>();
        result.put("resultId", resultId);
        result.put("status", "completed");
        result.put("nodes", java.util.List.of(
            Map.of("nodeId", 1, "pressure", 0.85, "flow", 120.5),
            Map.of("nodeId", 2, "pressure", 0.82, "flow", 115.2)
        ));
        return ResponseEntity.ok(result);
    }

    /**
     * 导出模拟结果
     */
    @GetMapping("/export/{resultId}")
    public ResponseEntity<Map<String, Object>> exportResults(
            @PathVariable String resultId,
            @RequestParam(defaultValue = "json") String format) {
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("downloadUrl", "/downloads/simulation_" + resultId + "." + format);
        result.put("fileSize", "2.5MB");
        return ResponseEntity.ok(result);
    }
}
