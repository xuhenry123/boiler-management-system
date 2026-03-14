package com.boiler.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BalanceOptimizationService {

    public Map<String, Object> optimizeWithGA(Long stationId, List<Map<String, Object>> buildings) {
        int populationSize = 50;
        int maxGenerations = 100;
        double crossoverRate = 0.8;
        double mutationRate = 0.05;

        List<double[]> population = initializePopulation(populationSize, buildings.size());
        double bestFitness = Double.MAX_VALUE;
        double[] bestSolution = null;

        for (int generation = 0; generation < maxGenerations; generation++) {
            for (double[] individual : population) {
                double fitness = calculateFitness(individual, buildings);
                if (fitness < bestFitness) {
                    bestFitness = fitness;
                    bestSolution = individual.clone();
                }
            }
            population = evolvePopulation(population, buildings, crossoverRate, mutationRate);
        }

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

    public Map<String, Object> optimizeWithPSO(Long stationId, List<Map<String, Object>> buildings) {
        int particleCount = 30;
        int maxIterations = 100;
        double w = 0.7;
        double c1 = 1.5;
        double c2 = 1.5;

        List<double[]> particles = new ArrayList<>();
        List<double[]> velocities = new ArrayList<>();
        List<double[]> bestPositions = new ArrayList<>();
        double[] globalBest = null;
        double globalBestFitness = Double.MAX_VALUE;

        for (int i = 0; i < particleCount; i++) {
            double[] particle = new double[buildings.size()];
            for (int j = 0; j < buildings.size(); j++) {
                particle[j] = Math.random();
            }
            particles.add(particle);
            velocities.add(new double[buildings.size()]);
            bestPositions.add(particle.clone());

            double fitness = calculateFitness(particle, buildings);
            if (fitness < globalBestFitness) {
                globalBestFitness = fitness;
                globalBest = particle.clone();
            }
        }

        for (int iter = 0; iter < maxIterations; iter++) {
            for (int i = 0; i < particleCount; i++) {
                for (int j = 0; j < buildings.size(); j++) {
                    double r1 = Math.random();
                    double r2 = Math.random();
                    velocities.get(i)[j] = w * velocities.get(i)[j] +
                        c1 * r1 * (bestPositions.get(i)[j] - particles.get(i)[j]) +
                        c2 * r2 * (globalBest[j] - particles.get(i)[j]);
                    particles.get(i)[j] += velocities.get(i)[j];
                    particles.get(i)[j] = Math.max(0, Math.min(1, particles.get(i)[j]));
                }

                double fitness = calculateFitness(particles.get(i), buildings);
                if (fitness < calculateFitness(bestPositions.get(i), buildings)) {
                    bestPositions.set(i, particles.get(i).clone());
                }
                if (fitness < globalBestFitness) {
                    globalBestFitness = fitness;
                    globalBest = particles.get(i).clone();
                }
            }
        }

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

    private List<double[]> initializePopulation(int populationSize, int geneCount) {
        List<double[]> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            double[] individual = new double[geneCount];
            for (int j = 0; j < geneCount; j++) {
                individual[j] = Math.random();
            }
            population.add(individual);
        }
        return population;
    }

    private double calculateFitness(double[] solution, List<Map<String, Object>> buildings) {
        double totalDeviation = 0;
        for (int i = 0; i < solution.length && i < buildings.size(); i++) {
            double targetTemp = ((Number) buildings.get(i).getOrDefault("targetTemp", 20)).doubleValue();
            double currentFlow = ((Number) buildings.get(i).getOrDefault("flow", 100)).doubleValue();
            double ratio = solution[i];
            double expectedFlow = currentFlow * ratio;
            double deviation = Math.abs(expectedFlow - targetTemp * 5);
            totalDeviation += deviation;
        }
        return totalDeviation;
    }

    private List<double[]> evolvePopulation(List<double[]> population, List<Map<String, Object>> buildings,
                                            double crossoverRate, double mutationRate) {
        List<double[]> newPopulation = new ArrayList<>();
        
        Collections.sort(population, (a, b) -> 
            Double.compare(calculateFitness(a, buildings), calculateFitness(b, buildings)));
        
        int eliteCount = population.size() / 10;
        for (int i = 0; i < eliteCount; i++) {
            newPopulation.add(population.get(i).clone());
        }

        while (newPopulation.size() < population.size()) {
            double[] parent1 = tournamentSelection(population, buildings);
            double[] parent2 = tournamentSelection(population, buildings);
            double[] offspring;
            
            if (Math.random() < crossoverRate) {
                offspring = crossover(parent1, parent2);
            } else {
                offspring = parent1.clone();
            }
            
            if (Math.random() < mutationRate) {
                mutate(offspring);
            }
            
            newPopulation.add(offspring);
        }
        
        return newPopulation;
    }

    private double[] tournamentSelection(List<double[]> population, List<Map<String, Object>> buildings) {
        int k = 3;
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

    private void mutate(double[] individual) {
        int mutationPoint = (int) (Math.random() * individual.length);
        individual[mutationPoint] = Math.random();
    }

    private Map<String, Double> buildValveRatios(List<Map<String, Object>> buildings, double[] solution) {
        Map<String, Double> ratios = new HashMap<>();
        for (int i = 0; i < buildings.size() && i < solution.length; i++) {
            String buildingCode = (String) buildings.get(i).get("buildingCode");
            ratios.put(buildingCode, solution[i]);
        }
        return ratios;
    }
}
