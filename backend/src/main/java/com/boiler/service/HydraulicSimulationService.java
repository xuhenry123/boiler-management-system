package com.boiler.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@RequiredArgsConstructor
public class HydraulicSimulationService {

    private static final double GRAVITY = 9.81;
    private static final double WATER_DENSITY = 1000.0;

    public Map<String, Object> simulate(Map<String, Object> networkConfig) {
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) networkConfig.get("nodes");
        List<Map<String, Object>> segments = (List<Map<String, Object>>) networkConfig.get("segments");
        
        Map<String, Double> nodePressures = initializePressures(nodes);
        Map<String, Double> segmentFlows = new HashMap<>();
        
        boolean converged = false;
        int maxIterations = 100;
        int iteration = 0;
        
        while (!converged && iteration < maxIterations) {
            Map<String, Double> oldPressures = new HashMap<>(nodePressures);
            
            for (Map<String, Object> segment : segments) {
                String startNode = (String) segment.get("startNode");
                String endNode = (String) segment.get("endNode");
                double flow = calculateFlow(
                    nodePressures.get(startNode) - nodePressures.get(endNode),
                    (double) segment.getOrDefault("resistance", 0.01)
                );
                segmentFlows.put((String) segment.get("segmentCode"), flow);
            }
            
            for (Map<String, Object> node : nodes) {
                String nodeCode = (String) node.get("nodeCode");
                if (!"source".equals(node.get("nodeType"))) {
                    double pressure = calculateNodePressure(node, nodePressures, segmentFlows);
                    nodePressures.put(nodeCode, pressure);
                }
            }
            
            double maxChange = calculateMaxChange(oldPressures, nodePressures);
            converged = maxChange < 0.001;
            iteration++;
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("nodePressures", formatResults(nodePressures));
        result.put("segmentFlows", formatResults(segmentFlows));
        result.put("converged", converged);
        result.put("iterationCount", iteration);
        result.put("calculateTime", new Date());
        
        return result;
    }

    public Map<String, Object> dynamicSimulate(Map<String, Object> networkConfig, int timeSteps) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        Map<String, Object> currentState = new HashMap<>(networkConfig);
        
        for (int t = 0; t < timeSteps; t++) {
            Map<String, Object> stepResult = simulate(currentState);
            stepResult.put("timeStep", t);
            
            results.add(stepResult);
            
            currentState = updateState(currentState, stepResult);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("timeSeriesResults", results);
        result.put("totalTimeSteps", timeSteps);
        
        return result;
    }

    private Map<String, Double> initializePressures(List<Map<String, Object>> nodes) {
        Map<String, Double> pressures = new HashMap<>();
        for (Map<String, Object> node : nodes) {
            String nodeCode = (String) node.get("nodeCode");
            String nodeType = (String) node.get("nodeType");
            
            if ("source".equals(nodeType)) {
                pressures.put(nodeCode, (double) node.getOrDefault("pressure", 0.6));
            } else {
                pressures.put(nodeCode, 0.3);
            }
        }
        return pressures;
    }

    private double calculateFlow(double pressureDiff, double resistance) {
        if (Math.abs(pressureDiff) < 0.0001) {
            return 0;
        }
        return Math.signum(pressureDiff) * Math.sqrt(Math.abs(pressureDiff) / resistance);
    }

    private double calculateNodePressure(Map<String, Object> node, 
                                         Map<String, Double> nodePressures,
                                         Map<String, Double> segmentFlows) {
        double demand = ((Number) node.getOrDefault("demand", 0.0)).doubleValue();
        
        double elevation = ((Number) node.getOrDefault("elevation", 0.0)).doubleValue();
        double pressureBase = GRAVITY * elevation * WATER_DENSITY / 1000000.0;
        
        double flowContribution = demand * 0.01;
        
        return Math.max(0.1, pressureBase + flowContribution);
    }

    private double calculateMaxChange(Map<String, Double> oldPressures, 
                                       Map<String, Double> newPressures) {
        double maxChange = 0.0;
        for (String node : oldPressures.keySet()) {
            double change = Math.abs(newPressures.get(node) - oldPressures.get(node));
            if (change > maxChange) {
                maxChange = change;
            }
        }
        return maxChange;
    }

    private Map<String, BigDecimal> formatResults(Map<String, Double> values) {
        Map<String, BigDecimal> formatted = new HashMap<>();
        for (Map.Entry<String, Double> entry : values.entrySet()) {
            formatted.put(entry.getKey(), 
                BigDecimal.valueOf(entry.getValue()).setScale(4, RoundingMode.HALF_UP));
        }
        return formatted;
    }

    private Map<String, Object> updateState(Map<String, Object> state, 
                                            Map<String, Object> simulationResult) {
        Map<String, Object> newState = new HashMap<>(state);
        
        Map<String, BigDecimal> pressures = (Map<String, BigDecimal>) simulationResult.get("nodePressures");
        Map<String, BigDecimal> flows = (Map<String, BigDecimal>) simulationResult.get("segmentFlows");
        
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) state.get("nodes");
        for (Map<String, Object> node : nodes) {
            String nodeCode = (String) node.get("nodeCode");
            if (pressures.containsKey(nodeCode)) {
                double newDemand = ((Number) node.getOrDefault("demand", 0.0)).doubleValue() 
                    + (Math.random() - 0.5) * 0.5;
                node.put("demand", Math.max(0, newDemand));
            }
        }
        
        return newState;
    }
}
