/*
 * Copyright (C) 2025 yuehan124@gmail.com
 *
 * This repository is licensed under the Dataround Open Source License
 */

package io.dataround.link.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.dataround.link.entity.Role;
import io.dataround.link.mapper.RoleMapper;
import io.dataround.link.mapper.UserRoleMapper;
import io.dataround.link.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Role service impl
 * 
 * @author yuehan124@gmail.com
 * @since 2025/09/21
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public List<Role> getRolesByUserId(Long userId) {
        return userRoleMapper.selectRolesByUserId(userId);
    }
}
