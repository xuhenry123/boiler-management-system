package com.boiler.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boiler.entity.ValveDevice;
import com.boiler.mapper.ValveDeviceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 阀门设备业务逻辑层
 * 负责阀门设备相关的业务逻辑处理
 */
@Service
@RequiredArgsConstructor
public class ValveService {

    private final ValveDeviceMapper valveDeviceMapper;

    /**
     * 分页查询阀门设备列表
     */
    public Page<ValveDevice> listValves(Integer page, Integer size, String valveName, Long nodeId) {
        Page<ValveDevice> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<ValveDevice> wrapper = new LambdaQueryWrapper<>();
        if (valveName != null && !valveName.isEmpty()) {
            wrapper.like(ValveDevice::getValveName, valveName);
        }
        if (nodeId != null) {
            wrapper.eq(ValveDevice::getNodeId, nodeId);
        }
        wrapper.orderByDesc(ValveDevice::getId);
        return valveDeviceMapper.selectPage(pageParam, wrapper);
    }

    /**
     * 根据ID查询阀门设备
     */
    public ValveDevice getValveById(Long id) {
        return valveDeviceMapper.selectById(id);
    }

    /**
     * 根据节点ID查询阀门列表
     */
    public List<ValveDevice> listValvesByNodeId(Long nodeId) {
        LambdaQueryWrapper<ValveDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ValveDevice::getNodeId, nodeId);
        wrapper.eq(ValveDevice::getStatus, 1);
        return valveDeviceMapper.selectList(wrapper);
    }

    /**
     * 保存阀门设备
     */
    public boolean saveValve(ValveDevice valve) {
        return valveDeviceMapper.insert(valve) > 0;
    }

    /**
     * 更新阀门设备
     */
    public boolean updateValve(ValveDevice valve) {
        return valveDeviceMapper.updateById(valve) > 0;
    }

    /**
     * 删除阀门设备
     */
    public boolean deleteValve(Long id) {
        return valveDeviceMapper.deleteById(id) > 0;
    }

    /**
     * 控制阀门开度
     */
    public boolean controlValve(Long id, Double openRatio) {
        ValveDevice valve = new ValveDevice();
        valve.setId(id);
        valve.setOpenRatio(new java.math.BigDecimal(openRatio));
        return valveDeviceMapper.updateById(valve) > 0;
    }
}
