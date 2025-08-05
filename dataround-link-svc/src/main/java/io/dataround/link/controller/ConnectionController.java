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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.dataround.link.common.PageResult;
import io.dataround.link.common.Result;
import io.dataround.link.common.controller.BaseController;
import io.dataround.link.entity.Connector;
import io.dataround.link.config.MessageUtils;
import io.dataround.link.connector.TableField;
import io.dataround.link.entity.Connection;
import io.dataround.link.entity.VirtualTable;
import io.dataround.link.entity.res.ConnectionRes;
import io.dataround.link.entity.res.FieldMapping;
import io.dataround.link.service.ConnectionService;
import io.dataround.link.service.ConnectorService;
import io.dataround.link.service.UserService;
import io.dataround.link.service.VirtualTableService;
import io.dataround.link.utils.Constants;
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

    @GetMapping("/{id}")
    public Result<Connection> getById(@PathVariable Long id) {
        Connection connection = connectionService.getById(id);
        return Result.success(connection);
    }

    @GetMapping("/list")
    public PageResult<List<ConnectionRes>> list(@Parameter(hidden = true) Page<Connection> page, String connector, Integer type) {
        LambdaQueryWrapper<Connection> wrapper = new LambdaQueryWrapper<>();
        if (type != null) {
            LambdaQueryWrapper<Connector> subQueryWrapper = new LambdaQueryWrapper<>();
            if (type == 1) {
                subQueryWrapper.eq(Connector::getType, Constants.CONNECTION_TYPE_DATABASE);
            } else {
                subQueryWrapper.ne(Connector::getType, Constants.CONNECTION_TYPE_DATABASE);
            }
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
        List<ConnectionRes> connectionResList = convert2ConnectionRes(connections.getRecords());
        return PageResult.success(connections.getTotal(), connectionResList);
    }

    @PostMapping("/saveOrUpdate")
    public Result<Boolean> saveOrUpdate(@RequestBody Connection connection, HttpServletRequest request) {
        boolean bool = connectionService.saveOrUpdate(buildConnection(connection, request));
        return Result.success(bool);
    }

    @PostMapping("/test")
    public Result<Boolean> testConnection(@RequestBody Connection connection) {
        addDriverToConfig(connection);
        boolean bool = connectionService.testConnection(connection);
        return bool ? Result.success(MessageUtils.getMessage("connection.test.success")) : Result.error(MessageUtils.getMessage("connection.test.failed"));
    }

    private Connection buildConnection(Connection connection, HttpServletRequest request) {
        addDriverToConfig(connection);
        Long userId = getCurrentUserId();
        connection.setProjectId(getCurrentProjectId());
        Date now = new Date();
        connection.setUpdateBy(userId);
        connection.setUpdateTime(now);
        if (connection.getId() == null) {
            connection.setCreateBy(userId);
            connection.setCreateTime(now);
        }
        return connection;
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

    /**
     * Both source table fields and target fields required when do FieldMapping
     * If it's implement by reactjs, I don't know how to keep request sequence correctly when tabs changed quickly
     */
    @GetMapping("/{sourceId}/{sourceDb}/{sourceTable}/{targetId}/{targetDb}/{targetTable}")
    public Result<List<FieldMapping>> getSourceAndTargetTableFields(@PathVariable Long sourceId, @PathVariable String sourceDb, @PathVariable String sourceTable,
                                                                    @PathVariable Long targetId, @PathVariable String targetDb, @PathVariable String targetTable,
                                                                    Integer matchMethod) {
        boolean matchByName = matchMethod != null && matchMethod == Constants.FIELD_MAPPING_MATCH_BY_NAME;
        List<FieldMapping> list = new ArrayList<>();
        List<TableField> sourceFields = connectionService.getTableFields(sourceId, sourceDb, sourceTable);
        List<TableField> targetFields = connectionService.getTableFields(targetId, targetDb, targetTable);
        int idx = 0;
        for (TableField target : targetFields) {
            FieldMapping fm = new FieldMapping();
            fm.setTargetFieldName(target.getName());
            fm.setTargetFieldType(target.getType());
            fm.setTargetNullable(target.getNullable());
            fm.setTargetPrimaryKey(target.getPrimaryKey());
            if (matchByName) {
                for (TableField source : sourceFields) {
                    if (target.getName().equalsIgnoreCase(source.getName())) {
                        fm.setSourceFieldName(source.getName());
                        fm.setSourceFieldType(source.getType());
                        fm.setSourceNullable(source.getNullable());
                        fm.setSourcePrimaryKey(source.getPrimaryKey());
                        break;
                    }
                }
            } else {
                if (sourceFields.size() > idx) {
                    fm.setSourceFieldName(sourceFields.get(idx).getName());
                    fm.setSourceFieldType(sourceFields.get(idx).getType());
                    fm.setSourceNullable(sourceFields.get(idx).getNullable());
                    fm.setSourcePrimaryKey(sourceFields.get(idx).getPrimaryKey());
                }
            }
            list.add(fm);
            idx++;
        }
        return Result.success(list);
    }

    @GetMapping("/connectors")
    public Result<Map<String, List<String>>> listConnectors(String type, Boolean streamSource) {
        List<String> pluginNames = connectorService.getPluginNames(type);
        return Result.success(Map.of(type, pluginNames));
    }

    /**
     * Convert Connection to ConnectionRes
     * @param connections Connection list
     * @return ConnectionRes list
     */
    private List<ConnectionRes> convert2ConnectionRes(List<Connection> connections) {
        List<ConnectionRes> connectionResList = new ArrayList<>();
        for (Connection connection : connections) {
            ConnectionRes connectionRes = new ConnectionRes();
            BeanUtils.copyProperties(connection, connectionRes);
            connectionRes.setCreateUser(userService.getById(connection.getCreateBy()).getName());
            connectionRes.setUpdateUser(userService.getById(connection.getUpdateBy()).getName());
            connectionResList.add(connectionRes);
        }
        return connectionResList;
    }

    /**
     * Add driver property to connection config from connector properties
     * @param connection the connection object to modify
     */
    private void addDriverToConfig(Connection connection) {
        if (connection.getConnector() != null) {
            Connector connector = connectorService.getConnector(connection.getConnector());
            if (connector != null && connector.getProperties() != null) {
                String driver = connector.getProperties().get("driver");
                if (driver != null) {
                    // Ensure config is initialized as Map
                    if (connection.getConfig() == null) {
                        connection.setConfig(new HashMap<>());
                    }
                    connection.getConfig().put("driver", driver);
                }
            }
        }
    }
}
