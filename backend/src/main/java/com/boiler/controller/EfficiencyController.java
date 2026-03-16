package com.boiler.controller;

import com.boiler.dto.*;
import com.boiler.service.EfficiencyAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/efficiency")
@RequiredArgsConstructor
@CrossOrigin
public class EfficiencyController {

    private final EfficiencyAnalysisService efficiencyAnalysisService;

    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getOverview(
            @RequestParam(required = false) Long stationId,
            @RequestParam(required = false) String date) {
        EfficiencyOverviewDTO overview = efficiencyAnalysisService.getOverview(stationId, date);
        Map<String, Object> result = new HashMap<>();
        result.put("cop", overview.getCop());
        result.put("heatLossRate", overview.getHeatLossRate());
        result.put("supplyBalance", overview.getSupplyBalance());
        result.put("avgIndoorTemp", overview.getAvgIndoorTemp());
        result.put("totalHeatInput", overview.getTotalHeatInput());
        result.put("totalHeatOutput", overview.getTotalHeatOutput());
        result.put("targetTempCompliance", overview.getTargetTempCompliance());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/ranking")
    public ResponseEntity<Map<String, Object>> getRanking(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String metricType,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        List<StationRankingDTO> rankings = efficiencyAnalysisService.getRanking(startDate, endDate, metricType, limit);
        Map<String, Object> result = new HashMap<>();
        result.put("data", rankings);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/terminal-monitor")
    public ResponseEntity<Map<String, Object>> getTerminalMonitor(
            @RequestParam Long stationId,
            @RequestParam(required = false) Long buildingId) {
        List<TerminalMonitorDTO> monitors = efficiencyAnalysisService.getTerminalMonitor(stationId, buildingId);
        Map<String, Object> result = new HashMap<>();
        result.put("data", monitors);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/comparison")
    public ResponseEntity<Map<String, Object>> getComparison(
            @RequestParam Long stationId,
            @RequestParam String type,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        EfficiencyComparisonDTO comparison = efficiencyAnalysisService.getComparison(stationId, type, startDate, endDate);
        Map<String, Object> result = new HashMap<>();
        result.put("realtime", comparison.getRealtime());
        result.put("history", comparison.getHistory());
        result.put("comparison", comparison.getComparison());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/resident-temperatures")
    public ResponseEntity<Map<String, Object>> getResidentTemperatures(
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String dataSource) {
        List<ResidentTemperatureDTO> temps = efficiencyAnalysisService.getResidentTemperatures(buildingId, startDate, endDate, dataSource);
        Map<String, Object> result = new HashMap<>();
        result.put("data", temps);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/resident-temperatures/manual")
    public ResponseEntity<Map<String, Object>> saveResidentTemperature(@RequestBody ResidentTemperatureDTO dto) {
        boolean success = efficiencyAnalysisService.saveResidentTemperature(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/heating-curve")
    public ResponseEntity<Map<String, Object>> getHeatingCurve(@RequestParam Long stationId) {
        HeatingCurveDTO curve = efficiencyAnalysisService.getHeatingCurve(stationId);
        Map<String, Object> result = new HashMap<>();
        result.put("currentCurve", curve.getCurrentCurve());
        result.put("optimizedCurve", curve.getOptimizedCurve());
        result.put("estimatedSavings", curve.getEstimatedSavings());
        result.put("analysis", curve.getAnalysis());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/heating-curve/apply")
    public ResponseEntity<Map<String, Object>> applyHeatingCurve(
            @RequestParam Long stationId,
            @RequestBody List<HeatingCurveDTO.CurvePoint> curve) {
        boolean success = efficiencyAnalysisService.applyHeatingCurve(stationId, curve);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("effectiveTime", java.time.LocalDateTime.now().plusHours(1).toString());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/trend")
    public ResponseEntity<Map<String, Object>> getTrend(
            @RequestParam Long stationId,
            @RequestParam(required = false) String metricType,
            @RequestParam(required = false, defaultValue = "week") String period) {
        EfficiencyTrendDTO trend = efficiencyAnalysisService.getTrend(stationId, metricType, period);
        Map<String, Object> result = new HashMap<>();
        result.put("dates", trend.getDates());
        result.put("cop", trend.getCop());
        result.put("heatLossRate", trend.getHeatLossRate());
        result.put("supplyBalance", trend.getSupplyBalance());
        return ResponseEntity.ok(result);
    }
}
