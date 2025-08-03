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

package io.dataround.link.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.dataround.link.entity.Connector;
import io.dataround.link.common.connector.Param;
import io.dataround.link.connector.ConnectorFactory;
import io.dataround.link.connector.TableField;
import io.dataround.link.entity.Connection;
import io.dataround.link.entity.VirtualField;
import io.dataround.link.entity.VirtualTable;
import io.dataround.link.mapper.ConnectionMapper;
import io.dataround.link.mapper.VirtualTableMapper;
import io.dataround.link.service.ConnectionService;
import io.dataround.link.service.ConnectorService;
import io.dataround.link.service.VirtualFieldService;
import io.dataround.link.utils.Constants;
import io.dataround.link.utils.ParamParser;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the ConnectionService interface.
 * Provides concrete implementation for managing database connections, including connection testing,
 * metadata retrieval, and virtual table operations.
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
@Slf4j
@Service
public class ConnectionServiceImpl extends ServiceImpl<ConnectionMapper, Connection> implements ConnectionService {

    @Autowired
    private ConnectionMapper connectionMapper;
    @Autowired
    private VirtualTableMapper virtualTableMapper;
    @Autowired
    private VirtualFieldService virtualFieldService;
    @Autowired
    private ConnectorService connectorService;

    @Override
    public Map<Long, String> listNameByIds(Set<Long> connectionIds) {
        if (connectionIds.isEmpty()) {
            return new HashMap<>();
        }
        return this.listByIds(connectionIds).stream().collect(Collectors.toMap(Connection::getId, Connection::getName));
    }

    @Override
    public boolean testConnection(Connection connection) {
        Connector connector = connectorService.getConnector(connection.getConnector());
        Param param = ParamParser.from(connection, connector);
        try {
            return ConnectorFactory.createConnector(param).testConnectivity();
        } catch (Throwable e) {
            log.error("test connection error", e);
        }
        return false;
    }

    @Override
    public boolean checkProjectUsed(Long projectId) {
        return connectionMapper.checkProjectUsed(projectId);
    }

    public List<String> getDatabases(Long connectionId) {
        Connection connection = connectionMapper.selectById(connectionId);
        Connector connector = connectorService.getConnector(connection.getConnector());
        if (!connector.getVirtualTable()) {
            Param param = ParamParser.from(connection, connector);
            // if classLoaderRestore() not execute, strange error msg: load class is error
            return ConnectorFactory.createTableConnector(param).getDatabases();
        }
        return virtualTableMapper.getDatabaseByConnectionId(connectionId);
    }

    public List<String> getTableNames(Long connectionId, String databaseName) {
        return getTableNames(connectionId, databaseName, null, null);
    }

    public List<String> getTableNames(Long connectionId, String databaseName, String filterName, Integer size) {
        Connection connection = connectionMapper.selectById(connectionId);
        Connector connector = connectorService.getConnector(connection.getConnector());
        if (!connector.getVirtualTable()) {
            Param param = ParamParser.from(connection, connector);
            // if (size != null) {
            //     configMap.put("size", size.toString());
            // }
            // if (filterName != null) {
            //     configMap.put("filterName", filterName);
            // }
            return ConnectorFactory.createTableConnector(param).getTables(databaseName);
        }
        return virtualTableMapper.getTablesByConnectionIdAndDatabase(connectionId, databaseName);
    }

    public List<TableField> getTableFields(Long connectionId, String databaseName, String tableName) {
        Connection connection = connectionMapper.selectById(connectionId);
        Connector connector = connectorService.getConnector(connection.getConnector());
        if (!connector.getVirtualTable()) {
            Param param = ParamParser.from(connection, connector);
            return ConnectorFactory.createTableConnector(param).getTableFields(databaseName, tableName);
        }
        VirtualTable virtualTable = virtualTableMapper.getVirtualTable(connectionId, databaseName, tableName);

        // convert virtual table to table field
        VirtualField params = new VirtualField();
        params.setTableId(virtualTable.getId());
        List<VirtualField> fields = virtualFieldService.list(new QueryWrapper<>(params));
        return fields.stream().map(field -> {
            TableField tableField = new TableField();
            tableField.setName(field.getName());
            tableField.setType(field.getType());
            tableField.setComment(field.getComment());
            tableField.setNullable(field.getNullable());
            tableField.setDefaultValue(field.getDefaultValue());
            tableField.setPrimaryKey(field.getPrimaryKey());
            return tableField;
        }).collect(Collectors.toList());
    }

    public Map<String, String> connection2Map(Connection connection) {
        Map<String, String> map = connection.getConfig();
        Connector connector = connectorService.getConnector(connection.getConnector());
        Map<String, String> properties = connector.getProperties();
        if (properties != null) {
            map.put("driver", properties.get("driver"));
        }
        map.put("libDir", connector.getLibDir());
        map.put("type", connector.getType());
        map.put("host", connection.getHost());
        if (connection.getPort() != null) {
            map.put("port", connection.getPort().toString());
        }
        map.put("user", connection.getUser());
        map.put("password", connection.getPasswd());
        // rename broker to bootstrap.servers
        if (map.containsKey("broker")) {
            map.put("\"bootstrap.servers\"", map.get("broker"));
            map.remove("broker");
        }
        // MYSQL-CDC use base-url and username, different property key
        String pluginName = connector.getPluginName();
        if (Constants.PlUGIN_NAME_MYSQL_CDC.equals(pluginName)) {
            map.put("base-url", map.get("url"));
            map.put("username", map.get("user"));
        }
        return map;
    }

}
