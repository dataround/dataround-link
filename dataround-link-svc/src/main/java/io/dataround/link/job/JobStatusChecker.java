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

package io.dataround.link.job;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.dataround.link.entity.JobInstance;
import io.dataround.link.entity.enums.JobInstanceStatusEnum;
import io.dataround.link.service.JobInstanceService;
import io.dataround.link.utils.SeaTunnelRestClient;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * Component for checking job statuses.
 * Monitors all running jobs and updates their status in the database.
 */
@Slf4j
@Component
public class JobStatusChecker {

    @Autowired
    private JobInstanceService jobInstanceService;
    @Autowired
    private SeaTunnelRestClient seaTunnelRestClient;
    private final long checkIntervalSeconds = 1000L;

    private final ExecutorService executorService;
    private final ConcurrentHashMap<String, JobInstance> runningJobs;
    
    public JobStatusChecker() {
        this.executorService = Executors.newFixedThreadPool(5);
        this.runningJobs = new ConcurrentHashMap<>();
    }
    
    @PostConstruct
    public void init() {
        // Load all unfinished jobs from database on startup
        List<JobInstance> unfinishedJobs = jobInstanceService.selectUnfinishedJobs();
        for (JobInstance job : unfinishedJobs) {
            if (job.getSeatunnelId() != null) {
                runningJobs.put(job.getSeatunnelId(), job);
            }
        }
        log.info("Loaded {} unfinished jobs on startup", unfinishedJobs.size());
    }
    
    /**
     * Add a job to be monitored
     * @param jobInstance Job instance to monitor
     */
    public void addJobToMonitor(JobInstance jobInstance) {
        if (jobInstance.getSeatunnelId() != null) {
            runningJobs.put(jobInstance.getSeatunnelId(), jobInstance);
        }
    }
    
    /**
     * Scheduled task to check job statuses every 30 seconds
     */
    @Scheduled(fixedRate = checkIntervalSeconds)
    public void checkJobStatuses() {
        runningJobs.forEach((jobEngineId, jobInstance) -> {
            executorService.submit(() -> {
                try {
                    String status = seaTunnelRestClient.getJobStatus(jobEngineId);
                    updateJobStatus(jobInstance, status);
                } catch (Exception e) {
                    log.error("Failed to check status for job {}", jobEngineId, e);
                }
            });
        });
    }
    
    private void updateJobStatus(JobInstance jobInstance, String status) {
        try {
            switch (status.toUpperCase()) {
                case "FINISHED":
                    jobInstance.setStatus(JobInstanceStatusEnum.SUCCESS.getCode());
                    runningJobs.remove(jobInstance.getSeatunnelId());
                    break;
                case "FAILED":
                    jobInstance.setStatus(JobInstanceStatusEnum.FAILURE.getCode());
                    runningJobs.remove(jobInstance.getSeatunnelId());
                    break;
                case "CANCELED":
                case "CANCELLING":
                    jobInstance.setStatus(JobInstanceStatusEnum.CANCELLED.getCode());
                    break;
                default:
                    jobInstance.setStatus(JobInstanceStatusEnum.RUNNING.getCode());
            }
            jobInstance.setUpdateTime(new Date());
            // update job instance
            jobInstanceService.updateById(jobInstance);
            // If job is finished, update metrics
            if (JobInstanceStatusEnum.RUNNING.getCode() != jobInstance.getStatus()) {
                jobInstance.setEndTime(new Date());
                jobInstanceService.updateJobMetrics(jobInstance);
            }
        } catch (Exception e) {
            log.error("Failed to update status for job {}", jobInstance.getSeatunnelId(), e);
        }
    }
} 