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

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.dataround.link.entity.Connection;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for ConnectionController.
 * Tests database connection operations and endpoints.
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ConnectionControllerTest extends ConnectionBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void list() throws Exception {
        Page<Connection> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(new Connection()));
        mockMvc.perform(get("/api/connection/list")
                        .cookie(genCookie(userId))
                        .param("current", String.valueOf(page.getCurrent()))
                        .param("size", String.valueOf(page.getSize())))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    log.info(result.getResponse().getContentAsString());
                    Assertions.assertTrue(result.getResponse().getContentAsString().contains("total"));
                });
    }

    @Test
    public void saveOrUpdateMysql() throws Exception {
        saveOrUpdateConnection(buildMySQL());
    }

    @Test
    public void saveOrUpdateOracle() throws Exception {
        saveOrUpdateConnection(buildOracle());
    }
    @Test
    public void saveOrUpdateSQLServer() throws Exception {
        saveOrUpdateConnection(buildMSSQL());
    }
    @Test
    public void saveOrUpdateTidb() throws Exception {
        saveOrUpdateConnection(buildTidb());
    }

    @Test
    public void saveOrUpdateHive() throws Exception {
        saveOrUpdateConnection(buildHiveMetaStore());
    }

    @Test
    public void testMysqlConnection() throws Exception {
        testConnection(buildMySQL());
    }

    @Test
    public void testOracleConnection() throws Exception {
        testConnection(buildOracle());
    }

    @Test
    public void testSQLServerConnection() throws Exception {
        testConnection(buildMSSQL());
    }

    @Test
    public void testTidbConnection() throws Exception {
        testConnection(buildTidb());
    }

    @Test
    public void testHiveMetastoreConnection() throws Exception {
        testConnection(buildHiveMetaStore());
    }

    @Test
    public void testHiveJdbcConnection() throws Exception {
        testConnection(buildHiveJDBC());
    }

    private void saveOrUpdateConnection(Connection connection) throws Exception {
        mockMvc.perform(post("/api/connection/saveOrUpdate")
                        .cookie(genCookie(userId))
                        .content(BaseControllerTest.asJsonString(connection))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    private void testConnection(Connection connection) throws Exception {
        mockMvc.perform(post("/api/connection/test")
                        .cookie(genCookie(userId))
                        .content(BaseControllerTest.asJsonString(connection))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void getDatabases() throws Exception {
        // mysql
        //Long id = 1857318074881433601L;
        // hive
        Long id = 1857367719519252481L;
        mockMvc.perform(get("/api/connection/{id}/dbs", id)
                        .cookie(genCookie(userId)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    log.info(result.getResponse().getContentAsString());
                });
    }

    @Test
    public void getTableNames() throws Exception {
        Long id = 1857367719519252481L;
        String db = "hive_test08";
        mockMvc.perform(get("/api/connection/{id}/{databaseName}/tables", id, db)
                        .cookie(genCookie(userId)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    log.info(result.getResponse().getContentAsString());
                });
    }

    @Test
    public void getTableFields() throws Exception {
        Long id = 1857367719519252481L;
        String db = "hive_test08";
        String tableName = "users";
        mockMvc.perform(get("/api/connection/{id}/{databaseName}/{tableName}/columns", id, db, tableName)
                        .cookie(genCookie(userId)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    log.info(result.getResponse().getContentAsString());
                });
    }

    @Test
    public void deleteById() throws Exception {
        Long id = 1L;
        mockMvc.perform(delete("/api/connection/delete/{id}", id)
                        .cookie(genCookie(userId)))
                .andExpect(status().isOk());
    }

}
