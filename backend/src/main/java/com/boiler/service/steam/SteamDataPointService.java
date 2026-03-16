package com.boiler.service.steam;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boiler.entity.steam.SteamDataPoint;
import com.boiler.mapper.steam.SteamDataPointMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SteamDataPointService extends ServiceImpl<SteamDataPointMapper, SteamDataPoint> {

    public boolean saveDataPoint(SteamDataPoint dataPoint) {
        return this.save(dataPoint);
    }

    public boolean batchSave(List<SteamDataPoint> dataPoints) {
        return this.saveBatch(dataPoints);
    }

    public SteamDataPoint getLatestByBoilerId(String boilerId) {
        return this.getOne(new LambdaQueryWrapper<SteamDataPoint>()
                .eq(SteamDataPoint::getBoilerId, boilerId)
                .orderByDesc(SteamDataPoint::getDataTime)
                .last("LIMIT 1"));
    }

    public List<SteamDataPoint> listByBoilerIdAndTimeRange(String boilerId, LocalDateTime startTime, LocalDateTime endTime) {
        return this.list(new LambdaQueryWrapper<SteamDataPoint>()
                .eq(SteamDataPoint::getBoilerId, boilerId)
                .between(SteamDataPoint::getDataTime, startTime, endTime)
                .orderByAsc(SteamDataPoint::getDataTime));
    }

    public List<SteamDataPoint> listLatestByBoilerIds(List<String> boilerIds, Integer limit) {
        if (boilerIds == null || boilerIds.isEmpty()) {
            return List.of();
        }
        return this.list(new LambdaQueryWrapper<SteamDataPoint>()
                .in(SteamDataPoint::getBoilerId, boilerIds)
                .orderByDesc(SteamDataPoint::getDataTime)
                .last("LIMIT " + limit));
    }
}
