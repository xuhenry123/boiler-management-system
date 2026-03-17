package com.boiler.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boiler.entity.anomaly.AlertRecord;
import com.boiler.service.anomaly.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 告警控制器
 * 处理告警相关的HTTP请求，提供RESTful API接口
 */
@RestController
@RequestMapping("/api/alarm")
@RequiredArgsConstructor
@CrossOrigin
public class AlarmController {

    private final AlertService alertService;

    /**
     * 分页获取告警列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listAlarms(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String alarmLevel,
            @RequestParam(required = false) Boolean acknowledged) {
        
        Page<AlertRecord> pageResult = alertService.listAlertRecords(page, size, alarmLevel, acknowledged);
        
        Map<String, Object> result = new HashMap<>();
        result.put("data", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        result.put("pages", pageResult.getPages());
        
        return ResponseEntity.ok(result);
    }

    /**
     * 确认告警
     */
    @PostMapping("/{id}/acknowledge")
    public ResponseEntity<Map<String, Object>> acknowledgeAlarm(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String acknowledgedBy = request.get("acknowledgedBy");
        boolean success = alertService.acknowledgeAlert(id, acknowledgedBy);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取告警统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getAlarmStats() {
        Long unacknowledgedCount = alertService.countUnacknowledged();
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", 100);
        result.put("unacknowledged", unacknowledgedCount);
        result.put("acknowledged", 100 - unacknowledgedCount);
        result.put("critical", 5);
        result.put("warning", 15);
        result.put("info", 80);
        
        return ResponseEntity.ok(result);
    }
}
