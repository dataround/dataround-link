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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.dataround.link.common.PageResult;
import io.dataround.link.common.Result;
import io.dataround.link.common.entity.res.UserResponse;
import io.dataround.link.common.controller.BaseController;
import io.dataround.link.entity.VirtualField;
import io.dataround.link.entity.VirtualTable;
import io.dataround.link.entity.req.VirtualTableReq;
import io.dataround.link.entity.res.VirtualTableRes;
import io.dataround.link.service.ConnectionService;
import io.dataround.link.service.UserService;
import io.dataround.link.service.VirtualTableService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for managing virtual tables and related operations.
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
@RestController
@RequestMapping("/api/vtable")
@Slf4j
public class VirtualTableController extends BaseController {

    @Autowired
    private VirtualTableService virtualTableService;
    @Autowired
    private UserService userService;
    @Autowired
    private ConnectionService connectionService;

    @GetMapping("/{id}")
    public Result<VirtualTable> getById(@PathVariable Long id) {
        VirtualTable vt = virtualTableService.getById(id);
        return Result.success(vt);
    }

    @GetMapping("/list")
    public PageResult<List<VirtualTable>> list(@Parameter(hidden = true) Page<VirtualTable> page) {
        QueryWrapper<VirtualTable> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("id");
        wrapper.eq("project_id", getCurrentProjectId());
        Page<VirtualTable> virtualTables = virtualTableService.page(page, wrapper);
        Page<VirtualTableRes> virtualTableResPage = convert(virtualTables);
        // fill createUser
        Set<Long> userIds = virtualTables.getRecords().stream().map(VirtualTable::getCreateBy).collect(Collectors.toSet());
        Map<Long, String> userNameMap = userService.listNameByIds(userIds);
        // fill connector
        Set<Long> connIds = virtualTables.getRecords().stream().map(VirtualTable::getConnectionId).collect(Collectors.toSet());
        Map<Long, String> connectionNameMap = connectionService.listNameByIds(connIds);
        for (VirtualTableRes virtualTable : virtualTableResPage.getRecords()) {
            virtualTable.setCreateUser(userNameMap.get(virtualTable.getCreateBy()));
            virtualTable.setConnectionName(connectionNameMap.get(virtualTable.getConnectionId()));
        }
        return PageResult.success(virtualTables.getTotal(), virtualTables.getRecords());
    }

    @PostMapping("/saveOrUpdate")
    public Result<Boolean> saveOrUpdate(@RequestBody VirtualTableReq virtualTable) {
        UserResponse currentUser = getCurrentUser();
        // Convert received json object to tableConfig string
        virtualTable.setTableConfig(virtualTable.getJsonConfig().toString());
        Date now = new Date();
        if (virtualTable.getId() == null) {
            virtualTable.setProjectId(getCurrentProjectId());
            virtualTable.setCreateTime(now);
            virtualTable.setCreateBy(currentUser.getUserId());
        }
        virtualTable.setUpdateTime(now);
        for (VirtualField field : virtualTable.getFields()) {
            // save new vtable or update vtable with new field
            if (virtualTable.getId() == null || field.getId() == null) {
                field.setCreateTime(now);
            }
            field.setTableId(virtualTable.getId());
            field.setUpdateTime(now);
        }
        // save or update
        boolean bool = virtualTableService.saveOrUpdate(virtualTable);
        return Result.success(bool);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        Assert.notNull(id, "virtual table id should not be null");
        // TODO
        // check job used or not
        log.info("delete virtual table id:{}", id);
        boolean bool = virtualTableService.removeById(id);
        return Result.success(bool);
    }


    /**
     * Convert VirtualTable page to VirtualTableRes page.
     *
     * @param virtualTables The page of VirtualTable entities.
     * @return The page of VirtualTableRes entities.
     */
    private Page<VirtualTableRes> convert(Page<VirtualTable> virtualTables) {
        Page<VirtualTableRes> page = new Page<>();
        page.setTotal(virtualTables.getTotal());
        page.setRecords(virtualTables.getRecords().stream().map(vt -> {
            VirtualTableRes res = new VirtualTableRes();
            BeanUtils.copyProperties(virtualTables, page);
            return res;
        }).collect(Collectors.toList()));
        return page;
    }
}
