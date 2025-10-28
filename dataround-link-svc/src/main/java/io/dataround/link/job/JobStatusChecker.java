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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import io.dataround.link.entity.JobInstance;
import io.dataround.link.entity.enums.JobInstanceStatusEnum;
import io.dataround.link.service.JobInstanceService;
import io.dataround.link.utils.SeaTunnelRestClient;
import lombok.extern.slf4j.Slf4j;

/**
 * Component for checking job statuses.
 * Monitors all running jobs and updates their status in the database.
 * 
 * @author yuehan124@gmail.com
 * @since 2025-09-07
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
    private final ConcurrentHashMap<String, Boolean> checkingJobs;

    public JobStatusChecker() {
        this.executorService = Executors.newFixedThreadPool(5);
        this.runningJobs = new ConcurrentHashMap<>();
        this.checkingJobs = new ConcurrentHashMap<>();
    }

    public void start() {
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
     *
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
        runningJobs.forEach((seatunnelId, jobInstance) -> {
            executorService.submit(() -> {
                try {
                    // Check if this job is already being checked
                    if (checkingJobs.putIfAbsent(seatunnelId, true) != null) {
                        return;
                    }
                    JsonNode jobDetail = seaTunnelRestClient.getJobDetail(seatunnelId);
                    updateJobStatus(jobInstance, jobDetail);
                } catch (Exception e) {
                    log.error("Failed to check status for job {}", seatunnelId, e);
                } finally {
                    // Remove the checking flag when done
                    checkingJobs.remove(seatunnelId);
                }
            });
        });
    }

    private void updateJobStatus(JobInstance jobInstance, JsonNode jobDetail) {
        JsonNode jobStatus = jobDetail.get("jobStatus");
        if (jobStatus == null) {
            return;
        }
        String status = jobStatus.asText();        
        boolean changed = false;
        try {
            switch (status.toUpperCase()) {
                case "FINISHED":
                    changed = jobInstance.getStatus() != JobInstanceStatusEnum.SUCCESS.getCode();
                    jobInstance.setStatus(JobInstanceStatusEnum.SUCCESS.getCode());
                    runningJobs.remove(jobInstance.getSeatunnelId());
                    break;
                case "FAILED":
                    changed = jobInstance.getStatus() != JobInstanceStatusEnum.FAILURE.getCode();
                    jobInstance.setStatus(JobInstanceStatusEnum.FAILURE.getCode());
                    runningJobs.remove(jobInstance.getSeatunnelId());
                    break;
                case "CANCELED":
                case "CANCELLING":
                    changed = jobInstance.getStatus() != JobInstanceStatusEnum.CANCELLED.getCode();
                    jobInstance.setStatus(JobInstanceStatusEnum.CANCELLED.getCode());
                    break;
                default:
                    changed = jobInstance.getStatus() != JobInstanceStatusEnum.RUNNING.getCode();
                    jobInstance.setStatus(JobInstanceStatusEnum.RUNNING.getCode());
            }
            JsonNode metrics = jobDetail.get("metrics");
            changed = parseAndUpdateMetrics(jobInstance, metrics) || changed;
            jobInstance.setUpdateTime(new Date());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            StringBuilder logContent = new StringBuilder();
            // Add job start time at the beginning
            if (jobInstance.getStartTime() != null) {
                logContent.append("=== Job Start Time: " + sdf.format(jobInstance.getStartTime()) + " ===\n");
            }
            // Add table-level metrics information
            if (metrics != null) {
                String tableMetricsInfo = buildTableMetricsInfo(metrics);
                if (!tableMetricsInfo.isEmpty()) {
                    logContent.append("=== Table Metrics Info ===\n");
                    logContent.append(tableMetricsInfo);
                }
            }
            // If job is finished, update end time and add logs
            if (JobInstanceStatusEnum.SUBMITTED.getCode() != jobInstance.getStatus() && JobInstanceStatusEnum.RUNNING.getCode() != jobInstance.getStatus()) {
                jobInstance.setEndTime(new Date());
                // Add job end time at the end
                logContent.append("=== Job End Time: " + sdf.format(jobInstance.getEndTime()) + " ===\n\n");

                JsonNode errorMsg = jobDetail.get("errorMsg");
                String logs = seaTunnelRestClient.getJobLogs(jobInstance.getSeatunnelId());
                logContent.append("=== Job Detail Logs ===\n" + (errorMsg != null ? errorMsg.asText() + "\n\n" + logs : logs));

                jobInstance.setLogContent(logContent.toString());
                changed = true;
            } else {
                // For running jobs, update log content with current metrics
                jobInstance.setLogContent(logContent.toString());
            }
            // update job instance only if properties changed, avoid unnecessary updates, especially for streaming jobs
            if (changed) {
                jobInstanceService.updateById(jobInstance);
            }
        } catch (Exception e) {
            log.error("Failed to update status for job {}", jobInstance.getId(), e);
        }
    }

    private boolean parseAndUpdateMetrics(JobInstance jobInstance, JsonNode metricsJson) {
        // aggregate metrics of all pipelines
        long totalReadCount = 0;
        long totalWriteCount = 0;
        double totalReadQps = 0.0;
        double totalWriteQps = 0.0;
        long totalReadBytes = 0;
        long totalWriteBytes = 0;
        // process read count
        if (metricsJson.get("SourceReceivedCount") != null) {
            totalReadCount = metricsJson.get("SourceReceivedCount").asLong();
        }
        // process write count
        if (metricsJson.get("SinkWriteCount") != null) {
            totalWriteCount = metricsJson.get("SinkWriteCount").asLong();
        }
        // process read QPS
        if (metricsJson.get("SourceReceivedQPS") != null) {
            totalReadQps = metricsJson.get("SourceReceivedQPS").asDouble();
        }
        // process write QPS
        if (metricsJson.get("SinkWriteQPS") != null) {
            totalWriteQps = metricsJson.get("SinkWriteQPS").asDouble();
        }
        // process read bytes
        if (metricsJson.get("SourceReceivedBytes") != null) {
            totalReadBytes = metricsJson.get("SourceReceivedBytes").asLong();
        }
        // process write bytes
        if (metricsJson.get("SinkWriteBytes") != null) {
            totalWriteBytes = metricsJson.get("SinkWriteBytes").asLong();
        }
        // ignore qps and bytes, they are changed always
        boolean changed = jobInstance.getReadCount() != totalReadCount || jobInstance.getWriteCount() != totalWriteCount
                || jobInstance.getReadBytes() != totalReadBytes || jobInstance.getWriteBytes() != totalWriteBytes;
        // update JobInstance metrics
        jobInstance.setReadCount(totalReadCount);
        jobInstance.setWriteCount(totalWriteCount);
        jobInstance.setReadQps(totalReadQps);
        jobInstance.setWriteQps(totalWriteQps);
        jobInstance.setReadBytes(totalReadBytes);
        jobInstance.setWriteBytes(totalWriteBytes);
        return changed;
    }

    /**
     * Build table-level metrics information from metrics JSON
     *
     * @param metricsJson the metrics JSON node
     * @return formatted table metrics information
     */
    private String buildTableMetricsInfo(JsonNode metricsJson) {
        StringBuilder tableInfo = new StringBuilder();
        // Process TableSourceReceivedCount
        if (metricsJson.get("TableSourceReceivedCount") != null) {
            JsonNode sourceCount = metricsJson.get("TableSourceReceivedCount");
            if (sourceCount.isObject()) {
                sourceCount.fields().forEachRemaining(entry -> {
                    tableInfo.append("Table ").append(entry.getKey()).append(" received: ")
                             .append(entry.getValue().asText()).append(" rows\n");
                });
            }
        }

        // Process TableSinkWriteCount
        if (metricsJson.get("TableSinkWriteCount") != null) {
            JsonNode sinkCount = metricsJson.get("TableSinkWriteCount");
            if (sinkCount.isObject()) {
                sinkCount.fields().forEachRemaining(entry -> {
                    tableInfo.append("Table ").append(entry.getKey()).append(" written: ")
                             .append(entry.getValue().asText()).append(" rows\n");
                });
            }
        }

        return tableInfo.toString();
    }

}