package com.boiler.service.cost;

import com.boiler.dto.cost.CostResult;
import com.boiler.dto.cost.RankItem;
import com.boiler.dto.cost.StatisticsResult;
import com.boiler.entity.cost.CarbonFactor;
import com.boiler.entity.cost.CostSummary;
import com.boiler.entity.cost.EnergyConsumption;
import com.boiler.entity.cost.EnergyPrice;
import com.boiler.entity.cost.EfficiencyMetrics;
import com.boiler.mapper.cost.CarbonFactorMapper;
import com.boiler.mapper.cost.CostSummaryMapper;
import com.boiler.mapper.cost.EnergyConsumptionMapper;
import com.boiler.mapper.cost.EnergyPriceMapper;
import com.boiler.mapper.cost.EfficiencyMetricsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CostAnalysisService {

    private final EnergyConsumptionMapper energyConsumptionMapper;
    private final EnergyPriceMapper energyPriceMapper;
    private final CarbonFactorMapper carbonFactorMapper;
    private final CostSummaryMapper costSummaryMapper;
    private final EfficiencyMetricsMapper efficiencyMetricsMapper;

    public List<CostResult> getDailyCost(String energyType, LocalDate startDate, LocalDate endDate, List<Long> stationIds) {
        List<CostSummary> summaries = costSummaryMapper.selectByDateRange(startDate, endDate, stationIds, "DAILY");
        return convertToCostResults(summaries);
    }

    public List<CostResult> getMonthlyCost(String energyType, Integer year, Integer month, List<Long> stationIds) {
        LocalDate startDate = YearMonth.of(year, month).atDay(1);
        LocalDate endDate = YearMonth.of(year, month).atEndOfMonth();
        List<CostSummary> summaries = costSummaryMapper.selectByDateRange(startDate, endDate, stationIds, "MONTHLY");
        return convertToCostResults(summaries);
    }

    public List<CostResult> getYearlyCost(String energyType, Integer year, List<Long> stationIds) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        List<CostSummary> summaries = costSummaryMapper.selectByDateRange(startDate, endDate, stationIds, "YEARLY");
        return convertToCostResults(summaries);
    }

    public Map<String, Object> getCostTrend(String energyType, LocalDate startDate, LocalDate endDate, List<Long> stationIds) {
        List<CostSummary> dailySummaries = costSummaryMapper.selectByDateRange(startDate, endDate, stationIds, "DAILY");
        
        Map<String, List<CostSummary>> groupedByDate = dailySummaries.stream()
                .collect(Collectors.groupingBy(s -> s.getStatDate().toString()));
        
        List<String> dates = new ArrayList<>();
        List<BigDecimal> costs = new ArrayList<>();
        
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            String dateStr = current.toString();
            dates.add(dateStr);
            BigDecimal dayCost = groupedByDate.getOrDefault(dateStr, Collections.emptyList())
                    .stream()
                    .map(CostSummary::getTotalCost)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            costs.add(dayCost);
            current = current.plusDays(1);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("dates", dates);
        result.put("costs", costs);
        return result;
    }

    public Map<String, Object> getMultiEnergySummary(LocalDate startDate, LocalDate endDate, List<Long> stationIds) {
        List<CostSummary> summaries = costSummaryMapper.selectByDateRange(startDate, endDate, stationIds, "DAILY");
        
        Map<String, List<CostSummary>> groupedByType = summaries.stream()
                .collect(Collectors.groupingBy(CostSummary::getEnergyType));
        
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> energyList = new ArrayList<>();
        
        for (Map.Entry<String, List<CostSummary>> entry : groupedByType.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("energyType", entry.getKey());
            BigDecimal totalConsumption = entry.getValue().stream()
                    .map(CostSummary::getConsumption)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalCost = entry.getValue().stream()
                    .map(CostSummary::getTotalCost)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            item.put("consumption", totalConsumption);
            item.put("totalCost", totalCost);
            energyList.add(item);
        }
        
        result.put("energies", energyList);
        return result;
    }

    public StatisticsResult getEfficiencyStatistics(LocalDate startDate, LocalDate endDate, List<Long> stationIds) {
        List<EfficiencyMetrics> metrics = efficiencyMetricsMapper.selectByDateRange(startDate, endDate, stationIds);
        
        StatisticsResult result = new StatisticsResult();
        
        Map<Long, List<EfficiencyMetrics>> groupedByStation = metrics.stream()
                .collect(Collectors.groupingBy(EfficiencyMetrics::getStationId));
        
        List<StatisticsResult.StationStatItem> stationItems = new ArrayList<>();
        BigDecimal totalConsumption = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalCarbon = BigDecimal.ZERO;
        BigDecimal totalEfficiency = BigDecimal.ZERO;
        int count = 0;
        
        for (Map.Entry<Long, List<EfficiencyMetrics>> entry : groupedByStation.entrySet()) {
            StatisticsResult.StationStatItem item = new StatisticsResult.StationStatItem();
            item.setStationId(entry.getKey());
            item.setConsumption(entry.getValue().stream()
                    .map(EfficiencyMetrics::getEnergyConsumption)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            item.setCost(entry.getValue().stream()
                    .map(EfficiencyMetrics::getCostPerUnit)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            item.setCarbon(entry.getValue().stream()
                    .map(EfficiencyMetrics::getCarbonPerUnit)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            item.setEfficiency(entry.getValue().stream()
                    .map(EfficiencyMetrics::getEfficiencyValue)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(BigDecimal.ZERO));
            
            if (!entry.getValue().isEmpty()) {
                item.setStationName(entry.getValue().get(0).getStationName());
            }
            stationItems.add(item);
            
            totalConsumption = totalConsumption.add(item.getConsumption());
            totalCost = totalCost.add(item.getCost());
            totalCarbon = totalCarbon.add(item.getCarbon());
            totalEfficiency = totalEfficiency.add(item.getEfficiency());
            count++;
        }
        
        result.setStations(stationItems);
        result.setTotalConsumption(totalConsumption);
        result.setTotalCost(totalCost);
        result.setTotalCarbon(totalCarbon);
        result.setAverageEfficiency(count > 0 ? totalEfficiency.divide(BigDecimal.valueOf(count), 4, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        
        return result;
    }

    public StatisticsResult getConsumptionStatistics(LocalDate startDate, LocalDate endDate, List<Long> stationIds) {
        List<CostSummary> summaries = costSummaryMapper.selectByDateRange(startDate, endDate, stationIds, "DAILY");
        
        StatisticsResult result = new StatisticsResult();
        
        Map<Long, List<CostSummary>> groupedByStation = summaries.stream()
                .collect(Collectors.groupingBy(CostSummary::getStationId));
        
        List<StatisticsResult.StationStatItem> stationItems = new ArrayList<>();
        BigDecimal totalConsumption = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        
        for (Map.Entry<Long, List<CostSummary>> entry : groupedByStation.entrySet()) {
            StatisticsResult.StationStatItem item = new StatisticsResult.StationStatItem();
            item.setStationId(entry.getKey());
            BigDecimal consumption = entry.getValue().stream()
                    .map(CostSummary::getConsumption)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal cost = entry.getValue().stream()
                    .map(CostSummary::getTotalCost)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            item.setConsumption(consumption);
            item.setCost(cost);
            item.setCarbon(entry.getValue().stream()
                    .map(CostSummary::getCarbonEmission)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            
            if (!entry.getValue().isEmpty()) {
                item.setStationName(entry.getValue().get(0).getStationName());
            }
            stationItems.add(item);
            
            totalConsumption = totalConsumption.add(consumption);
            totalCost = totalCost.add(cost);
        }
        
        result.setStations(stationItems);
        result.setTotalConsumption(totalConsumption);
        result.setTotalCost(totalCost);
        
        return result;
    }

    public StatisticsResult getCarbonStatistics(LocalDate startDate, LocalDate endDate, List<Long> stationIds) {
        List<CostSummary> summaries = costSummaryMapper.selectByDateRange(startDate, endDate, stationIds, "DAILY");
        
        StatisticsResult result = new StatisticsResult();
        
        Map<Long, List<CostSummary>> groupedByStation = summaries.stream()
                .collect(Collectors.groupingBy(CostSummary::getStationId));
        
        List<StatisticsResult.StationStatItem> stationItems = new ArrayList<>();
        BigDecimal totalCarbon = BigDecimal.ZERO;
        
        for (Map.Entry<Long, List<CostSummary>> entry : groupedByStation.entrySet()) {
            StatisticsResult.StationStatItem item = new StatisticsResult.StationStatItem();
            item.setStationId(entry.getKey());
            BigDecimal carbon = entry.getValue().stream()
                    .map(CostSummary::getCarbonEmission)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            item.setCarbon(carbon);
            
            if (!entry.getValue().isEmpty()) {
                item.setStationName(entry.getValue().get(0).getStationName());
                item.setConsumption(entry.getValue().stream()
                        .map(CostSummary::getConsumption)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
            }
            stationItems.add(item);
            
            totalCarbon = totalCarbon.add(carbon);
        }
        
        result.setStations(stationItems);
        result.setTotalCarbon(totalCarbon);
        
        return result;
    }

    public List<RankItem> getRankByEfficiency(LocalDate period, List<Long> stationIds, Integer topN) {
        LocalDate startDate = period.withDayOfMonth(1);
        LocalDate endDate = period.withDayOfMonth(period.lengthOfMonth());
        List<EfficiencyMetrics> metrics = efficiencyMetricsMapper.selectByDateRange(startDate, endDate, stationIds);
        
        Map<Long, BigDecimal> efficiencyMap = metrics.stream()
                .filter(m -> m.getEfficiencyValue() != null)
                .collect(Collectors.groupingBy(
                        EfficiencyMetrics::getStationId,
                        Collectors.averaging(EfficiencyMetrics::getEfficiencyValue)
                ));
        
        return buildRankItems(efficiencyMap, metrics, "efficiency");
    }

    public List<RankItem> getRankByConsumption(LocalDate period, List<Long> stationIds, Integer topN) {
        LocalDate startDate = period.withDayOfMonth(1);
        LocalDate endDate = period.withDayOfMonth(period.lengthOfMonth());
        List<CostSummary> summaries = costSummaryMapper.selectByDateRange(startDate, endDate, stationIds, "MONTHLY");
        
        Map<Long, BigDecimal> consumptionMap = summaries.stream()
                .filter(s -> s.getConsumption() != null)
                .collect(Collectors.groupingBy(
                        CostSummary::getStationId,
                        Collectors.reducing(BigDecimal.ZERO, CostSummary::getConsumption, BigDecimal::add)
                ));
        
        return buildRankItems(consumptionMap, summaries, "consumption");
    }

    public List<RankItem> getRankByCost(LocalDate period, List<Long> stationIds, Integer topN) {
        LocalDate startDate = period.withDayOfMonth(1);
        LocalDate endDate = period.withDayOfMonth(period.lengthOfMonth());
        List<CostSummary> summaries = costSummaryMapper.selectByDateRange(startDate, endDate, stationIds, "MONTHLY");
        
        Map<Long, BigDecimal> costMap = summaries.stream()
                .filter(s -> s.getTotalCost() != null)
                .collect(Collectors.groupingBy(
                        CostSummary::getStationId,
                        Collectors.reducing(BigDecimal.ZERO, CostSummary::getTotalCost, BigDecimal::add)
                ));
        
        return buildRankItems(costMap, summaries, "cost");
    }

    public List<RankItem> getRankByCarbon(LocalDate period, List<Long> stationIds, Integer topN) {
        LocalDate startDate = period.withDayOfMonth(1);
        LocalDate endDate = period.withDayOfMonth(period.lengthOfMonth());
        List<CostSummary> summaries = costSummaryMapper.selectByDateRange(startDate, endDate, stationIds, "MONTHLY");
        
        Map<Long, BigDecimal> carbonMap = summaries.stream()
                .filter(s -> s.getCarbonEmission() != null)
                .collect(Collectors.groupingBy(
                        CostSummary::getStationId,
                        Collectors.reducing(BigDecimal.ZERO, CostSummary::getCarbonEmission, BigDecimal::add)
                ));
        
        return buildRankItems(carbonMap, summaries, "carbon");
    }

    private List<CostResult> convertToCostResults(List<CostSummary> summaries) {
        return summaries.stream().map(s -> {
            CostResult result = new CostResult();
            result.setStationId(s.getStationId());
            result.setStationName(s.getStationName());
            result.setEnergyType(s.getEnergyType());
            result.setConsumption(s.getConsumption());
            result.setUnitPrice(s.getUnitPrice());
            result.setTotalCost(s.getTotalCost());
            result.setDataTime(s.getStatDate() != null ? s.getStatDate().atStartOfDay() : LocalDateTime.now());
            return result;
        }).collect(Collectors.toList());
    }

    private <T> List<RankItem> buildRankItems(Map<Long, BigDecimal> valueMap, List<T> dataList, String type) {
        List<RankItem> rankItems = new ArrayList<>();
        
        for (Map.Entry<Long, BigDecimal> entry : valueMap.entrySet()) {
            RankItem item = new RankItem();
            item.setStationId(entry.getKey());
            item.setValue(entry.getValue());
            
            String stationName = dataList.stream()
                    .filter(d -> {
                        if (d instanceof CostSummary) return ((CostSummary) d).getStationId().equals(entry.getKey());
                        if (d instanceof EfficiencyMetrics) return ((EfficiencyMetrics) d).getStationId().equals(entry.getKey());
                        return false;
                    })
                    .map(d -> {
                        if (d instanceof CostSummary) return ((CostSummary) d).getStationName();
                        if (d instanceof EfficiencyMetrics) return ((EfficiencyMetrics) d).getStationName();
                        return "";
                    })
                    .filter(StringUtils::hasText)
                    .findFirst()
                    .orElse("站点" + entry.getKey());
            
            item.setStationName(stationName);
            item.setTrend(BigDecimal.ZERO);
            rankItems.add(item);
        }
        
        rankItems.sort((a, b) -> {
            if ("efficiency".equals(type)) {
                return b.getValue().compareTo(a.getValue());
            } else {
                return a.getValue().compareTo(b.getValue());
            }
        });
        
        List<RankItem> result = new ArrayList<>();
        int rank = 1;
        for (int i = 0; i < rankItems.size(); i++) {
            if (i > 0 && rankItems.get(i).getValue().compareTo(rankItems.get(i - 1).getValue()) != 0) {
                rank = i + 1;
            }
            RankItem item = rankItems.get(i);
            item.setRank(rank);
            result.add(item);
        }
        
        return result;
    }
}
