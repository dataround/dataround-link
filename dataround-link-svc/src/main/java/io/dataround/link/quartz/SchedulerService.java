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
import java.util.List;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.DirectSchedulerFactory;
import org.quartz.impl.SchedulerRepository;
import org.quartz.simpl.RAMJobStore;
import org.quartz.simpl.SimpleThreadPool;
import org.quartz.spi.JobStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.dataround.link.entity.Job;
import io.dataround.link.entity.enums.JobScheduleTypeEnum;
import io.dataround.link.service.JobService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class for managing job scheduling using Quartz.
 * Provides methods to schedule, pause, resume, and delete jobs.
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
@Slf4j
@Service
public class SchedulerService {

    @Value("${dataround.link.scheduler.threadPoolSize:10}")
    private Integer threadCount;
    private final String schedulerName = "dataroundScheduler";
    private final String schedulerInstanceId = "dataroundInstance";

    @Autowired
    private JobService jobService;
    private DirectSchedulerFactory sf = DirectSchedulerFactory.getInstance();

    @PostConstruct
    public void start() {
        try {
            // check if scheduler exists, if exists, shutdown and remove it, only for hot deployment during development
            try {
                Scheduler existingScheduler = sf.getScheduler(schedulerName);
                if (existingScheduler != null) {
                    log.info("Found existing scheduler '{}', shutting down for hot deployment", schedulerName);
                    existingScheduler.shutdown(true);
                    SchedulerRepository.getInstance().remove(schedulerName);
                }
            } catch (SchedulerException e) {
                // scheduler not exists, ignore exception
                log.debug("No existing scheduler found: {}", e.getMessage());
            }
            
            JobStore jobStore = new RAMJobStore();
            SimpleThreadPool threadPool = new SimpleThreadPool(threadCount, Thread.NORM_PRIORITY);
            sf.createScheduler(schedulerName, schedulerInstanceId, threadPool, jobStore);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
        List<Job> jobs = jobService.listForScheduler(JobScheduleTypeEnum.SCHEDULED.getCode());
        log.info("{} job will be loaded for scheduling", jobs.size());
        jobs.forEach(this::scheduleJob);
        try {
            getScheduler().start();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    public void scheduleJob(Job job) {
        scheduleJob(job, null);
    }

    public void scheduleJob(Job job, Long userId) {
        String jobKey = getJobKey(job);
        ScheduleBuilder<? extends Trigger> scheduleBuilder = null;
        if (job.getScheduleType() == JobScheduleTypeEnum.RUN_NOW.getCode()) {
            scheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
        } else if (job.getScheduleType() == JobScheduleTypeEnum.SCHEDULED.getCode()) {
            scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCron());
        } else {
            return;
        }
        TriggerBuilder<? extends Trigger> triggerBuilder = TriggerBuilder.newTrigger().withIdentity(jobKey).withSchedule(scheduleBuilder);
        // If startTime less than now, job will be scheduled immediately, that cause repeat schedule
        if (job.getStartTime() != null && job.getStartTime().getTime() > System.currentTimeMillis()) {
            triggerBuilder.startAt(job.getStartTime());
        } else if (job.getEndTime() != null) {
            triggerBuilder.endAt(job.getEndTime());
        }
        Trigger trigger = triggerBuilder.build();
        JobDetail jobDetail = JobBuilder.newJob(JobRunner.class).withIdentity(jobKey).storeDurably(false).build();
        if (userId != null) {
            jobDetail.getJobDataMap().put("userId", userId);
        }
        try {
            TriggerKey triggerKey = trigger.getKey();
            if (getScheduler().checkExists(triggerKey)) {
                log.info("Job already exists in scheduler, jobId: {}, triggerKey: {}", job.getId(), triggerKey);
                Date date = getScheduler().rescheduleJob(triggerKey, trigger);
                if (date == null) {
                    log.warn("rescheduleJob maybe failed. jobKey:{} ", jobKey);
                }
            } else {
                getScheduler().scheduleJob(jobDetail, trigger);
            }
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteJob(Job job) {
        JobKey jobKey = new JobKey(getJobKey(job));
        try {
            JobDetail jobDetail = getScheduler().getJobDetail(jobKey);
            if (jobDetail == null) {
                log.warn("jobDetail is null, jobKey:{}", jobKey);
                return;
            }
            getScheduler().deleteJob(jobKey);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    private String getJobKey(Job job) {
        return job.getId().toString();
    }

    private Scheduler getScheduler() {
        try {
            return sf.getScheduler(schedulerName);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }
}
