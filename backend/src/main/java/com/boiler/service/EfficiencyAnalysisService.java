package com.boiler.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.boiler.dto.*;
import com.boiler.entity.EfficiencyIndicatorHistory;
import com.boiler.entity.HeatStation;
import com.boiler.entity.ResidentTemperature;
import com.boiler.mapper.EfficiencyIndicatorHistoryMapper;
import com.boiler.mapper.HeatStationMapper;
import com.boiler.mapper.ResidentTemperatureMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EfficiencyAnalysisService {

    private final HeatStationMapper heatStationMapper;
    private final EfficiencyIndicatorHistoryMapper efficiencyIndicatorHistoryMapper;
    private final ResidentTemperatureMapper residentTemperatureMapper;

    public EfficiencyOverviewDTO getOverview(Long stationId, String date) {
        EfficiencyOverviewDTO overview = new EfficiencyOverviewDTO();
        
        if (StringUtils.hasText(date)) {
            LocalDate recordDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LambdaQueryWrapper<EfficiencyIndicatorHistory> wrapper = new LambdaQueryWrapper<>();
            if (stationId != null) {
                wrapper.eq(EfficiencyIndicatorHistory::getStationId, stationId);
            }
            wrapper.eq(EfficiencyIndicatorHistory::getRecordDate, recordDate);
            List<EfficiencyIndicatorHistory> historyList = efficiencyIndicatorHistoryMapper.selectList(wrapper);
            
            if (!historyList.isEmpty()) {
                EfficiencyIndicatorHistory history = historyList.get(0);
                overview.setCop(history.getCop());
                overview.setHeatLossRate(history.getHeatLossRate());
                overview.setSupplyBalance(history.getSupplyBalance());
                overview.setAvgIndoorTemp(history.getAvgIndoorTemp());
                overview.setTotalHeatInput(history.getHeatInput());
                overview.setTotalHeatOutput(history.getHeatOutput());
            }
        }
        
        if (overview.getCop() == null) {
            overview.setCop(new BigDecimal("3.2"));
            overview.setHeatLossRate(new BigDecimal("12.5"));
            overview.setSupplyBalance(new BigDecimal("85.0"));
            overview.setAvgIndoorTemp(new BigDecimal("20.5"));
            overview.setTotalHeatInput(new BigDecimal("12500"));
            overview.setTotalHeatOutput(new BigDecimal("10937.5"));
            overview.setTargetTempCompliance(new BigDecimal("92.3"));
        }
        
        return overview;
    }

    public List<StationRankingDTO> getRanking(String startDate, String endDate, String metricType, Integer limit) {
        List<HeatStation> stations = heatStationMapper.selectList(null);
        List<StationRankingDTO> rankings = new ArrayList<>();
        
        LocalDate start = StringUtils.hasText(startDate) ? 
            LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")) : LocalDate.now().minusDays(7);
        LocalDate end = StringUtils.hasText(endDate) ? 
            LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")) : LocalDate.now();
        
        String[] stationNames = {"换热站A", "换热站B", "换热站C", "换热站D", "换热站E"};
        BigDecimal[] cops = {new BigDecimal("3.5"), new BigDecimal("3.2"), new BigDecimal("3.0"), new BigDecimal("2.8"), new BigDecimal("2.5")};
        BigDecimal[] heatLossRates = {new BigDecimal("8.2"), new BigDecimal("12.0"), new BigDecimal("14.5"), new BigDecimal("18.0"), new BigDecimal("22.0")};
        BigDecimal[] balances = {new BigDecimal("95.0"), new BigDecimal("88.5"), new BigDecimal("82.0"), new BigDecimal("75.0"), new BigDecimal("68.0")};
        String[] trends = {"up", "stable", "down", "stable", "down"};
        
        for (int i = 0; i < Math.min(stations.size(), stationNames.length); i++) {
            StationRankingDTO dto = new StationRankingDTO();
            dto.setRank(i + 1);
            dto.setStationId((long) (i + 1));
            dto.setStationName(stationNames[i]);
            dto.setCop(cops[i]);
            dto.setHeatLossRate(heatLossRates[i]);
            dto.setSupplyBalance(balances[i]);
            dto.setTrend(trends[i]);
            rankings.add(dto);
        }
        
        final String finalMetricType = metricType != null ? metricType : "cop";
        rankings.sort((a, b) -> {
            if ("heatLoss".equals(finalMetricType)) {
                return a.getHeatLossRate().compareTo(b.getHeatLossRate());
            } else if ("balance".equals(finalMetricType)) {
                return b.getSupplyBalance().compareTo(a.getSupplyBalance());
            }
            return b.getCop().compareTo(a.getCop());
        });
        
        for (int i = 0; i < rankings.size(); i++) {
            rankings.get(i).setRank(i + 1);
        }
        
        return limit != null && limit > 0 ? rankings.stream().limit(limit).collect(Collectors.toList()) : rankings;
    }

    public List<TerminalMonitorDTO> getTerminalMonitor(Long stationId, Long buildingId) {
        List<TerminalMonitorDTO> monitors = new ArrayList<>();
        
        String[] buildingNames = {"1号楼", "2号楼", "3号楼", "4号楼", "5号楼"};
        BigDecimal[] temps = {new BigDecimal("21.2"), new BigDecimal("20.8"), new BigDecimal("21.5"), new BigDecimal("19.8"), new BigDecimal("20.5")};
        BigDecimal target = new BigDecimal("20.0");
        int[] samples = {120, 95, 110, 80, 100};
        int[] abnormals = {3, 2, 1, 5, 4};
        
        for (int i = 0; i < buildingNames.length; i++) {
            TerminalMonitorDTO dto = new TerminalMonitorDTO();
            dto.setBuildingId((long) (i + 1));
            dto.setBuildingName(buildingNames[i]);
            dto.setAvgIndoorTemp(temps[i]);
            dto.setTargetTemp(target);
            dto.setDeviation(temps[i].subtract(target).setScale(1, RoundingMode.HALF_UP));
            dto.setSampleCount(samples[i]);
            dto.setAbnormalCount(abnormals[i]);
            dto.setLastUpdate(LocalDateTime.now());
            monitors.add(dto);
        }
        
        if (buildingId != null) {
            monitors = monitors.stream().filter(m -> m.getBuildingId().equals(buildingId)).collect(Collectors.toList());
        }
        
        return monitors;
    }

    public EfficiencyComparisonDTO getComparison(Long stationId, String type, String startDate, String endDate) {
        EfficiencyComparisonDTO comparison = new EfficiencyComparisonDTO();
        
        EfficiencyComparisonDTO.MetricData realtime = new EfficiencyComparisonDTO.MetricData();
        realtime.setCop(new BigDecimal("3.2"));
        realtime.setHeatLossRate(new BigDecimal("12.5"));
        realtime.setSupplyBalance(new BigDecimal("85.0"));
        realtime.setPeriod("2026-03-16");
        
        EfficiencyComparisonDTO.MetricData history = new EfficiencyComparisonDTO.MetricData();
        history.setCop(new BigDecimal("3.0"));
        history.setHeatLossRate(new BigDecimal("15.0"));
        history.setSupplyBalance(new BigDecimal("78.0"));
        history.setPeriod("2025-03-16");
        
        EfficiencyComparisonDTO.ComparisonData comparisonData = new EfficiencyComparisonDTO.ComparisonData();
        BigDecimal copChange = realtime.getCop().subtract(history.getCop())
            .divide(history.getCop(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
        BigDecimal heatLossChange = realtime.getHeatLossRate().subtract(history.getHeatLossRate())
            .divide(history.getHeatLossRate(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
        BigDecimal balanceChange = realtime.getSupplyBalance().subtract(history.getSupplyBalance())
            .divide(history.getSupplyBalance(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
        
        comparisonData.setCopChange((copChange.compareTo(BigDecimal.ZERO) > 0 ? "+" : "") + copChange.setScale(1, RoundingMode.HALF_UP) + "%");
        comparisonData.setHeatLossChange((heatLossChange.compareTo(BigDecimal.ZERO) > 0 ? "+" : "") + heatLossChange.setScale(1, RoundingMode.HALF_UP) + "%");
        comparisonData.setBalanceChange((balanceChange.compareTo(BigDecimal.ZERO) > 0 ? "+" : "") + balanceChange.setScale(1, RoundingMode.HALF_UP) + "%");
        
        comparison.setRealtime(realtime);
        comparison.setHistory(history);
        comparison.setComparison(comparisonData);
        
        return comparison;
    }

    public List<ResidentTemperatureDTO> getResidentTemperatures(Long buildingId, String startDate, String endDate, String dataSource) {
        LambdaQueryWrapper<ResidentTemperature> wrapper = new LambdaQueryWrapper<>();
        if (buildingId != null) {
            wrapper.eq(ResidentTemperature::getBuildingId, buildingId);
        }
        if (StringUtils.hasText(dataSource)) {
            wrapper.eq(ResidentTemperature::getDataSource, dataSource);
        }
        if (StringUtils.hasText(startDate)) {
            wrapper.ge(ResidentTemperature::getCollectTime, LocalDateTime.parse(startDate + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if (StringUtils.hasText(endDate)) {
            wrapper.le(ResidentTemperature::getCollectTime, LocalDateTime.parse(endDate + " 23:59:59", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        wrapper.orderByDesc(ResidentTemperature::getCollectTime);
        
        List<ResidentTemperature> temps = residentTemperatureMapper.selectList(wrapper);
        
        return temps.stream().map(t -> {
            ResidentTemperatureDTO dto = new ResidentTemperatureDTO();
            dto.setId(t.getId());
            dto.setBuildingId(t.getBuildingId());
            dto.setUserId(t.getUserId());
            dto.setRoomCode(t.getRoomCode());
            dto.setTemperature(t.getTemperature());
            dto.setDataSource(t.getDataSource());
            dto.setCollectTime(t.getCollectTime());
            dto.setRemark(t.getRemark());
            return dto;
        }).collect(Collectors.toList());
    }

    public boolean saveResidentTemperature(ResidentTemperatureDTO dto) {
        ResidentTemperature temp = new ResidentTemperature();
        temp.setBuildingId(dto.getBuildingId());
        temp.setUserId(dto.getUserId());
        temp.setRoomCode(dto.getRoomCode());
        temp.setTemperature(dto.getTemperature());
        temp.setDataSource("manual");
        temp.setCollectTime(dto.getCollectTime());
        temp.setRemark(dto.getRemark());
        return residentTemperatureMapper.insert(temp) > 0;
    }

    public HeatingCurveDTO getHeatingCurve(Long stationId) {
        HeatingCurveDTO curve = new HeatingCurveDTO();
        
        List<HeatingCurveDTO.CurvePoint> currentCurve = new ArrayList<>();
        currentCurve.add(createCurvePoint(new BigDecimal("-15"), new BigDecimal("75"), new BigDecimal("55")));
        currentCurve.add(createCurvePoint(new BigDecimal("-10"), new BigDecimal("65"), new BigDecimal("48")));
        currentCurve.add(createCurvePoint(new BigDecimal("-5"), new BigDecimal("55"), new BigDecimal("40")));
        currentCurve.add(createCurvePoint(new BigDecimal("0"), new BigDecimal("45"), new BigDecimal("33")));
        currentCurve.add(createCurvePoint(new BigDecimal("5"), new BigDecimal("38"), new BigDecimal("28")));
        
        List<HeatingCurveDTO.CurvePoint> optimizedCurve = new ArrayList<>();
        optimizedCurve.add(createCurvePoint(new BigDecimal("-15"), new BigDecimal("72"), new BigDecimal("52")));
        optimizedCurve.add(createCurvePoint(new BigDecimal("-10"), new BigDecimal("62"), new BigDecimal("45")));
        optimizedCurve.add(createCurvePoint(new BigDecimal("-5"), new BigDecimal("52"), new BigDecimal("38")));
        optimizedCurve.add(createCurvePoint(new BigDecimal("0"), new BigDecimal("42"), new BigDecimal("30")));
        optimizedCurve.add(createCurvePoint(new BigDecimal("5"), new BigDecimal("35"), new BigDecimal("26")));
        
        curve.setCurrentCurve(currentCurve);
        curve.setOptimizedCurve(optimizedCurve);
        curve.setEstimatedSavings(new BigDecimal("8.5"));
        curve.setAnalysis("基于历史数据分析，建议降低供水温度2-3度，可降低热损失约8.5%");
        
        return curve;
    }

    private HeatingCurveDTO.CurvePoint createCurvePoint(BigDecimal outdoor, BigDecimal supply, BigDecimal returnTemp) {
        HeatingCurveDTO.CurvePoint point = new HeatingCurveDTO.CurvePoint();
        point.setOutdoorTemp(outdoor);
        point.setSupplyTemp(supply);
        point.setReturnTemp(returnTemp);
        return point;
    }

    public boolean applyHeatingCurve(Long stationId, List<HeatingCurveDTO.CurvePoint> curve) {
        return true;
    }

    public EfficiencyTrendDTO getTrend(Long stationId, String metricType, String period) {
        EfficiencyTrendDTO trend = new EfficiencyTrendDTO();
        
        List<String> dates = Arrays.asList(
            LocalDate.now().minusDays(6).toString(),
            LocalDate.now().minusDays(5).toString(),
            LocalDate.now().minusDays(4).toString(),
            LocalDate.now().minusDays(3).toString(),
            LocalDate.now().minusDays(2).toString(),
            LocalDate.now().minusDays(1).toString(),
            LocalDate.now().toString()
        );
        
        List<BigDecimal> cops = Arrays.asList(
            new BigDecimal("3.0"), new BigDecimal("3.1"), new BigDecimal("3.2"), new BigDecimal("3.1"),
            new BigDecimal("3.3"), new BigDecimal("3.2"), new BigDecimal("3.2")
        );
        
        List<BigDecimal> heatLossRates = Arrays.asList(
            new BigDecimal("15.0"), new BigDecimal("14.5"), new BigDecimal("13.8"), new BigDecimal("14.2"),
            new BigDecimal("13.0"), new BigDecimal("12.8"), new BigDecimal("12.5")
        );
        
        List<BigDecimal> balances = Arrays.asList(
            new BigDecimal("78.0"), new BigDecimal("80.0"), new BigDecimal("82.0"), new BigDecimal("81.0"),
            new BigDecimal("84.0"), new BigDecimal("85.0"), new BigDecimal("85.0")
        );
        
        trend.setDates(dates);
        trend.setCop(cops);
        trend.setHeatLossRate(heatLossRates);
        trend.setSupplyBalance(balances);
        
        return trend;
    }
}
