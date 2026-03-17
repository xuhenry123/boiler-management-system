package com.boiler.controller;

import com.boiler.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 成本分析控制器
 * 处理成本分析相关的HTTP请求，提供RESTful API接口
 */
@RestController
@RequestMapping("/api/cost")
@RequiredArgsConstructor
@CrossOrigin
public class CostController {

    private final CostAnalysisService costAnalysisService;

    /**
     * 获取成本概览
     */
    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getOverview(
            @RequestParam(required = false) Long stationId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalCost", 1250000);
        result.put("electricityCost", 450000);
        result.put("gasCost", 650000);
        result.put("waterCost", 50000);
        result.put("maintenanceCost", 100000);
        result.put("period", startDate + " 至 " + endDate);
        
        return ResponseEntity.ok(result);
    }

    /**
     * 获取成本排名
     */
    @GetMapping("/ranking")
    public ResponseEntity<Map<String, Object>> getRanking(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        
        Map<String, Object> result = new HashMap<>();
        result.put("data", java.util.List.of(
            java.util.Map.of("rank", 1, "stationName", "换热站A", "cost", 280000, "unitCost", 45.5),
            java.util.Map.of("rank", 2, "stationName", "换热站B", "cost", 250000, "unitCost", 48.2),
            java.util.Map.of("rank", 3, "stationName", "换热站C", "cost", 220000, "unitCost", 52.1),
            java.util.Map.of("rank", 4, "stationName", "换热站D", "cost", 200000, "unitCost", 55.0),
            java.util.Map.of("rank", 5, "stationName", "换热站E", "cost", 180000, "unitCost", 58.3)
        ));
        
        return ResponseEntity.ok(result);
    }

    /**
     * 获取成本趋势
     */
    @GetMapping("/trend")
    public ResponseEntity<Map<String, Object>> getTrend(
            @RequestParam(required = false) Long stationId,
            @RequestParam(required = false, defaultValue = "month") String period) {
        
        Map<String, Object> result = new HashMap<>();
        result.put("dates", java.util.List.of("1月", "2月", "3月", "4月", "5月", "6月"));
        result.put("electricity", java.util.List.of(42000, 45000, 48000, 52000, 55000, 58000));
        result.put("gas", java.util.List.of(58000, 62000, 65000, 70000, 72000, 75000));
        result.put("water", java.util.List.of(4500, 4800, 5200, 5500, 5800, 6000));
        
        return ResponseEntity.ok(result);
    }

    /**
     * 获取碳排放统计
     */
    @GetMapping("/carbon")
    public ResponseEntity<Map<String, Object>> getCarbon(
            @RequestParam(required = false) Long stationId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalCarbon", 1250);
        result.put("electricityCarbon", 450);
        result.put("gasCarbon", 800);
        result.put("carbonIntensity", 0.85);
        
        return ResponseEntity.ok(result);
    }
}
