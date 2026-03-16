package com.boiler.controller.anomaly;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boiler.entity.anomaly.AnomalyRecord;
import com.boiler.service.anomaly.AnomalyDetectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/anomaly")
@RequiredArgsConstructor
@CrossOrigin
public class AnomalyDetectionController {

    private final AnomalyDetectionService anomalyDetectionService;

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> listAnomalies(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer severity,
            @RequestParam(required = false) String status) {
        
        Page<AnomalyRecord> pageResult = anomalyDetectionService.listAnomalies(page, size, type, severity, status);
        
        Map<String, Object> result = new HashMap<>();
        result.put("data", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        result.put("pages", pageResult.getPages());
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnomalyRecord> getAnomalyById(@PathVariable Long id) {
        AnomalyRecord anomaly = anomalyDetectionService.getAnomalyById(id);
        return ResponseEntity.ok(anomaly);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<AnomalyRecord>> listPendingAnomalies() {
        List<AnomalyRecord> anomalies = anomalyDetectionService.listPendingAnomalies();
        return ResponseEntity.ok(anomalies);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createAnomaly(@RequestBody AnomalyRecord anomaly) {
        Long id = anomalyDetectionService.saveAnomaly(anomaly);
        Map<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("success", true);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<Map<String, Object>> confirmAnomaly(
            @PathVariable Long id,
            @RequestBody Map<String, String> params) {
        String remark = params.get("remark");
        boolean success = anomalyDetectionService.confirmAnomaly(id, remark);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<Map<String, Object>> resolveAnomaly(@PathVariable Long id) {
        boolean success = anomalyDetectionService.resolveAnomaly(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Long pendingCount = anomalyDetectionService.countPending();
        Long highSeverityCount = anomalyDetectionService.countHighSeverity();
        
        Map<String, Object> result = new HashMap<>();
        result.put("pendingCount", pendingCount);
        result.put("highSeverityCount", highSeverityCount);
        result.put("accuracy", 92.5);
        result.put("avgLeadTime", "2.5小时");
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/device/{deviceId}")
    public ResponseEntity<List<AnomalyRecord>> listAnomaliesByDevice(@PathVariable String deviceId) {
        List<AnomalyRecord> anomalies = anomalyDetectionService.listAnomaliesByDevice(deviceId);
        return ResponseEntity.ok(anomalies);
    }
}
