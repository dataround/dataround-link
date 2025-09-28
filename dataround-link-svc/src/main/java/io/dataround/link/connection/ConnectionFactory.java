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

package io.dataround.link.connection;

import io.dataround.link.common.utils.ConnectorNameConstants;
import io.dataround.link.entity.vo.CdcConnectionVo;
import io.dataround.link.entity.vo.ConnectionVo;
import io.dataround.link.entity.vo.FtpConnectionVo;
import io.dataround.link.entity.vo.S3ConnectionVo;
import io.dataround.link.entity.vo.HiveConnectionVo;
import io.dataround.link.entity.vo.JdbcConnectionVo;
import io.dataround.link.entity.vo.KafkaConnectionVo;
import io.dataround.link.entity.vo.LocalFileConnectionVo;

/**
 * Factory for creating connection resources.
 *
 * @author yuehan124@gmail.com
 * @date 2025-09-27
 */
public class ConnectionFactory {

    /**
     * Get the appropriate configuration generator for the given connector
     * @param connector the connector to get generator for
     * @return the configuration generator, or null if none found
     */
    public static ConnectionVo create(String connector) {
        switch (connector) {
            case ConnectorNameConstants.MYSQL:
            case ConnectorNameConstants.POSTGRESQL:
            case ConnectorNameConstants.ORACLE:
            case ConnectorNameConstants.SQLSERVER:
            case ConnectorNameConstants.TIDB:
            case ConnectorNameConstants.DM:
            case ConnectorNameConstants.KINGBASE:
                return new JdbcConnectionVo();
            case ConnectorNameConstants.HIVE:
                return new HiveConnectionVo();
            case ConnectorNameConstants.MYSQL_CDC:
            case ConnectorNameConstants.SQLSERVER_CDC:
                return new CdcConnectionVo();
            case ConnectorNameConstants.KAFKA:
                return new KafkaConnectionVo();
            case ConnectorNameConstants.FTP:
            case ConnectorNameConstants.SFTP:           
                return new FtpConnectionVo();
            case ConnectorNameConstants.LOCAL_FILE:
                return new LocalFileConnectionVo();
            case ConnectorNameConstants.S3:
                return new S3ConnectionVo();
            default:
                throw new IllegalArgumentException("Unknown connector type: " + connector);
        }
    }
}
