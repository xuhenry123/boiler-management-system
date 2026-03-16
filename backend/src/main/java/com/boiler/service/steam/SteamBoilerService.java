package com.boiler.service.steam;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boiler.entity.steam.SteamBoiler;
import com.boiler.mapper.steam.SteamBoilerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SteamBoilerService extends ServiceImpl<SteamBoilerMapper, SteamBoiler> {

    public Page<SteamBoiler> listBoilers(Integer page, Integer size, String enterpriseId, String status) {
        Page<SteamBoiler> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<SteamBoiler> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(enterpriseId)) {
            wrapper.eq(SteamBoiler::getEnterpriseId, enterpriseId);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(SteamBoiler::getStatus, status);
        }
        wrapper.orderByDesc(SteamBoiler::getCreateTime);
        return this.page(pageParam, wrapper);
    }

    public List<SteamBoiler> listByEnterpriseId(String enterpriseId) {
        return this.list(new LambdaQueryWrapper<SteamBoiler>()
                .eq(SteamBoiler::getEnterpriseId, enterpriseId));
    }

    public SteamBoiler getByBoilerId(String id) {
        return this.getById(id);
    }

    public boolean saveBoiler(SteamBoiler boiler) {
        return this.save(boiler);
    }

    public boolean updateBoiler(SteamBoiler boiler) {
        return this.updateById(boiler);
    }

    public boolean deleteBoiler(String id) {
        return this.removeById(id);
    }

    public Long countByEnterprise(String enterpriseId) {
        return this.count(new LambdaQueryWrapper<SteamBoiler>()
                .eq(SteamBoiler::getEnterpriseId, enterpriseId));
    }
}
