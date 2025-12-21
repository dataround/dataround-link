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

import org.junit.Before;
import org.junit.Test;

import io.dataround.link.common.connector.Param;

/**
 * Postgres Jdbc Connector Test
 * 
 * @author yuehan124@gmail.com
 * @since 2025-09-13
 */
public class PostgresJdbcConnectorTest {

    private Param param;

    @Before
    public void before() {
        Param param = new Param();
        param.setUser("postgres");
        param.setPassword("dataround.io");
        param.setConfig(new HashMap<>());
        param.getConfig().put("driver", "org.postgresql.Driver");
        param.getConfig().put("url", "jdbc:postgresql://localhost:5432/tpcds_tst");
        this.param = param;
    }

    @Test
    public void testDoGetDatabases() {
        try (PostgresJdbcConnector connector = new PostgresJdbcConnector()) {
            connector.initialize(param);
            List<String> databases = connector.doGetDatabases();
            System.out.println(databases);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDoGetTables() {
        try (PostgresJdbcConnector connector = new PostgresJdbcConnector()) {
            connector.initialize(param);
            List<String> tables = connector.doGetTables("tpcds_tst");
            System.out.println(tables);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDoGetTableFields() {
        try (PostgresJdbcConnector connector = new PostgresJdbcConnector()) {
            connector.initialize(param);
            List<TableField> fields = connector.doGetTableFields("tpcds_tst", "catalog_sales");
            System.out.println(fields);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
