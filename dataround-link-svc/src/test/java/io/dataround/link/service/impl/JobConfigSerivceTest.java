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

package io.dataround.link.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import io.dataround.link.entity.enums.JobTypeEnum;
import io.dataround.link.entity.res.JobRes;
import io.dataround.link.job.JobConfigService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Test class for JobConfigService.
 * Tests job configuration operations and service methods.
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class JobConfigSerivceTest {

    @Autowired
    private JobConfigService jobConfigService;

    @Test
    public void getJobConfig() {
        JobRes request = new JobRes();
        request.setId(123L);
        request.setName("job123");
        request.setJobType(JobTypeEnum.BATCH.getCode());
        request.setSourceConnId(1837273009577148418L);
        request.setTargetConnId(1839579992795185153L);
        JSONArray array = new JSONArray();
        JSONObject obj = new JSONObject();
        obj.put("sourceDbName", "test");
        obj.put("sourceTable", "table1");
        obj.put("targetDbName", "tpcds");
        obj.put("targetTable", "table1");
        array.add(obj);
        obj = new JSONObject();
        obj.put("sourceDbName", "test");
        obj.put("sourceTable", "table2");
        obj.put("targetDbName", "tpcds");
        obj.put("targetTable", "table2");
        array.add(obj);
        //request.setTableMapping(array);
        String config = jobConfigService.getJobJson(request, 1L);
        System.out.println(config);
    }
}
