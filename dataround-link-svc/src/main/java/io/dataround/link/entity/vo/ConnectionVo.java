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

package io.dataround.link.entity.vo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.dataround.link.entity.Connector;
import io.dataround.link.common.utils.ConnectorNameConstants;
import io.dataround.link.entity.Connection;
import lombok.Getter;
import lombok.Setter;

/**
 * ConnectionVo is the base class for all connection value object classes.
 * It contains common fields for all connection types.
 *
 * @author yuehan124@gmail.com
 * @since 2025-09-26
 */
@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "connector", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = JdbcConnectionVo.class, name = ConnectorNameConstants.MYSQL),
        @JsonSubTypes.Type(value = JdbcConnectionVo.class, name = ConnectorNameConstants.POSTGRESQL),
        @JsonSubTypes.Type(value = JdbcConnectionVo.class, name = ConnectorNameConstants.ORACLE),
        @JsonSubTypes.Type(value = JdbcConnectionVo.class, name = ConnectorNameConstants.SQLSERVER),
        @JsonSubTypes.Type(value = JdbcConnectionVo.class, name = ConnectorNameConstants.TIDB),
        @JsonSubTypes.Type(value = JdbcConnectionVo.class, name = ConnectorNameConstants.DM),
        @JsonSubTypes.Type(value = JdbcConnectionVo.class, name = ConnectorNameConstants.KINGBASE),
        @JsonSubTypes.Type(value = HiveConnectionVo.class, name = ConnectorNameConstants.HIVE),
        @JsonSubTypes.Type(value = CdcConnectionVo.class, name = ConnectorNameConstants.MYSQL_CDC),
        @JsonSubTypes.Type(value = CdcConnectionVo.class, name = ConnectorNameConstants.SQLSERVER_CDC),
        @JsonSubTypes.Type(value = KafkaConnectionVo.class, name = ConnectorNameConstants.KAFKA),
        @JsonSubTypes.Type(value = FtpConnectionVo.class, name = ConnectorNameConstants.FTP),
        @JsonSubTypes.Type(value = FtpConnectionVo.class, name = ConnectorNameConstants.SFTP),
        @JsonSubTypes.Type(value = LocalFileConnectionVo.class, name = ConnectorNameConstants.LOCAL_FILE),
        @JsonSubTypes.Type(value = S3ConnectionVo.class, name = ConnectorNameConstants.S3)
})
public abstract class ConnectionVo extends BaseVo{
    // --- database connection fields ---
    private Long id;
    private Long projectId;
    private String name;
    private String description;
    private String connector;
    private Long connectorVersionId;
    private String host;
    private Integer port;
    private String user;
    private String passwd;
    private Map<String, String> config;
    private Long createBy;
    private Long updateBy;
    private Date createTime;
    private Date updateTime;
    // --- response fields ---
    private String createUser;
    private String updateUser;
    
    public Connection buildConnection(Connector connector, Long userId, Long projectId) {
        Connection connection = new Connection();
        connection.setId(getId());
        connection.setName(getName());
        connection.setDescription(getDescription());
        connection.setConnector(this.connector);
        connection.setConnectorVersionId(connectorVersionId);        
        connection.setHost(host);
        connection.setPort(port);
        connection.setUser(user);
        connection.setPasswd(passwd);
        connection.setConfig(config);
        if (this.config == null) {
            connection.setConfig(new HashMap<>());
        }    
        Date now = new Date();
        connection.setUpdateBy(userId);
        connection.setUpdateTime(now);
        if (connection.getId() == null) {
            connection.setCreateBy(userId);
            connection.setCreateTime(now);
        }
        connection.setProjectId(projectId);
        extractProperties(connection);
        return connection;
    }

    public void buildConnectionVo(Connection connection) {
        BeanUtils.copyProperties(connection, this);
        fillProperties(connection.getConfig());
    }

    public abstract void extractProperties(Connection connection);

    public abstract void fillProperties(Map<String, String> config);
 
}