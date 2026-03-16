package com.boiler.controller.anomaly;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boiler.entity.anomaly.AlertConfig;
import com.boiler.entity.anomaly.AlertRecord;
import com.boiler.service.anomaly.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alert")
@RequiredArgsConstructor
@CrossOrigin
public class AlertController {

    private final AlertService alertService;

    @GetMapping("/config/list")
    public ResponseEntity<Map<String, Object>> listAlertConfigs(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String deviceType,
            @RequestParam(required = false) String metricType) {
        
        Page<AlertConfig> pageResult = alertService.listAlertConfigs(page, size, deviceType, metricType);
        
        Map<String, Object> result = new HashMap<>();
        result.put("data", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        result.put("pages", pageResult.getPages());
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/config/{id}")
    public ResponseEntity<AlertConfig> getAlertConfigById(@PathVariable Long id) {
        AlertConfig config = alertService.getAlertConfigById(id);
        return ResponseEntity.ok(config);
    }

    @PostMapping("/config")
    public ResponseEntity<Map<String, Object>> createAlertConfig(@RequestBody AlertConfig config) {
        boolean success = alertService.saveAlertConfig(config);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/config/{id}")
    public ResponseEntity<Map<String, Object>> updateAlertConfig(
            @PathVariable Long id,
            @RequestBody AlertConfig config) {
        config.setId(id);
        boolean success = alertService.updateAlertConfig(config);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/config/{id}")
    public ResponseEntity<Map<String, Object>> deleteAlertConfig(@PathVariable Long id) {
        boolean success = alertService.deleteAlertConfig(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/config/enabled")
    public ResponseEntity<List<AlertConfig>> getEnabledConfigs() {
        List<AlertConfig> configs = alertService.getEnabledConfigs();
        return ResponseEntity.ok(configs);
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> listAlertRecords(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String alertLevel,
            @RequestParam(required = false) Boolean acknowledged) {
        
        Page<AlertRecord> pageResult = alertService.listAlertRecords(page, size, alertLevel, acknowledged);
        
        Map<String, Object> result = new HashMap<>();
        result.put("data", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        result.put("pages", pageResult.getPages());
        
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createAlert(@RequestBody AlertRecord alert) {
        Long id = alertService.createAlert(alert);
        Map<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("success", true);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/acknowledge")
    public ResponseEntity<Map<String, Object>> acknowledgeAlert(
            @PathVariable Long id,
            @RequestBody Map<String, String> params) {
        String acknowledgedBy = params.get("acknowledgedBy");
        boolean success = alertService.acknowledgeAlert(id, acknowledgedBy);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/unacknowledged/count")
    public ResponseEntity<Map<String, Object>> countUnacknowledged() {
        Long count = alertService.countUnacknowledged();
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        return ResponseEntity.ok(result);
    }
}
