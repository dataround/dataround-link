/*
 * Copyright (C) 2025 yuehan124@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.dataround.link.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.dataround.link.entity.Resource;
import io.dataround.link.entity.ResourceApi;
import io.dataround.link.entity.res.ResouceRes;

import java.util.List;

/**
 * Resource service
 * 
 * @author yuehan124@gmail.com
 * @since 2025/09/21
 */
public interface ResourceService extends IService<Resource> {

    /**
     * Get resources by user ID (through user's roles)
     */
    List<ResouceRes> getResourcesByUserId(Long userId);

    /**
     * Get resources by role ID
     */
    List<ResouceRes> getResourcesByRoleId(Long roleId);
    
    /**
     * Get all resources with their APIs and resolved names (ordered by pid, id)
     */
    List<ResouceRes> getResourcesWithApis();
    
    /**
     * Get API paths by user ID (from resource_api table)
     */
    List<ResourceApi> getApisByUserId(Long userId);
    
    /**
     * Save or update resource with its APIs (in one transaction)
     */
    boolean saveResourceWithApis(Resource resource, List<ResourceApi> resourceApis);
    
    /**
     * Delete resource and its APIs (in one transaction)
     */
    boolean deleteResourceWithApis(Long resourceId);
}
