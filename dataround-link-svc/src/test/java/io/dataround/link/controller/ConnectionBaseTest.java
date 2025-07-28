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

import io.dataround.link.common.entity.res.UserResponse;
import io.dataround.link.entity.Connection;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

/**
 * Base test class for connection-related tests.
 * Provides common setup and utilities for testing database connections.
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
@SpringBootTest
@ActiveProfiles("test")
public class ConnectionBaseTest extends BaseControllerTest {
    protected Connection buildMySQL() {
        Connection connection = new Connection();
        connection.setConnector("MySQL");
        Map<String, String> map = new HashMap<>();
        map.put("driver", "com.mysql.cj.jdbc.Driver");
        map.put("url", "jdbc:mysql://121.37.104.3:3306/test");
        connection.setHost("121.37.104.3");
        connection.setPort(3306);
        connection.setUser("root");
        connection.setPasswd("test1234zxcv....");
        //connection.setConfig(JSONUtils.toJsonString(map));
        return fillOtherInfo(connection);
    }

    protected Connection buildOracle() {
        Connection connection = new Connection();
        connection.setConnector("Oracle");
        Map<String, String> map = new HashMap<>();
        map.put("driver", "oracle.jdbc.OracleDriver");
        // using service name
        //map.put("url", "jdbc:oracle:thin:@//121.37.104.3:10002/XE");
        // use sid
        map.put("url", "jdbc:oracle:thin:@121.37.104.3:10002:XE");
        connection.setHost("121.37.104.3");
        connection.setPort(10002);
        connection.setUser("system");
        connection.setPasswd("test1234zxcv....");
        //connection.setConfig(JSONUtils.toJsonString(map));
        return fillOtherInfo(connection);
    }

    protected Connection buildMSSQL() {
        Connection connection = new Connection();
        connection.setConnector("SQLServer");
        Map<String, String> map = new HashMap<>();
        map.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        map.put("url", "jdbc:sqlserver://121.37.104.3:10003;databaseName=msdb;trustServerCertificate=true");
        connection.setHost("121.37.104.3");
        connection.setPort(10003);
        connection.setUser("sa");
        connection.setPasswd("test1234ZXCV....@");
        //connection.setConfig(JSONUtils.toJsonString(map));
        return fillOtherInfo(connection);
    }

    protected Connection buildTidb() {
        Connection connection = new Connection();
        connection.setConnector("Tidb");
        Map<String, String> map = new HashMap<>();
        map.put("driver", "com.mysql.cj.jdbc.Driver");
        map.put("url", "jdbc:mysql://121.37.104.3:10004/");
        connection.setHost("121.37.104.3");
        connection.setPort(10004);
        connection.setUser("root");
        connection.setPasswd("MyNewPassword123");
        //connection.setConfig(JSONUtils.toJsonString(map));
        return fillOtherInfo(connection);
    }

    protected Connection buildHiveMetaStore() {
        Connection connection = new Connection();
        connection.setConnector("Hive");
        Map<String, String> map = new HashMap<>();
        map.put("metastore_uri", "thrift://10.122.198.164:9083");
        connection.setHost("10.122.198.164");
        connection.setPort(9083);
        //connection.setConfig(JSONUtils.toJsonString(map));
        return fillOtherInfo(connection);
    }

    protected Connection buildHiveJDBC() {
        Connection connection = new Connection();
        connection.setConnector("Hive");
        Map<String, String> map = new HashMap<>();
        map.put("driver", "org.apache.hive.jdbc.HiveDriver");
        String url = "jdbc:hive2://node44.it.leap.com:2181,node43.it.leap.com:2181,node40.it.leap.com:2181/;serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2";
        map.put("url", url);
        map.put("user", "hive");
        map.put("password", "");
        //connection.setConfig(JSONUtils.toJsonString(map));
        return fillOtherInfo(connection);
    }

    private Connection fillOtherInfo(Connection connection) {
        connection.setName("conn_" + connection.getConnector());
        connection.setDescription("desc");
        UserResponse userInfo = new UserResponse();
        userInfo.setUserId(userId);
        return connection;
    }
}
