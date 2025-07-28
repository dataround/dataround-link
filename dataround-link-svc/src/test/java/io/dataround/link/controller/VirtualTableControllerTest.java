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

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.dataround.link.common.entity.res.UserResponse;
import io.dataround.link.entity.Connection;
import io.dataround.link.entity.VirtualField;
import io.dataround.link.entity.res.VirtualTableRes;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for VirtualTableController.
 * Tests virtual table operations and endpoints.
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class VirtualTableControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void list() throws Exception {
        Page<Connection> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(new Connection()));
        mockMvc.perform(get("/api/vtable/list")
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
    public void saveOrUpdate() throws Exception {
        VirtualTableRes vtable = new VirtualTableRes();
        // update test
        boolean isUpdate = true;
        if (isUpdate) {
            vtable.setId(1857003325018865666L);
        }
        vtable.setConnectionId(1856488978576990209L);
        vtable.setDatabase("db1");
        vtable.setTableName("table1");
        vtable.setDescription("desc");
        vtable.setCreateBy(userId);
        JSONObject jsonConfig = new JSONObject();
        jsonConfig.put("broker", "localhost:9092");
        jsonConfig.put("delimiter", ",");
        vtable.setJsonConfig(jsonConfig);
        List<VirtualField> fields = new ArrayList<>() {
            {
                VirtualField field = new VirtualField();
                if (isUpdate) {
                    field.setId(1857010470338748417L);
                    field.setTableId(vtable.getId());
                }
                field.setName("id");
                field.setType("int");
                field.setComment("this is ..");
                field.setNullable(false);
                field.setPrimaryKey(true);
                field.setDefaultValue("1");
                add(field);
                field = new VirtualField();
                if (isUpdate) {
                    field.setId(1857010470351331329L);
                }
                field.setName("name");
                field.setType("string");
                field.setComment("desc");
                field.setNullable(false);
                field.setPrimaryKey(true);
                field.setDefaultValue("tom");
                add(field);
            }
        };
        vtable.setFields(fields);
        UserResponse userInfo = new UserResponse();
        userInfo.setUserId(userId);
        mockMvc.perform(post("/api/vtable/saveOrUpdate")
                        .cookie(genCookie(userId))
                        .content(asJsonString(vtable))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void deleteById() throws Exception {
        Long id = 1L;
        mockMvc.perform(delete("/api/vtable/delete/{id}", id)
                        .cookie(genCookie(userId)))
                .andExpect(status().isOk());
    }
}
