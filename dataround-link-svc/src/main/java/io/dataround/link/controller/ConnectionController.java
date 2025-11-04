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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.dataround.link.common.PageResult;
import io.dataround.link.common.Result;
import io.dataround.link.common.controller.BaseController;
import io.dataround.link.entity.Connector;
import io.dataround.link.config.MessageUtils;
import io.dataround.link.connection.ConnectionFactory;
import io.dataround.link.connector.TableField;
import io.dataround.link.entity.Connection;
import io.dataround.link.entity.VirtualTable;
import io.dataround.link.entity.vo.ConnectionVo;
import io.dataround.link.service.ConnectionService;
import io.dataround.link.service.ConnectorService;
import io.dataround.link.service.UserService;
import io.dataround.link.service.VirtualTableService;
import io.dataround.link.service.HazelcastCacheService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for managing connections and related operations.
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
@RestController
@RequestMapping("/api/connection")
@Slf4j
public class ConnectionController extends BaseController {

    @Autowired
    private ConnectionService connectionService;
    @Autowired
    private VirtualTableService virtualTableService;
    @Autowired
    private ConnectorService connectorService;
    @Autowired
    private UserService userService;
    @Autowired
    private HazelcastCacheService cacheService;

    @GetMapping("/{id}")
    public Result<ConnectionVo> getById(@PathVariable Long id) {
        Connection connection = connectionService.getById(id);
        ConnectionVo connectionVo = ConnectionFactory.create(connection.getConnector());
        connectionVo.buildConnectionVo(connection);
        return Result.success(connectionVo);
    }

    @GetMapping("/list")
    public PageResult<List<ConnectionVo>> list(@Parameter(hidden = true) Page<Connection> page, @RequestParam(required = false) String connector, @RequestParam(required = false) ArrayList<String> types) {
        LambdaQueryWrapper<Connection> wrapper = new LambdaQueryWrapper<>();
        if (types != null && !types.isEmpty()) {
            LambdaQueryWrapper<Connector> subQueryWrapper = new LambdaQueryWrapper<>();
            subQueryWrapper.in(Connector::getType, types);
            List<Connector> connectors = connectorService.list(subQueryWrapper);
            // extract connector name list
            List<String> connectorNames = connectors.stream().map(Connector::getName).collect(Collectors.toList());
            // if no connector match, return empty result
            if (connectorNames.isEmpty()) {
                return PageResult.success(0L, new ArrayList<>());
            }
            // filter connection by connector name
            wrapper.in(Connection::getConnector, connectorNames);
        }
        if (connector != null) {
            wrapper.eq(Connection::getConnector, connector);
        }
        wrapper.orderByDesc(Connection::getId);
        wrapper.eq(Connection::getProjectId, getCurrentProjectId());
        Page<Connection> connections = connectionService.page(page, wrapper);
        List<ConnectionVo> connectionResList = convert2ConnectionVo(connections.getRecords());
        return PageResult.success(connections.getTotal(), connectionResList);
    }

    @PostMapping("/saveOrUpdate")
    public Result<Boolean> saveOrUpdate(@RequestBody ConnectionVo connectionVo) {
        Connector connector = connectorService.getConnector(connectionVo.getConnector());
        Connection connection = connectionVo.buildConnection(connector, getCurrentUserId(), getCurrentProjectId());
        boolean bool = connectionService.saveOrUpdate(connection);
        return Result.success(bool);
    }

    @PostMapping("/upload")
    public Result<Object> upload(@RequestParam("file") MultipartFile file) {
        try {
            // Generate a unique key for the file
            String fileKey = "file_" + System.currentTimeMillis() + "_" + RandomStringUtils.randomAlphanumeric(5);
            
            // Cache the uploaded configuration file content in Hazelcast
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            boolean cached = cacheService.put(fileKey, content);
            if (cached) {
                log.info("Uploaded configuration file cached in Hazelcast with key: {}", fileKey);
                return Result.success((Object)fileKey); // Return the generated key
            } else {
                log.error("Failed to cache uploaded configuration file with key: {}", fileKey);
                return Result.error("Failed to cache uploaded file");
            }
        } catch (Exception e) {
            log.error("Failed to process uploaded file", e);
            return Result.error("Failed to process uploaded file");
        }
    }

    @PostMapping("/test")
    public Result<Boolean> testConnection(@RequestBody ConnectionVo connectionVo) {
        Connector connector = connectorService.getConnector(connectionVo.getConnector());
        Connection connection = connectionVo.buildConnection(connector, getCurrentUserId(), getCurrentProjectId());
        boolean bool = connectionService.testConnection(connection);
        return bool ? Result.success(MessageUtils.getMessage("connection.test.success")) : Result.error(MessageUtils.getMessage("connection.test.failed"));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id, HttpServletRequest request) {
        Assert.notNull(id, "connection id should not be null");
        // check virtual_table used or not
        VirtualTable vt = new VirtualTable();
        vt.setConnectionId(id);
        long count = virtualTableService.count(new QueryWrapper<>(vt));
        if (count > 0) {
            return Result.error("connection is used by virtual table");
        }
        // check privilege
        Connection connection = connectionService.getById(id);
        if (connection.getCreateBy().intValue() != getCurrentUserId()) {
            return Result.error("Permission denied");
        }
        log.info("delete connection id: {}", id);
        boolean bool = connectionService.removeById(id);
        return bool ? Result.success(bool) : Result.error();
    }

    @GetMapping("/{id}/dbs")
    public Result<List<String>> getDatabases(@PathVariable Long id) {
        List<String> databases = connectionService.getDatabases(id);
        return Result.success(databases);
    }

    @GetMapping("/{id}/{databaseName}/tables")
    public Result<List<String>> getTableNames(@PathVariable Long id, @PathVariable String databaseName) {
        List<String> tableNames = connectionService.getTableNames(id, databaseName);
        return Result.success(tableNames);
    }

    @GetMapping("/{id}/{databaseName}/{tableName}/columns")
    public Result<List<TableField>> getTableFields(@PathVariable Long id, @PathVariable String databaseName, @PathVariable String tableName) {
        List<TableField> tableFields = connectionService.getTableFields(id, databaseName, tableName);
        return Result.success(tableFields);
    }

    @GetMapping("/connectors")
    public Result<Map<String, List<String>>> listConnectors(String type, Boolean streamSource) {
        List<String> pluginNames = connectorService.getPluginNames(type);
        return Result.success(Map.of(type, pluginNames));
    }

    /**
     * Convert Connection to ConnectionVo
     * @param connections Connection list
     * @return ConnectionVo list
     */
    private List<ConnectionVo> convert2ConnectionVo(List<Connection> connections) {
        List<ConnectionVo> connectionVos = new ArrayList<>();
        Map<Long, String> userMap = new HashMap<>();
        for (Connection connection : connections) {
            ConnectionVo connectionVo = ConnectionFactory.create(connection.getConnector());
            BeanUtils.copyProperties(connection, connectionVo);
            connectionVo.setCreateUser(userMap.computeIfAbsent(connection.getCreateBy(), k -> userService.getById(k).getName()));
            connectionVo.setUpdateUser(userMap.computeIfAbsent(connection.getUpdateBy(), k -> userService.getById(k).getName()));
            connectionVos.add(connectionVo);
        }
        return connectionVos;
    }
}