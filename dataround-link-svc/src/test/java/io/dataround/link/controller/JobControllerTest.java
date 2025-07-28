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
import io.dataround.link.common.entity.res.UserResponse;
import io.dataround.link.entity.Connection;
import io.dataround.link.entity.Job;
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

import java.util.Collections;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for JobController.
 * Tests job operations and endpoints.
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class JobControllerTest extends BaseControllerTest {
    private Long userId;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        userId = 1L;
    }

    @Test
    public void list() throws Exception {
        Page<Connection> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(new Connection()));
        mockMvc.perform(get("/api/job/list")
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
        Job job = new Job();
        job.setId(1L);
        job.setJobType(1);
        job.setName("job1");
        job.setDescription("desc");
        Date now = new Date();
        job.setCreateTime(now);
        job.setUpdateTime(now);
        job.setCreateBy(userId);
        UserResponse userInfo = new UserResponse();
        userInfo.setUserId(userId);
        mockMvc.perform(post("/api/job/saveOrUpdate")
                        .cookie(genCookie(userId))
                        .content(asJsonString(job))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void deleteById() throws Exception {
        Long id = 1L;
        mockMvc.perform(delete("/api/job/delete/{id}", id)
                        .cookie(genCookie(userId)))
                .andExpect(status().isOk());
    }

    @Test
    public void execute() throws Exception {
        Long id = 1861206029380005890L;
        mockMvc.perform(post("/api/job/execute/{id}", id)
                        .cookie(genCookie(userId)))
                .andExpect(status().isOk());
    }
}
