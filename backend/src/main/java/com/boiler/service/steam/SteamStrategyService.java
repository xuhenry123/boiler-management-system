package com.boiler.service.steam;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boiler.entity.steam.SteamStrategy;
import com.boiler.mapper.steam.SteamStrategyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SteamStrategyService extends ServiceImpl<SteamStrategyMapper, SteamStrategy> {

    public SteamStrategy getByBoilerId(String boilerId) {
        return this.getOne(new LambdaQueryWrapper<SteamStrategy>()
                .eq(SteamStrategy::getBoilerId, boilerId)
                .orderByDesc(SteamStrategy::getCreateTime)
                .last("LIMIT 1"));
    }

    public List<SteamStrategy> listByBoilerId(String boilerId) {
        return this.list(new LambdaQueryWrapper<SteamStrategy>()
                .eq(SteamStrategy::getBoilerId, boilerId)
                .orderByDesc(SteamStrategy::getCreateTime));
    }

    public boolean saveStrategy(SteamStrategy strategy) {
        return this.save(strategy);
    }

    public boolean updateStrategy(SteamStrategy strategy) {
        return this.updateById(strategy);
    }

    public boolean activateStrategy(String id) {
        SteamStrategy strategy = this.getById(id);
        if (strategy != null) {
            strategy.setStatus("active");
            return this.updateById(strategy);
        }
        return false;
    }

    public boolean suspendStrategy(String id) {
        SteamStrategy strategy = this.getById(id);
        if (strategy != null) {
            strategy.setStatus("suspended");
            return this.updateById(strategy);
        }
        return false;
    }
}
