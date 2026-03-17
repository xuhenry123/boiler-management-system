package com.boiler.mapper.cost;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boiler.entity.cost.CarbonFactor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDate;

/**
 * 碳排放因子数据访问层接口
 * 继承MyBatis Plus的BaseMapper，提供对carbon_factor表的基本增删改查操作
 * 同时提供自定义查询方法
 */
@Mapper
public interface CarbonFactorMapper extends BaseMapper<CarbonFactor> {
    
    /**
     * 查询指定日期生效的碳排放因子
     * @param energyType 能源类型
     * @param effectiveDate 生效日期
     * @return 碳排放因子记录
     */
    CarbonFactor selectEffectiveFactor(
            @Param("energyType") String energyType,
            @Param("effectiveDate") LocalDate effectiveDate);
}
