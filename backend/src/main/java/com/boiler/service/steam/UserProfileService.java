package com.boiler.service.steam;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boiler.entity.steam.UserProfile;
import com.boiler.mapper.steam.UserProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfileService extends ServiceImpl<UserProfileMapper, UserProfile> {

    public UserProfile getByEnterpriseId(String enterpriseId) {
        return this.getOne(new LambdaQueryWrapper<UserProfile>()
                .eq(UserProfile::getEnterpriseId, enterpriseId));
    }

    public boolean saveOrUpdateProfile(UserProfile profile) {
        UserProfile existing = getByEnterpriseId(profile.getEnterpriseId());
        if (existing != null) {
            profile.setId(existing.getId());
            profile.setLastUpdated(LocalDateTime.now());
            return this.updateById(profile);
        }
        profile.setLastUpdated(LocalDateTime.now());
        return this.save(profile);
    }

    public List<UserProfile> listAll() {
        return this.list(new LambdaQueryWrapper<UserProfile>()
                .orderByDesc(UserProfile::getLastUpdated));
    }

    public boolean updateProfileScore(String enterpriseId, Integer score) {
        UserProfile profile = getByEnterpriseId(enterpriseId);
        if (profile != null) {
            profile.setProfileScore(score);
            profile.setLastUpdated(LocalDateTime.now());
            return this.updateById(profile);
        }
        return false;
    }
}
