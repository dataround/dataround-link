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

package io.dataround.link.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.dataround.link.common.Result;
import io.dataround.link.common.controller.BaseController;
import io.dataround.link.entity.Resource;
import io.dataround.link.entity.Role;
import io.dataround.link.service.ResourceService;
import io.dataround.link.service.RoleResourceService;
import io.dataround.link.service.RoleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * Role controller
 * 
 * @author yuehan124@gmail.com
 * @since 2025/02/19
 */
@RestController
@RequestMapping("/api/role")
@Tag(name = "role", description = "role management")
@Slf4j
public class RoleController extends BaseController {

    @Autowired
    private RoleService roleService;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private RoleResourceService roleResourceService;

    @GetMapping("/list")
    public Result<Page<Role>> list(Page<Role> params) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Role::getId);
        Page<Role> page = roleService.page(params, wrapper);
        return Result.success(page);
    }

    @GetMapping("/all")
    public Result<List<Role>> all() {
        List<Role> roles = roleService.list();
        return Result.success(roles);
    }

    @PostMapping("/saveOrUpdate")
    public Result<Boolean> saveOrUpdate(@RequestBody Role role) {
        if (role.getId() == null) {
            role.setCreateTime(new Date());
        }
        boolean result = roleService.saveOrUpdate(role);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean result = roleService.removeById(id);
        return Result.success(result);
    }

    @GetMapping("/{id}/resources")
    public Result<List<Resource>> getRoleResources(@PathVariable Long id) {
        List<Resource> resources = resourceService.getResourcesByRoleId(id);
        return Result.success(resources);
    }

    @PostMapping("/{id}/resources")
    public Result<Boolean> assignResources(@PathVariable Long id, @RequestBody List<Long> resourceIds) {
        roleResourceService.assignResourcesToRole(id, resourceIds);
        return Result.success(true);
    }
}
