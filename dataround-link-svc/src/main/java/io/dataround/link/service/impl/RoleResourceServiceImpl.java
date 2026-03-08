/*
 * Copyright (C) 2025 yuehan124@gmail.com
 *
 * This repository is licensed under the Dataround Open Source License
 */

package io.dataround.link.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.dataround.link.entity.RoleResource;
import io.dataround.link.mapper.RoleResourceMapper;
import io.dataround.link.service.RoleResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Role-Resource service impl
 * 
 * @author yuehan124@gmail.com
 * @since 2025/02/19
 */
@Service
public class RoleResourceServiceImpl extends ServiceImpl<RoleResourceMapper, RoleResource> implements RoleResourceService {
    @Autowired
    private RoleResourceMapper roleResourceMapper;

    @Override
    public List<Long> getResourceIdsByRoleId(Long roleId) {
        LambdaQueryWrapper<RoleResource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleResource::getRoleId, roleId);
        wrapper.select(RoleResource::getResourceId);
        List<RoleResource> list = list(wrapper);
        return list.stream().map(RoleResource::getResourceId).toList();
    }

    @Override
    @Transactional
    public void assignResourcesToRole(Long roleId, List<Long> resourceIds) {
        // Remove existing resources first
        removeRoleResources(roleId);
        
        // Add new resources
        if (resourceIds != null && !resourceIds.isEmpty()) {
            List<RoleResource> roleResources = new ArrayList<>();
            Date now = new Date();
            for (Long resourceId : resourceIds) {
                RoleResource rr = new RoleResource();
                rr.setRoleId(roleId);
                rr.setResourceId(resourceId);
                rr.setCreateTime(now);
                roleResources.add(rr);
            }
            saveBatch(roleResources);
        }
    }

    @Override
    public void removeRoleResources(Long roleId) {
        LambdaQueryWrapper<RoleResource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleResource::getRoleId, roleId);
        remove(wrapper);
    }
}
