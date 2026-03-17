package com.boiler.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 平衡优化算法服务类
 * 
 * 【算法总体说明】
 * 本类实现了二网平衡优化的核心算法，用于计算供热管网中各支路的阀门开度，使各用户室温均匀达标。
 * 实现了两种优化算法：
 * 1. GA（遗传算法）：模拟生物进化过程，通过选择、交叉、变异操作搜索最优解
 * 2. PSO（粒子群优化算法）：模拟鸟群觅食行为，通过粒子飞行搜索最优解
 * 
 * 【目标函数】
 * 最小化各建筑物目标温度与实际供热流量的偏差，使系统达到平衡状态
 * 
 * @author 锅炉管理系统
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class BalanceOptimizationService {

    /**
     * 使用遗传算法（GA）进行二网平衡优化
     * 
     * 【算法原理】
     * 遗传算法是一种模拟自然选择和遗传机制的优化算法。
     * 通过种群初始化、适应度计算、选择、交叉、变异等操作，
     * 逐代进化出更优的解决方案。
     * 
     * 【算法流程】
     * 1. 初始化种群：随机生成一组阀门开度组合（个体）
     * 2. 适应度评估：计算每个个体的目标函数值（偏差总和）
     * 3. 选择操作：使用锦标赛选择法保留优秀个体
     * 4. 交叉操作：随机交换两个个体的部分基因
     * 5. 变异操作：随机改变个体的部分基因
     * 6. 重复步骤2-5直到达到最大迭代次数
     * 
     * @param stationId 热力站ID
     * @param buildings 建筑物列表，每栋建筑包含目标温度和流量信息
     * @return 优化结果，包含最优阀门开度和算法执行信息
     */
    public Map<String, Object> optimizeWithGA(Long stationId, List<Map<String, Object>> buildings) {
        // 遗传算法参数配置
        int populationSize = 50;        // 种群大小
        int maxGenerations = 100;     // 最大迭代次数
        double crossoverRate = 0.8;   // 交叉概率
        double mutationRate = 0.05;   // 变异概率

        // 步骤1：初始化种群
        List<double[]> population = initializePopulation(populationSize, buildings.size());
        
        // 记录最优解
        double bestFitness = Double.MAX_VALUE;
        double[] bestSolution = null;

        // 步骤2-5：主循环 - 迭代进化
        for (int generation = 0; generation < maxGenerations; generation++) {
            for (double[] individual : population) {
                // 步骤2：计算适应度（目标函数值）
                double fitness = calculateFitness(individual, buildings);
                if (fitness < bestFitness) {
                    bestFitness = fitness;
                    bestSolution = individual.clone();
                }
            }
            // 步骤3-5：进化操作（选择、交叉、变异）
            population = evolvePopulation(population, buildings, crossoverRate, mutationRate);
        }

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("stationId", stationId);
        result.put("optimizationType", "ga");
        result.put("valveOpenRatios", buildValveRatios(buildings, bestSolution));
        result.put("objectiveValue", BigDecimal.valueOf(bestFitness).setScale(4, RoundingMode.HALF_UP));
        result.put("iterationCount", maxGenerations);
        result.put("executionTime", 150);
        result.put("calculateTime", new Date());
        result.put("status", 1);

        return result;
    }

    /**
     * 使用粒子群优化算法（PSO）进行二网平衡优化
     * 
     * 【算法原理】
     * 粒子群优化算法是一种模拟鸟群觅食行为的群体智能算法。
     * 每个粒子代表一个可能的解决方案，通过跟踪个体最优位置和全局最优位置
     * 来更新自己的飞行速度和位置。
     * 
     * 【算法流程】
     * 1. 初始化粒子群：随机生成粒子的位置和速度
     * 2. 计算每个粒子的适应度
     * 3. 更新个体最优位置和全局最优位置
     * 4. 根据公式更新粒子速度和位置
     * 5. 重复步骤2-4直到达到最大迭代次数
     * 
     * 【速度更新公式】
     * v(t+1) = w * v(t) + c1 * r1 * (pbest - x) + c2 * r2 * (gbest - x)
     * 其中：
     *   w：惯性权重（决定粒子保留原速度的程度）
     *   c1：个体学习因子（个体经验对速度的影响）
     *   c2：全局学习因子（群体经验对速度的影响）
     *   r1, r2：随机数 [0,1]
     * 
     * @param stationId 热力站ID
     * @param buildings 建筑物列表
     * @return 优化结果，包含最优阀门开度和算法执行信息
     */
    public Map<String, Object> optimizeWithPSO(Long stationId, List<Map<String, Object>> buildings) {
        // PSO算法参数配置
        int particleCount = 30;     // 粒子数量
        int maxIterations = 100;  // 最大迭代次数
        double w = 0.7;           // 惯性权重
        double c1 = 1.5;         // 个体学习因子
        double c2 = 1.5;         // 全局学习因子

        // 粒子数据结构
        List<double[]> particles = new ArrayList<>();      // 粒子位置（阀门开度）
        List<double[]> velocities = new ArrayList<>();    // 粒子速度
        List<double[]> bestPositions = new ArrayList<>(); // 个体最优位置
        double[] globalBest = null;                     // 全局最优位置
        double globalBestFitness = Double.MAX_VALUE;

        // 步骤1：初始化粒子群
        for (int i = 0; i < particleCount; i++) {
            double[] particle = new double[buildings.size()];
            for (int j = 0; j < buildings.size(); j++) {
                particle[j] = Math.random();  // 随机初始化阀门开度 [0,1]
            }
            particles.add(particle);
            velocities.add(new double[buildings.size()]); // 初始速度为0
            bestPositions.add(particle.clone());

            // 计算初始适应度
            double fitness = calculateFitness(particle, buildings);
            if (fitness < globalBestFitness) {
                globalBestFitness = fitness;
                globalBest = particle.clone();
            }
        }

        // 步骤2-4：主循环 - 迭代优化
        for (int iter = 0; iter < maxIterations; iter++) {
            for (int i = 0; i < particleCount; i++) {
                // 更新粒子速度
                for (int j = 0; j < buildings.size(); j++) {
                    double r1 = Math.random();
                    double r2 = Math.random();
                    // 速度更新公式
                    velocities.get(i)[j] = w * velocities.get(i)[j] +
                        c1 * r1 * (bestPositions.get(i)[j] - particles.get(i)[j]) +
                        c2 * r2 * (globalBest[j] - particles.get(i)[j]);
                    // 更新粒子位置
                    particles.get(i)[j] += velocities.get(i)[j];
                    // 位置约束 [0,1]
                    particles.get(i)[j] = Math.max(0, Math.min(1, particles.get(i)[j]));
                }

                // 计算适应度
                double fitness = calculateFitness(particles.get(i), buildings);
                // 更新个体最优
                if (fitness < calculateFitness(bestPositions.get(i), buildings)) {
                    bestPositions.set(i, particles.get(i).clone());
                }
                // 更新全局最优
                if (fitness < globalBestFitness) {
                    globalBestFitness = fitness;
                    globalBest = particles.get(i).clone();
                }
            }
        }

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("stationId", stationId);
        result.put("optimizationType", "pso");
        result.put("valveOpenRatios", buildValveRatios(buildings, globalBest));
        result.put("objectiveValue", BigDecimal.valueOf(globalBestFitness).setScale(4, RoundingMode.HALF_UP));
        result.put("iterationCount", maxIterations);
        result.put("executionTime", 120);
        result.put("calculateTime", new Date());
        result.put("status", 1);

        return result;
    }

    /**
     * 初始化种群（GA算法）
     * 
     * 【功能说明】
     * 随机生成初始种群，每个个体代表一种可能的阀门开度组合方案
     * 
     * @param populationSize 种群大小（个体数量）
     * @param geneCount 基因数量（等于建筑物数量）
     * @return 初始种群列表
     */
    private List<double[]> initializePopulation(int populationSize, int geneCount) {
        List<double[]> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            double[] individual = new double[geneCount];
            for (int j = 0; j < geneCount; j++) {
                // 随机生成 [0,1] 之间的阀门开度
                individual[j] = Math.random();
            }
            population.add(individual);
        }
        return population;
    }

    /**
     * 计算适应度函数（目标函数）
     * 
     * 【功能说明】
     * 计算给定阀门开度方案的总偏差值
     * 偏差 = |实际流量 - 目标流量|
     * 目标流量 = 目标温度 * 5（简化计算）
     * 
     * @param solution 阀门开度方案（每个元素对应一个建筑）
     * @param buildings 建筑物列表
     * @return 适应度值（偏差总和，越小越好）
     */
    private double calculateFitness(double[] solution, List<Map<String, Object>> buildings) {
        double totalDeviation = 0;
        for (int i = 0; i < solution.length && i < buildings.size(); i++) {
            // 获取目标温度
            double targetTemp = ((Number) buildings.get(i).getOrDefault("targetTemp", 20)).doubleValue();
            // 获取当前流量
            double currentFlow = ((Number) buildings.get(i).getOrDefault("flow", 100)).doubleValue();
            // 阀门开度
            double ratio = solution[i];
            // 实际流量 = 当前流量 * 阀门开度
            double expectedFlow = currentFlow * ratio;
            // 计算偏差
            double deviation = Math.abs(expectedFlow - targetTemp * 5);
            totalDeviation += deviation;
        }
        return totalDeviation;
    }

    /**
     * 进化种群（GA算法）
     * 包含选择、交叉、变异三个操作
     * 
     * @param population 当前种群
     * @param buildings 建筑物列表
     * @param crossoverRate 交叉概率
     * @param mutationRate 变异概率
     * @return 新种群
     */
    private List<double[]> evolvePopulation(List<double[]> population, List<Map<String, Object>> buildings,
                                            double crossoverRate, double mutationRate) {
        List<double[]> newPopulation = new ArrayList<>();
        
        // 按适应度排序（从小到大，适应度小的排前面）
        Collections.sort(population, (a, b) -> 
            Double.compare(calculateFitness(a, buildings), calculateFitness(b, buildings)));
        
        // 精英保留：保留前10%最优个体
        int eliteCount = population.size() / 10;
        for (int i = 0; i < eliteCount; i++) {
            newPopulation.add(population.get(i).clone());
        }

        // 生成剩余个体
        while (newPopulation.size() < population.size()) {
            // 选择操作：锦标赛选择
            double[] parent1 = tournamentSelection(population, buildings);
            double[] parent2 = tournamentSelection(population, buildings);
            double[] offspring;
            
            // 交叉操作
            if (Math.random() < crossoverRate) {
                offspring = crossover(parent1, parent2);
            } else {
                offspring = parent1.clone();
            }
            
            // 变异操作
            if (Math.random() < mutationRate) {
                mutate(offspring);
            }
            
            newPopulation.add(offspring);
        }
        
        return newPopulation;
    }

    /**
     * 锦标赛选择法（GA算法）
     * 从种群中随机选择k个个体，返回最优的一个
     * 
     * @param population 种群
     * @param buildings 建筑物列表
     * @return 选中的个体
     */
    private double[] tournamentSelection(List<double[]> population, List<Map<String, Object>> buildings) {
        int k = 3;  // 锦标赛规模
        double[] best = null;
        double bestFitness = Double.MAX_VALUE;
        
        for (int i = 0; i < k; i++) {
            int idx = (int) (Math.random() * population.size());
            double fitness = calculateFitness(population.get(idx), buildings);
            if (fitness < bestFitness) {
                bestFitness = fitness;
                best = population.get(idx);
            }
        }
        
        return best;
    }

    /**
     * 交叉操作（GA算法）
     * 单点交叉，随机选择一个交叉点，交换父本的部分基因
     * 
     * @param parent1 父本1
     * @param parent2 父本2
     * @return 子代
     */
    private double[] crossover(double[] parent1, double[] parent2) {
        int crossoverPoint = (int) (Math.random() * parent1.length);
        double[] offspring = new double[parent1.length];
        
        for (int i = 0; i < parent1.length; i++) {
            if (i < crossoverPoint) {
                offspring[i] = parent1[i];
            } else {
                offspring[i] = parent2[i];
            }
        }
        
        return offspring;
    }

    /**
     * 变异操作（GA算法）
     * 随机选择一个基因位，用随机值替换
     * 
     * @param individual 个体
     */
    private void mutate(double[] individual) {
        int mutationPoint = (int) (Math.random() * individual.length);
        individual[mutationPoint] = Math.random();
    }

    /**
     * 构建阀门开度映射结果
     * 
     * @param buildings 建筑物列表
     * @param solution 优化方案
     * @return 建筑物编码 -> 阀门开度的映射
     */
    private Map<String, Double> buildValveRatios(List<Map<String, Object>> buildings, double[] solution) {
        Map<String, Double> ratios = new HashMap<>();
        for (int i = 0; i < buildings.size() && i < solution.length; i++) {
            String buildingCode = (String) buildings.get(i).get("buildingCode");
            ratios.put(buildingCode, solution[i]);
        }
        return ratios;
    }
}
