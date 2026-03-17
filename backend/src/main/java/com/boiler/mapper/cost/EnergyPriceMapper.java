package com.boiler.mapper.cost;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boiler.entity.cost.EnergyPrice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDate;
import java.util.List;

/**
 * 能源价格数据访问层接口
 * 继承MyBatis Plus的BaseMapper，提供对energy_price表的基本增删改查操作
 * 同时提供自定义查询方法
 */
@Mapper
public interface EnergyPriceMapper extends BaseMapper<EnergyPrice> {
    
    /**
     * 查询指定日期生效的价格
     * @param energyType 能源类型
     * @param effectiveDate 生效日期
     * @return 生效的价格记录
     */
    EnergyPrice selectEffectivePrice(
            @Param("energyType") String energyType,
            @Param("effectiveDate") LocalDate effectiveDate);
    
    /**
     * 查询指定日期范围内的价格记录
     * @param energyType 能源类型
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 价格记录列表
     */
    List<EnergyPrice> selectByDateRange(
            @Param("energyType") String energyType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
