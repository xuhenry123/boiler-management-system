package com.boiler.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boiler.entity.HeatUser;
import com.boiler.mapper.HeatUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HeatUserService {

    private final HeatUserMapper heatUserMapper;

    public Page<HeatUser> listUsers(Integer page, Integer size, Long buildingId, String userName) {
        Page<HeatUser> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<HeatUser> wrapper = new LambdaQueryWrapper<>();
        if (buildingId != null) {
            wrapper.eq(HeatUser::getBuildingId, buildingId);
        }
        if (StringUtils.hasText(userName)) {
            wrapper.like(HeatUser::getUserName, userName);
        }
        wrapper.eq(HeatUser::getStatus, 1);
        wrapper.orderByDesc(HeatUser::getId);
        return heatUserMapper.selectPage(pageParam, wrapper);
    }

    public HeatUser getUserById(Long id) {
        return heatUserMapper.selectById(id);
    }

    public List<HeatUser> listUsersByBuildingId(Long buildingId) {
        LambdaQueryWrapper<HeatUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HeatUser::getBuildingId, buildingId);
        wrapper.eq(HeatUser::getStatus, 1);
        return heatUserMapper.selectList(wrapper);
    }

    public boolean saveUser(HeatUser user) {
        return heatUserMapper.insert(user) > 0;
    }

    public boolean updateUser(HeatUser user) {
        return heatUserMapper.updateById(user) > 0;
    }

    public boolean deleteUser(Long id) {
        return heatUserMapper.deleteById(id) > 0;
    }
}
