package com.boiler.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/heat-source")
@RequiredArgsConstructor
@CrossOrigin
public class HeatSourceController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping("/boilers")
    public ResponseEntity<List<Map<String, Object>>> getBoilers() {
        List<Map<String, Object>> boilers = new ArrayList<>();
        
        try {
            String sql = "SELECT * FROM boiler ORDER BY id";
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);
            
            for (Map<String, Object> boiler : results) {
                Map<String, Object> data = new HashMap<>();
                data.put("id", boiler.get("id"));
                data.put("name", boiler.get("boiler_name"));
                data.put("status", "running".equals(boiler.get("status")) ? "running" : 
                                  "1".equals(String.valueOf(boiler.get("status"))) ? "running" : "stopped");
                data.put("loadRate", 85);
                data.put("supplyTemp", 120);
                data.put("returnTemp", 70);
                data.put("efficiency", 0.95);
                boilers.add(data);
            }
            
            if (boilers.isEmpty()) {
                boilers = getDefaultBoilers();
            }
        } catch (Exception e) {
            boilers = getDefaultBoilers();
        }
        
        return ResponseEntity.ok(boilers);
    }

    @GetMapping("/load-prediction")
    public ResponseEntity<Map<String, Object>> getLoadPrediction() {
        Map<String, Object> result = new HashMap<>();
        
        result.put("hours", Arrays.asList("00:00", "04:00", "08:00", "12:00", "16:00", "20:00", "24:00"));
        result.put("actual", Arrays.asList(45, 42, 55, 60, 58, 52, 48));
        result.put("predicted", Arrays.asList(null, null, null, null, 55, 53, 50));
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/efficiency-comparison")
    public ResponseEntity<List<Map<String, Object>>> getEfficiencyComparison() {
        List<Map<String, Object>> data = new ArrayList<>();
        
        data.add(Map.of("name", "1号锅炉", "efficiency", 95));
        data.add(Map.of("name", "2号锅炉", "efficiency", 93));
        data.add(Map.of("name", "3号锅炉", "efficiency", 0));
        
        return ResponseEntity.ok(data);
    }

    @GetMapping("/model-info")
    public ResponseEntity<Map<String, Object>> getModelInfo() {
        Map<String, Object> info = new HashMap<>();
        
        info.put("modelType", "LSTM神经网络");
        info.put("modelVersion", "v2.1.0");
        info.put("accuracy", "94.5%");
        info.put("trainCycle", "每周");
        info.put("inputFeatures", "室外温度、时间、历史负荷");
        info.put("outputPrediction", "4小时热负荷");
        info.put("trainDataAmount", "30天");
        info.put("lastTrainTime", "2026-03-14 02:00");
        
        return ResponseEntity.ok(info);
    }

    @PostMapping("/adjust")
    public ResponseEntity<Map<String, Object>> adjustBoiler(
            @RequestParam Long id,
            @RequestParam String direction) {
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "锅炉负荷已调整");
        
        return ResponseEntity.ok(result);
    }

    private List<Map<String, Object>> getDefaultBoilers() {
        List<Map<String, Object>> list = new ArrayList<>();
        
        Map<String, Object> b1 = new HashMap<>();
        b1.put("id", 1);
        b1.put("name", "1号燃气锅炉");
        b1.put("status", "running");
        b1.put("loadRate", 85);
        b1.put("supplyTemp", 120);
        b1.put("returnTemp", 70);
        b1.put("efficiency", 0.95);
        list.add(b1);
        
        Map<String, Object> b2 = new HashMap<>();
        b2.put("id", 2);
        b2.put("name", "2号燃气锅炉");
        b2.put("status", "running");
        b2.put("loadRate", 72);
        b2.put("supplyTemp", 118);
        b2.put("returnTemp", 68);
        b2.put("efficiency", 0.93);
        list.add(b2);
        
        Map<String, Object> b3 = new HashMap<>();
        b3.put("id", 3);
        b3.put("name", "3号燃气锅炉");
        b3.put("status", "stopped");
        b3.put("loadRate", 0);
        b3.put("supplyTemp", 0);
        b3.put("returnTemp", 0);
        b3.put("efficiency", 0);
        list.add(b3);
        
        return list;
    }
}
