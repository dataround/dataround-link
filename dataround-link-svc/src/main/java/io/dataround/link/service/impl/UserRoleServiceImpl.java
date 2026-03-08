/*
 * Copyright (C) 2025 yuehan124@gmail.com
 *
 * This repository is licensed under the Dataround Open Source License
 */

package io.dataround.link.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.dataround.link.entity.UserRole;
import io.dataround.link.mapper.UserRoleMapper;
import io.dataround.link.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User-Role service impl
 * 
 * @author yuehan124@gmail.com
 * @since 2025/02/19
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {
    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public List<Long> getRoleIdsByUserId(Long userId) {
        return userRoleMapper.selectRoleIdsByUserId(userId);
    }

    @Override
    @Transactional
    public void assignRolesToUser(Long userId, List<Long> roleIds) {
        // Remove existing roles first
        removeUserRoles(userId);
        
        // Add new roles
        if (roleIds != null && !roleIds.isEmpty()) {
            List<UserRole> userRoles = new ArrayList<>();
            Date now = new Date();
            for (Long roleId : roleIds) {
                UserRole ur = new UserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                ur.setCreateTime(now);
                userRoles.add(ur);
            }
            saveBatch(userRoles);
        }
    }

    @Override
    public void removeUserRoles(Long userId) {
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        remove(wrapper);
    }
}
