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

package io.dataround.link.connector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import io.dataround.link.common.connector.Param;

/**
 * Test class for HiveConnector
 * 
 * @author yuehan124@gmail.com
 * @since 2025-06-09
 */
public class HiveConnectorTest {

    private Param param;
    private HiveConnector connector;

    @Before
    void setUp() {
        connector = new HiveConnector();
        param = new Param();
        param.setHost("localhost");
        param.setPort(10000);
        param.setUser("hive");
        param.setPassword("hive");
        Map<String, String> config = new HashMap<>();
        config.put("driver", "org.apache.hive.jdbc.HiveDriver");
        param.setConfig(config);
    }

    @Test
    void testGetName() {
        assertEquals("Hive", connector.getName());
    }

    @Test
    void testInitializeWithDefaultUrl() throws Exception {
        // Remove url from config to test default URL generation
        param.getConfig().remove("url");

        connector.initialize(param);

        // Verify that the connector was initialized
        assertNotNull(connector.getParam());
        assertEquals("localhost", connector.getParam().getHost());
        assertEquals(Integer.valueOf(10000), connector.getParam().getPort());
    }

    @Test
    void testInitializeWithCustomUrl() throws Exception {
        param.getConfig().put("url", "jdbc:hive2://custom-host:10001/custom_db");

        connector.initialize(param);

        // Verify that the connector was initialized
        assertNotNull(connector.getParam());
        assertEquals("jdbc:hive2://custom-host:10001/custom_db", connector.getParam().getConfig().get("url"));
    }

    @Test
    void testInitializeWithDefaultDriver() throws Exception {
        // Remove driver from config to test default driver
        param.getConfig().remove("driver");
        connector.initialize(param);

        // Verify that default driver was set
        assertEquals("org.apache.hive.jdbc.HiveDriver", connector.getParam().getConfig().get("driver"));
    }
}