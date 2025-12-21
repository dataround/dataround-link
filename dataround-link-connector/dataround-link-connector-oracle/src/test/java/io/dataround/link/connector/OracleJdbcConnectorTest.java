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
import lombok.extern.slf4j.Slf4j;

/**
 * Oracle JDBC connector test
 * 
 * @author yuehan124@gmail.com
 * @since 2025-08-03
 */
@Slf4j
public class OracleJdbcConnectorTest {
    
    private Param param;

    @Before
    public void setUp() {
        Param param = new Param();
        param.setUser("tpcds");
        param.setPassword("tpcds1234@56");
        param.setConfig(new HashMap<>());
        param.getConfig().put("driver", "oracle.jdbc.OracleDriver");
        param.getConfig().put("url", "jdbc:oracle:thin:@//localhost:1521/XEPDB1");
        this.param = param;
    }

    @Test
    public void testDoGetDatabases() {
        try (OracleJdbcConnector connector = new OracleJdbcConnector()) {
            connector.initialize(param);
            List<String> databases = connector.getDatabases();
            System.out.println(databases);
        } catch (Exception e) {
            log.error("Failed to get databases", e);
        }
    }

    @Test
    public void testDoGetTables() {
        try (OracleJdbcConnector connector = new OracleJdbcConnector()) {
            connector.initialize(param);
            List<String> tables = connector.getTables("tpcds");
            System.out.println(tables);
        } catch (Exception e) {
            log.error("Failed to get tables", e);
        }
    }

    @Test
    public void testDoGetTableFields() {
        try (OracleJdbcConnector connector = new OracleJdbcConnector()) {
            connector.initialize(param);
            List<TableField> tableFields = connector.getTableFields("tpcds", "CUSTOMER_ADDRESS");
            tableFields.forEach(field -> {
                System.out.println(field.getName() + " " + field.getType() + " ");
            });
        } catch (Exception e) {
            log.error("Failed to get table fields", e);
        }
    }
}
