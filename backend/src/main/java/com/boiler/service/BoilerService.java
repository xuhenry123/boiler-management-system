package com.boiler.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boiler.entity.Boiler;
import com.boiler.mapper.BoilerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 锅炉设备业务逻辑层
 * 负责锅炉设备相关的业务逻辑处理
 */
@Service
@RequiredArgsConstructor
public class BoilerService {

    private final BoilerMapper boilerMapper;

    /**
     * 分页查询锅炉列表
     */
    public Page<Boiler> listBoilers(Integer page, Integer size, String boilerName) {
        Page<Boiler> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Boiler> wrapper = new LambdaQueryWrapper<>();
        if (boilerName != null && !boilerName.isEmpty()) {
            wrapper.like(Boiler::getBoilerName, boilerName);
        }
        wrapper.orderByDesc(Boiler::getId);
        return boilerMapper.selectPage(pageParam, wrapper);
    }

    /**
     * 根据ID查询锅炉
     */
    public Boiler getBoilerById(Long id) {
        return boilerMapper.selectById(id);
    }

    /**
     * 查询所有锅炉
     */
    public List<Boiler> listAllBoilers() {
        return boilerMapper.selectList(null);
    }

    /**
     * 保存锅炉
     */
    public boolean saveBoiler(Boiler boiler) {
        return boilerMapper.insert(boiler) > 0;
    }

    /**
     * 更新锅炉
     */
    public boolean updateBoiler(Boiler boiler) {
        return boilerMapper.updateById(boiler) > 0;
    }

    /**
     * 删除锅炉
     */
    public boolean deleteBoiler(Long id) {
        return boilerMapper.deleteById(id) > 0;
    }
}
