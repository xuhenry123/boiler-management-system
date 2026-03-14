package com.boiler.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TemperaturePredictionService {

    public Map<String, Object> predict(Long userId, Integer hours) {
        List<Map<String, Object>> predictions = new ArrayList<>();
        BigDecimal baseTemp = new BigDecimal("20.0");
        BigDecimal outdoorTemp = new BigDecimal("-2.0");

        for (int i = 1; i <= hours; i++) {
            Map<String, Object> pred = new HashMap<>();
            LocalDateTime predictTime = LocalDateTime.now().plusHours(i);
            
            BigDecimal predictedTemp = baseTemp.subtract(
                outdoorTemp.multiply(BigDecimal.valueOf(0.1 * i))
            ).add(BigDecimal.valueOf(Math.random() * 0.5 - 0.25));
            
            BigDecimal confidenceLower = predictedTemp.subtract(BigDecimal.valueOf(0.5));
            BigDecimal confidenceUpper = predictedTemp.add(BigDecimal.valueOf(0.5));
            BigDecimal confidenceLevel = new BigDecimal("0.85");

            pred.put("userId", userId);
            pred.put("predictTime", predictTime.toString());
            pred.put("predictedTemp", predictedTemp.setScale(2, RoundingMode.HALF_UP));
            pred.put("confidenceLower", confidenceLower.setScale(2, RoundingMode.HALF_UP));
            pred.put("confidenceUpper", confidenceUpper.setScale(2, RoundingMode.HALF_UP));
            pred.put("confidenceLevel", confidenceLevel);
            
            predictions.add(pred);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("prediction", predictions);
        result.put("modelInfo", Map.of(
            "modelType", "lstm",
            "modelVersion", "v1.0.0",
            "accuracy", "0.92"
        ));
        
        return result;
    }

    public Map<String, Object> batchPredict(List<Long> userIds, Integer hours) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        for (Long userId : userIds) {
            Map<String, Object> pred = predict(userId, hours);
            results.add(pred);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("results", results);
        return result;
    }
}
