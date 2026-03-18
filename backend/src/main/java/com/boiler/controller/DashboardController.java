package com.boiler.controller;

import com.boiler.entity.HeatStation;
import com.boiler.entity.Building;
import com.boiler.entity.HeatUser;
import com.boiler.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin
public class DashboardController {

    private final HeatStationService heatStationService;
    private final BuildingService buildingService;
    private final HeatUserService heatUserService;
    private final AlarmService alarmService;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        List<HeatStation> stations = heatStationService.listAllStations();
        stats.put("stationCount", stations.size());
        stats.put("stationRunningCount", stations.stream().filter(s -> s.getStatus() == 1).count());
        
        List<Building> buildings = buildingService.listAllBuildings();
        stats.put("buildingCount", buildings.size());
        
        List<HeatUser> users = heatUserService.listAllUsers();
        stats.put("userCount", users.size());
        
        int alarmCount = alarmService.getUnacknowledgedAlarmCount();
        stats.put("alarmCount", alarmCount);
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/temperature-trend")
    public ResponseEntity<Map<String, Object>> getTemperatureTrend() {
        Map<String, Object> result = new HashMap<>();
        
        List<String> hours = Arrays.asList("00:00", "04:00", "08:00", "12:00", "16:00", "20:00");
        List<Integer> primarySupply = Arrays.asList(118, 120, 122, 121, 119, 120);
        List<Integer> primaryReturn = Arrays.asList(68, 70, 72, 71, 69, 70);
        List<Integer> secondarySupply = Arrays.asList(48, 50, 51, 50, 49, 50);
        List<Integer> secondaryReturn = Arrays.asList(38, 40, 41, 40, 39, 40);
        
        result.put("hours", hours);
        result.put("primarySupply", primarySupply);
        result.put("primaryReturn", primaryReturn);
        result.put("secondarySupply", secondarySupply);
        result.put("secondaryReturn", secondaryReturn);
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/station-status")
    public ResponseEntity<List<Map<String, Object>>> getStationStatus() {
        List<HeatStation> stations = heatStationService.listAllStations();
        
        List<Map<String, Object>> result = new ArrayList<>();
        
        long runningCount = stations.stream().filter(s -> s.getStatus() == 1).count();
        long stoppedCount = stations.stream().filter(s -> s.getStatus() == 0).count();
        long faultCount = stations.stream().filter(s -> s.getStatus() == 3).count();
        
        if (runningCount > 0) {
            Map<String, Object> running = new HashMap<>();
            running.put("value", runningCount);
            running.put("name", "运行中");
            running.put("itemStyle", Map.of("color", "#67c23a"));
            result.add(running);
        }
        if (stoppedCount > 0) {
            Map<String, Object> stopped = new HashMap<>();
            stopped.put("value", stoppedCount);
            stopped.put("name", "停止");
            stopped.put("itemStyle", Map.of("color", "#909399"));
            result.add(stopped);
        }
        if (faultCount > 0) {
            Map<String, Object> fault = new HashMap<>();
            fault.put("value", faultCount);
            fault.put("name", "故障");
            fault.put("itemStyle", Map.of("color", "#f56c6c"));
            result.add(fault);
        }
        
        if (result.isEmpty()) {
            Map<String, Object> defaultData = new HashMap<>();
            defaultData.put("value", 1);
            defaultData.put("name", "运行中");
            defaultData.put("itemStyle", Map.of("color", "#67c23a"));
            result.add(defaultData);
        }
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/heat-load")
    public ResponseEntity<List<Integer>> getHeatLoad() {
        List<Integer> loadData = Arrays.asList(45, 42, 55, 60, 58, 52);
        return ResponseEntity.ok(loadData);
    }

    @GetMapping("/alarms")
    public ResponseEntity<List<Map<String, Object>>> getAlarms() {
        List<Map<String, Object>> alarms = alarmService.getRecentAlarms(10);
        return ResponseEntity.ok(alarms);
    }
}
