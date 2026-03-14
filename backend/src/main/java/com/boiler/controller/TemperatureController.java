package com.boiler.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boiler.entity.IndoorTemperature;
import com.boiler.service.TemperatureService;
import com.boiler.service.TemperaturePredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/heat-user/temperature")
@RequiredArgsConstructor
@CrossOrigin
public class TemperatureController {

    private final TemperatureService temperatureService;
    private final TemperaturePredictionService predictionService;

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getTemperatureData(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        
        Page<IndoorTemperature> pageResult = temperatureService.listTemperatures(
            userId, startTime, endTime, page, size);
        
        Map<String, Object> result = new HashMap<>();
        result.put("data", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        result.put("pages", pageResult.getPages());
        result.put("current", pageResult.getCurrent());
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{userId}/latest")
    public ResponseEntity<Map<String, Object>> getLatestTemperature(@PathVariable Long userId) {
        IndoorTemperature temp = temperatureService.getLatestTemperature(userId);
        Map<String, Object> result = new HashMap<>();
        if (temp != null) {
            result.put("temperature", temp.getTemperature());
            result.put("collectTime", temp.getCollectTime());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{userId}/predict")
    public ResponseEntity<Map<String, Object>> predict(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "2") Integer hours) {
        Map<String, Object> result = predictionService.predict(userId, hours);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> batchSaveTemperature(@RequestBody List<IndoorTemperature> temperatures) {
        boolean success = temperatureService.batchSaveTemperature(temperatures);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("count", temperatures.size());
        return ResponseEntity.ok(result);
    }
}
