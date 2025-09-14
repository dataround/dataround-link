/*
 * Copyright 2025 yuehan124@gmail.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.dataround.link.connector;

import java.util.HashMap;
import java.util.List;
import io.dataround.link.common.connector.Param;

import org.junit.Before;
import org.junit.Test;

/**
 * MySQL JDBC connector test
 * 
 * @author yuehan124@gmail.com
 * @since 2025-08-26
 */
public class MySQLJdbcConnectorTest {

    private Param param;

    @Before
    public void setUp() {
        Param param = new Param();
        param.setUser("root");
        param.setPassword("test1234zxcv....");
        param.setConfig(new HashMap<>());
        param.getConfig().put("driver", "com.mysql.cj.jdbc.Driver");
        param.getConfig().put("url", "jdbc:mysql://localhost:3306/test");
        this.param = param;
    }

    @Test
    public void testDoGetDatabases() {
        try (MySQLJdbcConnector connector = new MySQLJdbcConnector()) {
            connector.initialize(param);
            List<String> databases = connector.doGetDatabases();
            System.out.println(databases);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDoGetTables() {
        try (MySQLJdbcConnector connector = new MySQLJdbcConnector()) {
            connector.initialize(param);
            List<String> tables = connector.doGetTables("test");
            System.out.println(tables);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDoGetTableFields() {
        try (MySQLJdbcConnector connector = new MySQLJdbcConnector()) {
            connector.initialize(param);
            List<TableField> tableFields = connector.doGetTableFields("test", "gen_table");
            System.out.println(tableFields);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
