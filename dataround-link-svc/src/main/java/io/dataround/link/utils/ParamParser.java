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

package io.dataround.link.utils;

import io.dataround.link.common.connector.Param;
import io.dataround.link.entity.Connection;
import io.dataround.link.entity.Connector;
import io.dataround.link.entity.ConnectorVersion;

/**
 * Param parser, parse the connection entity to Param.
 *
 * @author yuehan124@gmail.com
 * @date 2025-07-28
 */
public class ParamParser {

    public static Param from(Connection connection, Connector connector, ConnectorVersion connectorVersion) {
        Param param = new Param();
        param.setName(connection.getConnector());
        param.setHost(connection.getHost());
        param.setPort(connection.getPort());
        param.setUser(connection.getUser());
        param.setPassword(connection.getPasswd());
        param.setConfig(connection.getConfig());
        if (connector != null) {
            param.setType(connector.getType());
        }
        // If connectorVersion is not null, use name_version as libDir, otherwise use connector_name as libDir
        if (connector != null) {
            String libDir = connector.getName();           
            if (connectorVersion != null) {
                libDir = connectorVersion.getValue();
            }
            param.setLibDir(libDir);
        }
        return param;
    }
}
