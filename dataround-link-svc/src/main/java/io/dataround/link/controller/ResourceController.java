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
import io.dataround.link.service.ResourceService;
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
 * Resource controller
 * 
 * @author yuehan124@gmail.com
 * @since 2025/02/19
 */
@RestController
@RequestMapping("/api/resource")
@Tag(name = "resource", description = "resource management")
@Slf4j
public class ResourceController extends BaseController {

    @Autowired
    private ResourceService resourceService;

    @GetMapping("/list")
    public Result<Page<Resource>> list(Page<Resource> params) {
        LambdaQueryWrapper<Resource> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Resource::getPid);
        wrapper.orderByAsc(Resource::getId);
        Page<Resource> page = resourceService.page(params, wrapper);
        return Result.success(page);
    }

    @GetMapping("/all")
    public Result<List<Resource>> all() {
        LambdaQueryWrapper<Resource> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Resource::getPid);
        wrapper.orderByAsc(Resource::getId);
        List<Resource> resources = resourceService.list(wrapper);
        return Result.success(resources);
    }

    @GetMapping("/tree")
    public Result<List<Resource>> tree() {
        List<Resource> resources = resourceService.list();
        return Result.success(resources);
    }

    @GetMapping("/user")
    public Result<List<Resource>> userResources() {
        Long userId = getCurrentUserId();
        List<Resource> resources = resourceService.getResourcesByUserId(userId);
        return Result.success(resources);
    }

    @PostMapping("/saveOrUpdate")
    public Result<Boolean> saveOrUpdate(@RequestBody Resource resource) {
        if (resource.getId() == null) {
            resource.setCreateTime(new Date());
        }
        boolean result = resourceService.saveOrUpdate(resource);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean result = resourceService.removeById(id);
        return Result.success(result);
    }
}
