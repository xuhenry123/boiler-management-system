package com.boiler.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boiler.entity.HeatUser;
import com.boiler.mapper.HeatUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 热用户业务逻辑层
 * 负责热用户相关的业务逻辑处理，包括查询、添加、修改、删除等操作
 */
@Service
@RequiredArgsConstructor
public class HeatUserService {

    private final HeatUserMapper heatUserMapper;

    /**
     * 分页查询热用户列表
     * @param page 页码
     * @param size 每页数量
     * @param buildingId 建筑ID
     * @param userName 用户名称（模糊查询）
     * @return 分页结果
     */
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

    /**
     * 根据ID查询热用户
     * @param id 用户ID
     * @return 用户信息
     */
    public HeatUser getUserById(Long id) {
        return heatUserMapper.selectById(id);
    }

    /**
     * 根据建筑ID查询热用户列表
     * @param buildingId 建筑ID
     * @return 用户列表
     */
    public List<HeatUser> listUsersByBuildingId(Long buildingId) {
        LambdaQueryWrapper<HeatUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HeatUser::getBuildingId, buildingId);
        wrapper.eq(HeatUser::getStatus, 1);
        return heatUserMapper.selectList(wrapper);
    }

    /**
     * 新增热用户
     * @param user 用户信息
     * @return 是否成功
     */
    public boolean saveUser(HeatUser user) {
        return heatUserMapper.insert(user) > 0;
    }

    /**
     * 更新热用户信息
     * @param user 用户信息
     * @return 是否成功
     */
    public boolean updateUser(HeatUser user) {
        return heatUserMapper.updateById(user) > 0;
    }

    /**
     * 删除热用户
     * @param id 用户ID
     * @return 是否成功
     */
    public boolean deleteUser(Long id) {
        return heatUserMapper.deleteById(id) > 0;
    }

    /**
     * 查询所有热用户
     * @return 用户列表
     */
    public List<HeatUser> listAllUsers() {
        return heatUserMapper.selectList(null);
    }
}
