package com.boiler.service.steam;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boiler.entity.steam.EnergyConsumption;
import com.boiler.mapper.steam.EnergyConsumptionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnergyConsumptionService extends ServiceImpl<EnergyConsumptionMapper, EnergyConsumption> {

    public boolean saveOrUpdate(EnergyConsumption consumption) {
        LambdaQueryWrapper<EnergyConsumption> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EnergyConsumption::getEnterpriseId, consumption.getEnterpriseId())
                .eq(EnergyConsumption::getRecordDate, consumption.getRecordDate());
        EnergyConsumption existing = this.getOne(wrapper);
        if (existing != null) {
            consumption.setId(existing.getId());
            return this.updateById(consumption);
        }
        return this.save(consumption);
    }

    public EnergyConsumption getByEnterpriseAndDate(String enterpriseId, LocalDate date) {
        return this.getOne(new LambdaQueryWrapper<EnergyConsumption>()
                .eq(EnergyConsumption::getEnterpriseId, enterpriseId)
                .eq(EnergyConsumption::getRecordDate, date));
    }

    public List<EnergyConsumption> listByEnterpriseAndDateRange(String enterpriseId, LocalDate startDate, LocalDate endDate) {
        return this.list(new LambdaQueryWrapper<EnergyConsumption>()
                .eq(EnergyConsumption::getEnterpriseId, enterpriseId)
                .between(EnergyConsumption::getRecordDate, startDate, endDate)
                .orderByAsc(EnergyConsumption::getRecordDate));
    }

    public List<EnergyConsumption> listDailyReport(LocalDate date) {
        return this.list(new LambdaQueryWrapper<EnergyConsumption>()
                .eq(EnergyConsumption::getRecordDate, date));
    }

    public List<EnergyConsumption> listMonthlyReport(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        return this.list(new LambdaQueryWrapper<EnergyConsumption>()
                .between(EnergyConsumption::getRecordDate, startDate, endDate)
                .orderByAsc(EnergyConsumption::getRecordDate));
    }
}
