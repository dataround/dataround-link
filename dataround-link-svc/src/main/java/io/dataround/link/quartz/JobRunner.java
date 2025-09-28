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

import java.util.Date;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.quartz.QuartzJobBean;

import io.dataround.link.SpringContextUtil;
import io.dataround.link.entity.Job;
import io.dataround.link.entity.JobInstance;
import io.dataround.link.entity.enums.JobInstanceStatusEnum;
import io.dataround.link.service.JobInstanceService;
import io.dataround.link.service.JobService;
import lombok.extern.slf4j.Slf4j;

/**
 * Quartz job implementation for executing scheduled jobs.
 * Handles job execution and error handling.
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
@Slf4j
public class JobRunner extends QuartzJobBean {

    @Override
    protected void executeInternal(@NonNull JobExecutionContext context) throws JobExecutionException {
        Long jobId = Long.parseLong(context.getJobDetail().getKey().getName());
        log.info("Start execute job: {}, ScheduledFireTime: {}", jobId, context.getScheduledFireTime());
        JobService jobService = SpringContextUtil.getBean(JobService.class);
        Job job = jobService.getById(jobId);
        // save job instance
        JobInstanceService jobInstanceService = SpringContextUtil.getBean(JobInstanceService.class);
        JobInstance instance = new JobInstance();
        instance.setJobId(job.getId());
        instance.setProjectId(job.getProjectId());
        instance.setStatus(JobInstanceStatusEnum.RUNNING.getCode());
        instance.setReadCount(0L);
        instance.setWriteCount(0L);
        instance.setReadQps(0.0);
        instance.setWriteQps(0.0);
        instance.setReadBytes(0L);
        instance.setWriteBytes(0L);
        Date now = new Date();
        instance.setUpdateTime(now);
        instance.setStartTime(now);
        Long userId = job.getCreateBy();
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        if (jobDataMap.containsKey("userId")) {
            userId = jobDataMap.getLong("userId");
        }
        instance.setUpdateBy(userId);
        jobInstanceService.saveOrUpdate(instance);
        try {
            // submit to seatunnel
            jobInstanceService.execute(job.getCreateBy(), job.getId(), instance.getId());
        } catch (Throwable e) {
            log.error("instance {} execute failed", instance.getId(), e);
            Date endTime = new Date();
            instance.setEndTime(endTime);
            instance.setStatus(JobInstanceStatusEnum.FAILURE.getCode());
            // receive exception stack trace info/
            String stackTrace = ExceptionUtils.getStackTrace(e);
            instance.setLogContent(stackTrace);
            jobInstanceService.saveOrUpdate(instance);
        } finally {

        }
    }
}
