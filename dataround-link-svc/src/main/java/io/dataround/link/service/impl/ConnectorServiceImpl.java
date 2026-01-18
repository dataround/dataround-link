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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.dataround.link.entity.Connection;
import io.dataround.link.entity.Connector;
import io.dataround.link.entity.ConnectorVersion;
import io.dataround.link.entity.dto.ConnectorDto;
import io.dataround.link.mapper.ConnectorMapper;
import io.dataround.link.mapper.ConnectorVersionMapper;
import io.dataround.link.service.ConnectorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for managing connector definitions.
 * Provides methods to retrieve and filter connector information.
 *
 * @author yuehan124@gmail.com
 * @since 2025-05-06
 */
@Slf4j
@Service
public class ConnectorServiceImpl extends ServiceImpl<ConnectorMapper, Connector> implements ConnectorService {

    @Autowired
    private ConnectorMapper connectorMapper;
    
    @Autowired
    private ConnectorVersionMapper connectorVersionMapper;

    @Override
    public Connector getConnector(String name) {
        Connector connector = connectorMapper.findByName(name);
        if (connector == null) {
            throw new RuntimeException("can not find plugin name for connector: " + name);
        }
        return connector;
    }

    @Override
    public String getPluginName(String name) {
        return getConnector(name).getPluginName();
    }

    @Override
    public List<String> getPluginNames(String type) {
        LambdaQueryWrapper<Connector> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Connector::getType, type);
        return list(wrapper).stream()
                .map(Connector::getPluginName)
                .toList();
    }

    @Override
    public ConnectorDto getConnectorDto(String connectorName, Long connectorVersionId) {
        Connector connector = getConnector(connectorName);
        ConnectorVersion connectorVersion = getByVersionIdOrDefault(connectorVersionId, connectorName);
        return new ConnectorDto(connector, connectorVersion);
    }

    @Override
    public ConnectorDto getConnectorDto(Connection connection) {
        Connector connector = getConnector(connection.getConnector());
        ConnectorVersion connectorVersion = getByVersionIdOrDefault(connection.getConnectorVersionId(), connector.getName());
        return new ConnectorDto(connector, connectorVersion);
    }

    @Override
    public List<ConnectorVersion> getByConnectorName(String connectorName) {
        return connectorVersionMapper.getByConnector(connectorName);
    }

    private ConnectorVersion getByVersionIdOrDefault(Long versionId, String connectorName) {
        if (versionId != null) {
            return connectorVersionMapper.selectById(versionId);
        }
        LambdaQueryWrapper<ConnectorVersion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ConnectorVersion::getConnector, connectorName);
        queryWrapper.eq(ConnectorVersion::getIsDefault, true);
        return connectorVersionMapper.selectOne(queryWrapper);
    }
} 