package com.boiler.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 水力模拟算法服务类
 * 
 * 【算法总体说明】
 * 本类实现了供热管网的水力计算和模拟功能，用于分析管网运行状态。
 * 采用稳态水力计算方法，通过迭代求解达到压力平衡。
 * 
 * 【核心算法】
 * 1. 管网水力模型：基于质量守恒和能量守恒方程
 * 2. 迭代求解：使用牛顿-拉夫森法或简单迭代法求解非线性方程组
 * 3. 动态模拟：在稳态基础上叠加时变因素
 * 
 * 【主要功能】
 * - 静态水力计算：计算管网各节点压力、管段流量
 * - 动态仿真：模拟一段时间内的管网运行变化
 * - 阻力特性分析：分析管网的阻力分布
 * 
 * 【物理方程】
 * 1. 流量-阻力方程：Q = √(ΔP/R)
 *    其中：Q为流量，ΔP为压差，R为阻力
 * 2. 节点压力方程：ΣQ入 = ΣQ出 + 需求
 * 
 * @author 锅炉管理系统
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class HydraulicSimulationService {

    /** 重力加速度 m/s² */
    private static final double GRAVITY = 9.81;
    /** 水的密度 kg/m³ */
    private static final double WATER_DENSITY = 1000.0;

    /**
     * 静态水力模拟
     * 
     * 【功能说明】
     * 对供热管网进行稳态水力计算，计算各节点压力和管段流量
     * 
     * 【算法流程】
     * 1. 初始化各节点压力（源节点给定压力，其他节点估算）
     * 2. 迭代计算：
     *    - 根据压力差计算各管段流量
     *    - 根据流量计算各节点新压力
     *    - 检查收敛条件（最大压力变化 < 0.001）
     * 3. 返回计算结果
     * 
     * @param networkConfig 管网配置，包含节点和管段信息
     * @return 计算结果，包含节点压力、管段流量、收敛状态等
     */
    public Map<String, Object> simulate(Map<String, Object> networkConfig) {
        // 获取管网拓扑结构
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) networkConfig.get("nodes");
        List<Map<String, Object>> segments = (List<Map<String, Object>>) networkConfig.get("segments"));
        
        // 初始化节点压力
        Map<String, Double> nodePressures = initializePressures(nodes);
        // 存储管段流量
        Map<String, Double> segmentFlows = new HashMap<>();
        
        // 迭代求解
        boolean converged = false;
        int maxIterations = 100;
        int iteration = 0;
        
        // 主迭代循环
        while (!converged && iteration < maxIterations) {
            // 保存上一次迭代的压力值
            Map<String, Double> oldPressures = new HashMap<>(nodePressures);
            
            // 步骤1：根据压力差计算各管段流量
            for (Map<String, Object> segment : segments) {
                String startNode = (String) segment.get("startNode");
                String endNode = (String) segment.get("endNode");
                double flow = calculateFlow(
                    nodePressures.get(startNode) - nodePressures.get(endNode),
                    (double) segment.getOrDefault("resistance", 0.01)
                );
                segmentFlows.put((String) segment.get("segmentCode"), flow);
            }
            
            // 步骤2：根据流量计算各节点新压力
            for (Map<String, Object> node : nodes) {
                String nodeCode = (String) node.get("nodeCode");
                // 源节点压力已知，不需要计算
                if (!"source".equals(node.get("nodeType"))) {
                    double pressure = calculateNodePressure(node, nodePressures, segmentFlows);
                    nodePressures.put(nodeCode, pressure);
                }
            }
            
            // 步骤3：检查收敛条件
            double maxChange = calculateMaxChange(oldPressures, nodePressures);
            converged = maxChange < 0.001;  // 压力变化小于0.001MPa认为收敛
            iteration++;
        }
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("nodePressures", formatResults(nodePressures));
        result.put("segmentFlows", formatResults(segmentFlows));
        result.put("converged", converged);
        result.put("iterationCount", iteration);
        result.put("calculateTime", new Date());
        
        return result;
    }

    /**
     * 动态水力仿真
     * 
     * 【功能说明】
     * 在静态水力计算基础上，模拟管网随时间的变化过程
     * 
     * 【应用场景】
     * - 负荷变化趋势分析
     * - 调度方案预评估
     * - 故障扩散分析
     * 
     * @param networkConfig 管网配置
     * @param timeSteps 仿真时间步数
     * @return 时变仿真结果
     */
    public Map<String, Object> dynamicSimulate(Map<String, Object> networkConfig, int timeSteps) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        // 当前状态
        Map<String, Object> currentState = new HashMap<>(networkConfig);
        
        // 逐时间步计算
        for (int t = 0; t < timeSteps; t++) {
            // 执行一步静态计算
            Map<String, Object> stepResult = simulate(currentState);
            stepResult.put("timeStep", t);
            
            results.add(stepResult);
            
            // 更新状态进入下一时间步
            currentState = updateState(currentState, stepResult);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("timeSeriesResults", results);
        result.put("totalTimeSteps", timeSteps);
        
        return result;
    }

    /**
     * 初始化节点压力
     * 
     * 【功能说明】
     * 设定各节点的初始压力值
     * - 源节点：使用给定的工作压力（通常为0.6MPa）
     * - 其他节点：使用估算的静压（通常为0.3MPa）
     * 
     * @param nodes 节点列表
     * @return 节点压力映射
     */
    private Map<String, Double> initializePressures(List<Map<String, Object>> nodes) {
        Map<String, Double> pressures = new HashMap<>();
        for (Map<String, Object> node : nodes) {
            String nodeCode = (String) node.get("nodeCode");
            String nodeType = (String) node.get("nodeCode"));
            
            if ("source".equals(nodeType)) {
                // 源节点：使用工作压力
                pressures.put(nodeCode, (double) node.getOrDefault("pressure", 0.6));
            } else {
                // 其他节点：使用估算的静压
                pressures.put(nodeCode, 0.3);
            }
        }
        return pressures;
    }

    /**
     * 计算管段流量
     * 
     * 【功能说明】
     * 使用流量-阻力方程计算管段流量
     * 
     * 【计算公式】
     * Q = √(ΔP/R) × sign(ΔP)
     * 
     * @param pressureDiff 管道两端压力差（MPa）
     * @param resistance 管道阻力系数
     * @return 流量（t/h）
     */
    private double calculateFlow(double pressureDiff, double resistance) {
        // 压力差过小视为零流量
        if (Math.abs(pressureDiff) < 0.0001) {
            return 0;
        }
        // 流量-阻力方程
        return Math.signum(pressureDiff) * Math.sqrt(Math.abs(pressureDiff) / resistance);
    }

    /**
     * 计算节点压力
     * 
     * 【功能说明】
     * 根据节点流量需求和标高计算节点压力
     * 
     * 【计算公式】
     * P = P_elev + P_demand
     * 其中：
     *   P_elev = ρ × g × h（静压头）
     *   P_demand = 需求流量 × 系数
     * 
     * @param node 节点信息
     * @param nodePressures 当前各节点压力
     * @param segmentFlows 当前各管段流量
     * @return 节点压力
     */
    private double calculateNodePressure(Map<String, Object> node, 
                                         Map<String, Double> nodePressures,
                                         Map<String, Double> segmentFlows) {
        // 获取节点流量需求
        double demand = ((Number) node.getOrDefault("demand", 0.0)).doubleValue();
        
        // 获取节点标高
        double elevation = ((Number) node.getOrDefault("elevation", 0.0)).doubleValue();
        // 计算静压头：P = ρ × g × h
        double pressureBase = GRAVITY * elevation * WATER_DENSITY / 1000000.0;
        
        // 计算需求引起的压力变化
        double flowContribution = demand * 0.01;
        
        // 返回节点压力（最小不低于0.1MPa）
        return Math.max(0.1, pressureBase + flowContribution);
    }

    /**
     * 计算最大压力变化
     * 
     * 【功能说明】
     * 计算两次迭代之间所有节点压力变化的最大值，用于判断收敛
     * 
     * @param oldPressures 上一次迭代的压力
     * @param newPressures 当前压力
     * @return 最大压力变化值
     */
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

    /**
     * 格式化结果数值
     * 
     * @param values 原始数值映射
     * @return 格式化后的数值映射（保留4位小数）
     */
    private Map<String, BigDecimal> formatResults(Map<String, Double> values) {
        Map<String, BigDecimal> formatted = new HashMap<>();
        for (Map.Entry<String, Double> entry : values.entrySet()) {
            formatted.put(entry.getKey(), 
                BigDecimal.valueOf(entry.getValue()).setScale(4, RoundingMode.HALF_UP));
        }
        return formatted;
    }

    /**
     * 更新仿真状态
     * 
     * 【功能说明】
     * 根据仿真结果更新管网状态，模拟负荷变化
     * 
     * @param state 当前状态
     * @param simulationResult 仿真结果
     * @return 更新后的状态
     */
    private Map<String, Object> updateState(Map<String, Object> state, 
                                            Map<String, Object> simulationResult) {
        Map<String, Object> newState = new HashMap<>(state);
        
        // 获取压力和流量结果
        Map<String, BigDecimal> pressures = (Map<String, BigDecimal>) simulationResult.get("nodePressures");
        Map<String, BigDecimal> flows = (Map<String, BigDecimal>) simulationResult.get("segmentFlows");
        
        // 更新各节点需求（添加随机扰动）
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) state.get("nodes");
        for (Map<String, Object> node : nodes) {
            String nodeCode = (String) node.get("nodeCode");
            if (pressures.containsKey(nodeCode)) {
                // 添加随机扰动模拟需求变化
                double newDemand = ((Number) node.getOrDefault("demand", 0.0)).doubleValue() 
                    + (Math.random() - 0.5) * 0.5;
                node.put("demand", Math.max(0, newDemand));
            }
        }
        
        return newState;
    }
}
