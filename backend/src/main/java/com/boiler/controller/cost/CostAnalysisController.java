package com.boiler.controller.cost;

import com.boiler.dto.cost.CostResult;
import com.boiler.dto.cost.RankItem;
import com.boiler.dto.cost.StatisticsResult;
import com.boiler.service.cost.CostAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/cost")
@RequiredArgsConstructor
@CrossOrigin
public class CostAnalysisController {

    private final CostAnalysisService costAnalysisService;

    @GetMapping("/daily")
    public ResponseEntity<Map<String, Object>> getDailyCost(
            @RequestParam(required = false) String energyType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) List<Long> stationIds) {
        
        List<CostResult> data = costAnalysisService.getDailyCost(energyType, startDate, endDate, stationIds);
        
        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        result.put("total", data.size());
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/monthly")
    public ResponseEntity<Map<String, Object>> getMonthlyCost(
            @RequestParam(required = false) String energyType,
            @RequestParam Integer year,
            @RequestParam Integer month,
            @RequestParam(required = false) List<Long> stationIds) {
        
        List<CostResult> data = costAnalysisService.getMonthlyCost(energyType, year, month, stationIds);
        
        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        result.put("total", data.size());
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/yearly")
    public ResponseEntity<Map<String, Object>> getYearlyCost(
            @RequestParam(required = false) String energyType,
            @RequestParam Integer year,
            @RequestParam(required = false) List<Long> stationIds) {
        
        List<CostResult> data = costAnalysisService.getYearlyCost(energyType, year, stationIds);
        
        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        result.put("total", data.size());
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/trend")
    public ResponseEntity<Map<String, Object>> getCostTrend(
            @RequestParam(required = false) String energyType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) List<Long> stationIds) {
        
        Map<String, Object> data = costAnalysisService.getCostTrend(energyType, startDate, endDate, stationIds);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getMultiEnergySummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) List<Long> stationIds) {
        
        Map<String, Object> data = costAnalysisService.getMultiEnergySummary(startDate, endDate, stationIds);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/stats/efficiency")
    public ResponseEntity<StatisticsResult> getEfficiencyStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) List<Long> stationIds) {
        
        StatisticsResult data = costAnalysisService.getEfficiencyStatistics(startDate, endDate, stationIds);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/stats/consumption")
    public ResponseEntity<StatisticsResult> getConsumptionStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) List<Long> stationIds) {
        
        StatisticsResult data = costAnalysisService.getConsumptionStatistics(startDate, endDate, stationIds);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/stats/carbon")
    public ResponseEntity<StatisticsResult> getCarbonStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) List<Long> stationIds) {
        
        StatisticsResult data = costAnalysisService.getCarbonStatistics(startDate, endDate, stationIds);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/rank/efficiency")
    public ResponseEntity<List<RankItem>> getRankByEfficiency(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period,
            @RequestParam(required = false) List<Long> stationIds,
            @RequestParam(required = false, defaultValue = "10") Integer topN) {
        
        List<RankItem> data = costAnalysisService.getRankByEfficiency(period, stationIds, topN);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/rank/consumption")
    public ResponseEntity<List<RankItem>> getRankByConsumption(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period,
            @RequestParam(required = false) List<Long> stationIds,
            @RequestParam(required = false, defaultValue = "10") Integer topN) {
        
        List<RankItem> data = costAnalysisService.getRankByConsumption(period, stationIds, topN);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/rank/cost")
    public ResponseEntity<List<RankItem>> getRankByCost(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period,
            @RequestParam(required = false) List<Long> stationIds,
            @RequestParam(required = false, defaultValue = "10") Integer topN) {
        
        List<RankItem> data = costAnalysisService.getRankByCost(period, stationIds, topN);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/rank/carbon")
    public ResponseEntity<List<RankItem>> getRankByCarbon(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period,
            @RequestParam(required = false) List<Long> stationIds,
            @RequestParam(required = false, defaultValue = "10") Integer topN) {
        
        List<RankItem> data = costAnalysisService.getRankByCarbon(period, stationIds, topN);
        return ResponseEntity.ok(data);
    }
}
