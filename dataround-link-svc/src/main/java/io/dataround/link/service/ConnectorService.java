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

import io.dataround.link.entity.Connection;
import io.dataround.link.entity.Connector;
import io.dataround.link.entity.ConnectorVersion;
import io.dataround.link.entity.dto.ConnectorDto;

import java.util.List;

/**
 * Service interface for managing database connectors.
 *
 * @author yuehan124@gmail.com
 * @since 2025-05-06
 */
public interface ConnectorService extends IService<Connector> {

    /**
     * Get connector definition by name
     * @param name connector name
     * @return connector definition
     */
    Connector getConnector(String name);

    /**
     * Get plugin name by connector name
     * @param name connector name
     * @return plugin name
     */
    String getPluginName(String name);

    /**
     * Get plugin names by type
     * @param type connector type (database, cdc, nonstructural)
     * @return list of plugin names
     */
    List<String> getPluginNames(String type);

    /**
     * Get connector dto by connector name and connector version id
     * @param connector the connector name
     * @param connectorVersionId the connector version id
     * @return the connector dto
     */
    ConnectorDto getConnectorDto(String connector, Long connectorVersionId);

    /**
     * Get connector dto by connection
     * @param connection the connection
     * @return the connector dto
     */
    ConnectorDto getConnectorDto(Connection connection);

    /**
     * Get connector versions by connector name.
     *
     * @param connectorName the connector name
     * @return the list of connector versions
     */
    List<ConnectorVersion> getByConnectorName(String connectorName);
}
