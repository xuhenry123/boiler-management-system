package com.boiler.controller.steam;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boiler.entity.steam.*;
import com.boiler.service.steam.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/steam")
@RequiredArgsConstructor
@CrossOrigin
public class SteamController {

    private final SteamBoilerService steamBoilerService;
    private final SteamDataPointService steamDataPointService;
    private final UserProfileService userProfileService;
    private final EnergyConsumptionService energyConsumptionService;
    private final SteamStrategyService steamStrategyService;

    @GetMapping("/devices")
    public ResponseEntity<Map<String, Object>> listDevices(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String enterpriseId,
            @RequestParam(required = false) String status) {
        Page<SteamBoiler> pageResult = steamBoilerService.listBoilers(page, size, enterpriseId, status);
        Map<String, Object> result = new HashMap<>();
        result.put("data", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        result.put("pages", pageResult.getPages());
        result.put("current", pageResult.getCurrent());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/devices/{id}")
    public ResponseEntity<SteamBoiler> getDevice(@PathVariable String id) {
        SteamBoiler boiler = steamBoilerService.getByBoilerId(id);
        return ResponseEntity.ok(boiler);
    }

    @GetMapping("/devices/{id}/realtime")
    public ResponseEntity<SteamDataPoint> getRealtimeData(@PathVariable String id) {
        SteamDataPoint dataPoint = steamDataPointService.getLatestByBoilerId(id);
        return ResponseEntity.ok(dataPoint);
    }

    @GetMapping("/devices/{id}/history")
    public ResponseEntity<List<SteamDataPoint>> getHistoryData(
            @PathVariable String id,
            @RequestParam String startTime,
            @RequestParam String endTime) {
        LocalDateTime start = LocalDateTime.parse(startTime);
        LocalDateTime end = LocalDateTime.parse(endTime);
        List<SteamDataPoint> dataPoints = steamDataPointService.listByBoilerIdAndTimeRange(id, start, end);
        return ResponseEntity.ok(dataPoints);
    }

    @PostMapping("/devices")
    public ResponseEntity<Boolean> saveDevice(@RequestBody SteamBoiler boiler) {
        boolean result = steamBoilerService.saveBoiler(boiler);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/devices/{id}")
    public ResponseEntity<Boolean> updateDevice(@PathVariable String id, @RequestBody SteamBoiler boiler) {
        boiler.setId(id);
        boolean result = steamBoilerService.updateBoiler(boiler);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/devices/{id}")
    public ResponseEntity<Boolean> deleteDevice(@PathVariable String id) {
        boolean result = steamBoilerService.deleteBoiler(id);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/data")
    public ResponseEntity<Boolean> saveDataPoint(@RequestBody SteamDataPoint dataPoint) {
        if (dataPoint.getDataTime() == null) {
            dataPoint.setDataTime(LocalDateTime.now());
        }
        boolean result = steamDataPointService.saveDataPoint(dataPoint);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/profile/{enterpriseId}")
    public ResponseEntity<UserProfile> getProfile(@PathVariable String enterpriseId) {
        UserProfile profile = userProfileService.getByEnterpriseId(enterpriseId);
        return ResponseEntity.ok(profile);
    }

    @PostMapping("/profile")
    public ResponseEntity<Boolean> saveProfile(@RequestBody UserProfile profile) {
        boolean result = userProfileService.saveOrUpdateProfile(profile);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/profile/{enterpriseId}/production-pattern")
    public ResponseEntity<Map<String, Object>> getProductionPattern(@PathVariable String enterpriseId) {
        UserProfile profile = userProfileService.getByEnterpriseId(enterpriseId);
        Map<String, Object> result = new HashMap<>();
        if (profile != null) {
            result.put("productionPattern", profile.getProductionPattern());
            result.put("lastUpdated", profile.getLastUpdated());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/profile/{enterpriseId}/energy-consumption")
    public ResponseEntity<Map<String, Object>> getEnergyConsumption(
            @PathVariable String enterpriseId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        List<EnergyConsumption> list = energyConsumptionService.listByEnterpriseAndDateRange(enterpriseId, start, end);
        Map<String, Object> result = new HashMap<>();
        result.put("data", list);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/strategy/{boilerId}")
    public ResponseEntity<SteamStrategy> getStrategy(@PathVariable String boilerId) {
        SteamStrategy strategy = steamStrategyService.getByBoilerId(boilerId);
        return ResponseEntity.ok(strategy);
    }

    @PostMapping("/strategy/{boilerId}")
    public ResponseEntity<Boolean> saveStrategy(@PathVariable String boilerId, @RequestBody SteamStrategy strategy) {
        strategy.setBoilerId(boilerId);
        boolean result = steamStrategyService.saveStrategy(strategy);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/strategy/{id}/activate")
    public ResponseEntity<Boolean> activateStrategy(@PathVariable String id) {
        boolean result = steamStrategyService.activateStrategy(id);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/strategy/{id}/suspend")
    public ResponseEntity<Boolean> suspendStrategy(@PathVariable String id) {
        boolean result = steamStrategyService.suspendStrategy(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/strategy/optimization/{enterpriseId}")
    public ResponseEntity<Map<String, Object>> getOptimization(@PathVariable String enterpriseId) {
        List<SteamBoiler> boilers = steamBoilerService.listByEnterpriseId(enterpriseId);
        Map<String, Object> result = new HashMap<>();
        result.put("boilers", boilers);
        result.put("suggestions", List.of());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/energy/realtime")
    public ResponseEntity<Map<String, Object>> getRealtimeEnergy() {
        List<SteamBoiler> boilers = steamBoilerService.list();
        Map<String, Object> result = new HashMap<>();
        result.put("totalBoilers", boilers.size());
        result.put("onlineBoilers", boilers.stream().filter(b -> "online".equals(b.getStatus())).count());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/energy/report/daily")
    public ResponseEntity<List<EnergyConsumption>> getDailyReport(@RequestParam String date) {
        LocalDate recordDate = LocalDate.parse(date);
        List<EnergyConsumption> report = energyConsumptionService.listDailyReport(recordDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/energy/report/monthly")
    public ResponseEntity<List<EnergyConsumption>> getMonthlyReport(
            @RequestParam int year,
            @RequestParam int month) {
        List<EnergyConsumption> report = energyConsumptionService.listMonthlyReport(year, month);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/energy/compare")
    public ResponseEntity<Map<String, Object>> compareEnergy(
            @RequestParam String enterpriseId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        List<EnergyConsumption> list = energyConsumptionService.listByEnterpriseAndDateRange(enterpriseId, start, end);
        Map<String, Object> result = new HashMap<>();
        result.put("data", list);
        return ResponseEntity.ok(result);
    }
}
