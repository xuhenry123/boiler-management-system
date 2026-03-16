package com.boiler.controller;

import com.boiler.entity.BalanceConfig;
import com.boiler.service.PipeNetworkBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/balance")
@RequiredArgsConstructor
@CrossOrigin
public class PipeNetworkBalanceController {

    private final PipeNetworkBalanceService pipeNetworkBalanceService;

    @GetMapping("/topology")
    public Map<String, Object> getNetworkTopology(@RequestParam String networkType) {
        return pipeNetworkBalanceService.getNetworkTopology(networkType);
    }

    @GetMapping("/status")
    public Map<String, Object> getNetworkStatus(@RequestParam String networkType) {
        return pipeNetworkBalanceService.getNetworkStatus(networkType);
    }

    @PostMapping("/optimize")
    public Map<String, Object> executeOptimization(
            @RequestParam String networkType,
            @RequestParam Long stationId,
            @RequestParam(defaultValue = "GA") String algorithm) {
        return pipeNetworkBalanceService.executeOptimization(networkType, stationId, algorithm);
    }

    @GetMapping("/strategy")
    public List<Map<String, Object>> getValveAdjustmentSuggestions(@RequestParam String networkType) {
        return pipeNetworkBalanceService.getValveAdjustmentSuggestions(networkType);
    }

    @GetMapping("/config")
    public BalanceConfig getBalanceConfig(@RequestParam String networkType) {
        return pipeNetworkBalanceService.getBalanceConfig(networkType);
    }

    @PutMapping("/config")
    public Map<String, Object> updateBalanceConfig(@RequestBody BalanceConfig config) {
        Map<String, Object> result = new HashMap<>();
        boolean success = pipeNetworkBalanceService.updateBalanceConfig(config);
        result.put("success", success);
        result.put("message", success ? "配置更新成功" : "配置更新失败");
        return result;
    }

    @GetMapping("/history")
    public List<Map<String, Object>> getBalanceHistory(
            @RequestParam String networkType,
            @RequestParam(defaultValue = "10") Integer limit) {
        return pipeNetworkBalanceService.getBalanceHistory(networkType, limit);
    }
}
