package com.boiler.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boiler.entity.*;
import com.boiler.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PipeNetworkBalanceService extends ServiceImpl<PipeNodeMapper, PipeNode> {

    private final PipeNodeMapper pipeNodeMapper;
    private final PipeSegmentMapper pipeSegmentMapper;
    private final BalanceConfigMapper balanceConfigMapper;
    private final BalanceStrategyMapper balanceStrategyMapper;
    private final BalanceOptimizationService balanceOptimizationService;

    public Map<String, Object> getNetworkTopology(String networkType) {
        Map<String, Object> result = new HashMap<>();
        
        List<PipeNode> nodes = pipeNodeMapper.selectList(
            new LambdaQueryWrapper<PipeNode>()
                .eq(PipeNode::getNodeType, networkType)
                .orderByAsc(PipeNode::getSortOrder)
        );
        
        List<PipeSegment> segments = pipeSegmentMapper.selectList(null);
        
        result.put("nodes", nodes);
        result.put("segments", segments);
        result.put("nodeCount", nodes.size());
        result.put("segmentCount", segments.size());
        
        return result;
    }

    public Map<String, Object> getNetworkStatus(String networkType) {
        Map<String, Object> status = new HashMap<>();
        
        List<PipeNode> nodes = pipeNodeMapper.selectList(
            new LambdaQueryWrapper<PipeNode>()
                .eq(PipeNode::getNodeType, networkType)
        );
        
        if (nodes.isEmpty()) {
            status.put("avgTemperature", 0);
            status.put("avgPressure", 0);
            status.put("avgFlow", 0);
            status.put("balanceDeviation", 0);
            return status;
        }
        
        double avgTemp = nodes.stream()
            .filter(n -> n.getTemperature() != null)
            .mapToDouble(PipeNode::getTemperature)
            .average().orElse(0);
        
        double avgPressure = nodes.stream()
            .filter(n -> n.getPressure() != null)
            .mapToDouble(PipeNode::getPressure)
            .average().orElse(0);
        
        double avgFlow = nodes.stream()
            .filter(n -> n.getFlow() != null)
            .mapToDouble(PipeNode::getFlow)
            .average().orElse(0);
        
        double maxTemp = nodes.stream()
            .filter(n -> n.getTemperature() != null)
            .mapToDouble(PipeNode::getTemperature)
            .max().orElse(0);
        
        double minTemp = nodes.stream()
            .filter(n -> n.getTemperature() != null)
            .mapToDouble(PipeNode::getTemperature)
            .min().orElse(0);
        
        status.put("avgTemperature", BigDecimal.valueOf(avgTemp).setScale(2, RoundingMode.HALF_UP));
        status.put("avgPressure", BigDecimal.valueOf(avgPressure).setScale(3, RoundingMode.HALF_UP));
        status.put("avgFlow", BigDecimal.valueOf(avgFlow).setScale(2, RoundingMode.HALF_UP));
        status.put("maxTemperature", maxTemp);
        status.put("minTemperature", minTemp);
        status.put("balanceDeviation", BigDecimal.valueOf(maxTemp - minTemp).setScale(2, RoundingMode.HALF_UP));
        status.put("nodeCount", nodes.size());
        
        return status;
    }

    public Map<String, Object> executeOptimization(String networkType, Long stationId, String algorithm) {
        List<Map<String, Object>> buildings = getBuildingsWithData(stationId);
        
        Map<String, Object> optimizationResult;
        if ("GA".equalsIgnoreCase(algorithm)) {
            optimizationResult = balanceOptimizationService.optimizeWithGA(stationId, buildings);
        } else {
            optimizationResult = balanceOptimizationService.optimizeWithPSO(stationId, buildings);
        }
        
        saveStrategyResult(networkType, stationId, algorithm, optimizationResult);
        
        return optimizationResult;
    }

    @SuppressWarnings("unchecked")
    private void saveStrategyResult(String networkType, Long stationId, String algorithm, Map<String, Object> result) {
        BalanceStrategy strategy = new BalanceStrategy();
        strategy.setNetworkType(networkType);
        strategy.setStationId(stationId);
        strategy.setOptimizeAlgorithm(algorithm);
        strategy.setObjectiveValue((BigDecimal) result.get("objectiveValue"));
        strategy.setIterationCount((Integer) result.get("iterationCount"));
        strategy.setExecutionTime(((Number) result.get("executionTime")).longValue());
        strategy.setValveAdjustments(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(result.get("valveOpenRatios")));
        strategy.setStatus("COMPLETED");
        strategy.setDescription("优化完成");
        strategy.setCreateTime(LocalDateTime.now());
        strategy.setUpdateTime(LocalDateTime.now());
        
        balanceStrategyMapper.insert(strategy);
    }

    public List<Map<String, Object>> getValveAdjustmentSuggestions(String networkType) {
        List<Map<String, Object>> suggestions = new ArrayList<>();
        
        List<PipeNode> valveNodes = pipeNodeMapper.selectList(
            new LambdaQueryWrapper<PipeNode>()
                .eq(PipeNode::getNodeType, "VALVE")
                .eq(PipeNode::getStatus, 1)
        );
        
        BalanceConfig config = balanceConfigMapper.selectOne(
            new LambdaQueryWrapper<BalanceConfig>()
                .eq(BalanceConfig::getNetworkType, networkType)
                .eq(BalanceConfig::getStatus, 1)
        );
        
        double targetTemp = config != null && config.getIndoorTempTarget() != null 
            ? config.getIndoorTempTarget().doubleValue() 
            : 20.0;
        
        for (PipeNode node : valveNodes) {
            Map<String, Object> suggestion = new HashMap<>();
            suggestion.put("valveId", node.getId());
            suggestion.put("valveName", node.getNodeName());
            suggestion.put("currentTemperature", node.getTemperature());
            suggestion.put("targetTemperature", targetTemp);
            
            double currentTemp = node.getTemperature() != null ? node.getTemperature().doubleValue() : 20;
            double deviation = currentTemp - targetTemp;
            
            double adjustment;
            String action;
            if (deviation > 2) {
                adjustment = -10;
                action = "关小阀门";
            } else if (deviation < -2) {
                adjustment = 10;
                action = "开大阀门";
            } else {
                adjustment = 0;
                action = "保持当前开度";
            }
            
            suggestion.put("deviation", BigDecimal.valueOf(deviation).setScale(2, RoundingMode.HALF_UP));
            suggestion.put("adjustment", adjustment);
            suggestion.put("action", action);
            suggestion.put("priority", Math.abs(deviation) > 5 ? "HIGH" : "NORMAL");
            
            suggestions.add(suggestion);
        }
        
        return suggestions;
    }

    public boolean updateBalanceConfig(BalanceConfig config) {
        if (config.getId() == null) {
            config.setCreateTime(LocalDateTime.now());
            config.setUpdateTime(LocalDateTime.now());
            return balanceConfigMapper.insert(config) > 0;
        } else {
            config.setUpdateTime(LocalDateTime.now());
            return balanceConfigMapper.updateById(config) > 0;
        }
    }

    public BalanceConfig getBalanceConfig(String networkType) {
        return balanceConfigMapper.selectOne(
            new LambdaQueryWrapper<BalanceConfig>()
                .eq(BalanceConfig::getNetworkType, networkType)
                .eq(BalanceConfig::getStatus, 1)
        );
    }

    public List<Map<String, Object>> getBalanceHistory(String networkType, Integer limit) {
        List<BalanceStrategy> strategies = balanceStrategyMapper.selectList(
            new LambdaQueryWrapper<BalanceStrategy>()
                .eq(BalanceStrategy::getNetworkType, networkType)
                .orderByDesc(BalanceStrategy::getCreateTime)
                .last("LIMIT " + (limit != null ? limit : 10))
        );
        
        return strategies.stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", s.getId());
            map.put("strategyName", s.getStrategyName());
            map.put("objectiveValue", s.getObjectiveValue());
            map.put("iterationCount", s.getIterationCount());
            map.put("executionTime", s.getExecutionTime());
            map.put("optimizeAlgorithm", s.getOptimizeAlgorithm());
            map.put("status", s.getStatus());
            map.put("createTime", s.getCreateTime());
            return map;
        }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> getBuildingsWithData(Long stationId) {
        List<Map<String, Object>> buildings = new ArrayList<>();
        
        List<PipeNode> userNodes = pipeNodeMapper.selectList(
            new LambdaQueryWrapper<PipeNode>()
                .eq(PipeNode::getNodeType, "USER")
        );
        
        for (PipeNode node : userNodes) {
            Map<String, Object> building = new HashMap<>();
            building.put("buildingCode", node.getNodeCode());
            building.put("buildingName", node.getNodeName());
            building.put("targetTemp", node.getDesignTemp() != null ? node.getDesignTemp() : 20);
            building.put("flow", node.getFlow() != null ? node.getFlow() : 100);
            building.put("currentTemp", node.getTemperature());
            buildings.add(building);
        }
        
        return buildings;
    }
}
