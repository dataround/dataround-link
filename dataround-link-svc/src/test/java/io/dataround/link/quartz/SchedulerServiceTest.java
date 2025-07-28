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

package io.dataround.link.quartz;

import io.dataround.link.entity.Job;
import io.dataround.link.entity.enums.JobScheduleTypeEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Test class for SchedulerService.
 * Tests job scheduling operations and service methods.
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SchedulerServiceTest {

    @Autowired
    private SchedulerService schedulerService;

    @Test
    public void testScheduleNewJob() {
        Job job = new Job();
        job.setScheduleType(JobScheduleTypeEnum.SCHEDULED.getCode());
        job.setCron("*/2 * * * * ?");
        schedulerService.scheduleJob(job);
    }
}
