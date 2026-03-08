/*
 * Copyright (C) 2025 yuehan124@gmail.com
 *
 * This repository is licensed under the Dataround Open Source License
 */

package io.dataround.link.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.dataround.link.entity.Resource;
import io.dataround.link.mapper.ResourceMapper;
import io.dataround.link.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Resource service impl
 * 
 * @author yuehan124@gmail.com
 * @since 2025/09/21
 */
@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements ResourceService {
    @Autowired
    private ResourceMapper resourceMapper;

    @Override
    public List<Resource> getResourcesByUserId(Long userId) {
        return resourceMapper.selectResourcesByUserId(userId);
    }

    @Override
    public List<Resource> getResourcesByRoleId(Long roleId) {
        return resourceMapper.selectResourcesByRoleId(roleId);
    }
}
