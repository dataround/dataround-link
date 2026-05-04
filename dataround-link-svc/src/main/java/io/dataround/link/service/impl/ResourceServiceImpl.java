/*
 * Copyright (C) 2025 yuehan124@gmail.com
 *
 * This repository is licensed under the Dataround Open Source License
 */

package io.dataround.link.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.dataround.link.config.MessageUtils;
import io.dataround.link.entity.Resource;
import io.dataround.link.entity.ResourceApi;
import io.dataround.link.entity.res.ResouceRes;
import io.dataround.link.mapper.ResourceApiMapper;
import io.dataround.link.mapper.ResourceMapper;
import io.dataround.link.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Resource service impl
 * 
 * @author yuehan124@gmail.com
 * @since 2025/09/21
 */
@Service
@Slf4j
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements ResourceService {
    @Autowired
    private ResourceMapper resourceMapper;
    @Autowired
    private ResourceApiMapper resourceApiMapper;

    @Override
    public List<ResouceRes> getResourcesByUserId(Long userId) {
        List<Resource> resources = resourceMapper.selectResourcesByUserId(userId);
        return convertToResouceResList(resources);
    }
    
    @Override
    public List<ResouceRes> getResourcesWithApis() {
        // Get all resources ordered by pid, id
        LambdaQueryWrapper<Resource> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Resource::getPid);
        wrapper.orderByAsc(Resource::getId);
        List<Resource> resources = this.list(wrapper);
        
        return convertToResouceResList(resources);
    }
    
    @Override
    public List<ResourceApi> getApisByUserId(Long userId) {
        // Get user's menu resources
        List<Resource> menuResources = resourceMapper.selectResourcesByUserId(userId);
        
        // Get resource IDs
        List<Long> resourceIds = menuResources.stream()
            .map(Resource::getId)
            .collect(Collectors.toList());
        
        if (resourceIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Get API paths from resource_api table
        return resourceApiMapper.listByResourceIds(resourceIds);
    }

    @Override
    public List<ResouceRes> getResourcesByRoleId(Long roleId) {
        List<Resource> resources = resourceMapper.selectResourcesByRoleId(roleId);
        return convertToResouceResList(resources);
    }
    
    @Override
    @Transactional
    public boolean saveResourceWithApis(Resource resource, List<ResourceApi> resourceApis) {
        // Set create time if new resource
        if (resource.getId() == null) {
            resource.setCreateTime(new Date());
        }
        
        // Save or update resource
        boolean result = this.saveOrUpdate(resource);
        
        if (result && resource.getId() != null) {
            // Delete existing APIs
            resourceApiMapper.deleteByResourceId(resource.getId());
            
            // Insert new APIs
            if (resourceApis != null && !resourceApis.isEmpty()) {
                for (ResourceApi api : resourceApis) {
                    api.setResourceId(resource.getId());
                    resourceApiMapper.insert(api);
                }
            }
        }
        
        return result;
    }
    
    @Override
    @Transactional
    public boolean deleteResourceWithApis(Long resourceId) {
        // Delete resource APIs first
        resourceApiMapper.deleteByResourceId(resourceId);

        // Delete resource
        return this.removeById(resourceId);
    }

    /**
     * Convert Resource list to ResouceRes list with resolved names and APIs
     */
    private List<ResouceRes> convertToResouceResList(List<Resource> resources) {
        return resources.stream().map(resource -> {
            ResouceRes res = new ResouceRes();
            BeanUtils.copyProperties(resource, res);
            // Resolve i18n name
            String i18nName = resource.getI18nName();
            if (i18nName != null && !i18nName.isEmpty()) {
                res.setName(MessageUtils.getMessage(i18nName));
            }
            // Load resourceApis
            LambdaQueryWrapper<ResourceApi> apiWrapper = new LambdaQueryWrapper<>();
            apiWrapper.eq(ResourceApi::getResourceId, resource.getId());
            res.setResourceApis(resourceApiMapper.selectList(apiWrapper));
            return res;
        }).collect(Collectors.toList());
    }
}
