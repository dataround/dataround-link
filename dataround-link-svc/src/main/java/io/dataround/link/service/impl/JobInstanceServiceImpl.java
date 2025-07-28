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

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;

import io.dataround.link.entity.Job;
import io.dataround.link.entity.JobInstance;
import io.dataround.link.entity.enums.JobInstanceStatusEnum;
import io.dataround.link.entity.enums.JobTypeEnum;
import io.dataround.link.entity.req.JobInstanceReq;
import io.dataround.link.entity.res.JobRes;
import io.dataround.link.job.JobStatusChecker;
import io.dataround.link.mapper.JobInstanceMapper;
import io.dataround.link.mapper.JobMapper;
import io.dataround.link.service.FileSyncService;
import io.dataround.link.service.JobConfigService;
import io.dataround.link.service.JobInstanceService;
import io.dataround.link.utils.SeaTunnelRestClient;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the JobInstanceService interface.
 * Provides concrete implementation for managing job instances, including execution,
 * monitoring, and cancellation of jobs using SeaTunnel engine.
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
@Slf4j
@Service
public class JobInstanceServiceImpl extends ServiceImpl<JobInstanceMapper, JobInstance> implements JobInstanceService {

    @Autowired
    private JobMapper jobMapper;
    @Autowired
    private JobInstanceMapper jobInstanceMapper;
    @Autowired
    private JobConfigService jobConfigService;

    //@Lazy annotation for: circular references is discouraged
    @Lazy
    @Autowired
    private JobStatusChecker jobStatusChecker;
    @Autowired
    private SeaTunnelRestClient seaTunnelRestClient;
    @Lazy
    @Autowired
    private FileSyncService fileSyncService;

    @Override
    public Page<JobInstance> selectPage(JobInstanceReq jobInstanceRequest) {
        List<JobInstance> instances = jobInstanceMapper.selectPage(jobInstanceRequest);
        long count = jobInstanceMapper.selectCount(jobInstanceRequest);
        return new Page<JobInstance>().setTotal(count).setRecords(instances);
    }

    @Override
    public void execute(Long userId, Long jobId, Long instanceId) {
        Job job = jobMapper.selectById(jobId);
        JobRes vo = jobConfigService.getJobVo(job);
        String config = jobConfigService.getJobJson(vo);
        log.debug("Job Config:\n {}", config);
        if (job.getJobType() == JobTypeEnum.FILESYNC.getCode()) {
            executeByFileSync(vo, instanceId);
        } else {
            executeBySeaTunnel(config, instanceId);
        }
    }

    @Override
    public boolean cancel(Long instanceId) {
        JobInstance jobInstance = jobInstanceMapper.selectById(instanceId);
        try {
            seaTunnelRestClient.stopJob(jobInstance.getSeatunnelId());
            jobInstance.setStatus(JobInstanceStatusEnum.CANCELLED.getCode());
            jobInstance.setEndTime(new Date());
            jobInstanceMapper.updateById(jobInstance);
            return true;
        } catch (Exception e) {
            log.error("Job instance {} cancel failed.", instanceId, e);
            return false;
        }
    }

    @Override
    public List<JobInstance> selectUnfinishedJobs() {
        LambdaQueryWrapper<JobInstance> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(JobInstance::getStatus, JobInstanceStatusEnum.SUBMITTED.getCode(), JobInstanceStatusEnum.RUNNING.getCode());
        return this.list(queryWrapper);
    }

    @Override
    public void updateJobMetrics(@NonNull JobInstance jobInstance) {
        String jobEngineId = jobInstance.getSeatunnelId();
        JsonNode metricsJson = seaTunnelRestClient.getJobMetrics(jobEngineId);
        parseAndUpdateMetrics(jobInstance, metricsJson);
        // update JobInstance to database
        Date now = new Date();
        jobInstance.setUpdateTime(now);
        this.updateById(jobInstance);
    }

    private void parseAndUpdateMetrics(JobInstance jobInstance, JsonNode metricsJson) {
        try {
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
            // update JobInstance metrics
            jobInstance.setReadCount(totalReadCount);
            jobInstance.setWriteCount(totalWriteCount);
            jobInstance.setReadQps(totalReadQps);
            jobInstance.setWriteQps(totalWriteQps);
            jobInstance.setReadBytes(totalReadBytes);
            jobInstance.setWriteBytes(totalWriteBytes);
        } catch (Exception e) {
            log.error("Error parsing metrics JSON for job instance {}", jobInstance.getId(), e);
        }
    }

    public void executeBySeaTunnel(String configContent, Long instanceId) {
        try {
            // Submit job using REST API
            String jobEngineId = seaTunnelRestClient.submitJob(configContent);
            // Update job instance with engine ID
            JobInstance jobInstance = jobInstanceMapper.selectById(instanceId);
            jobInstance.setSeatunnelId(jobEngineId);
            jobInstance.setStatus(JobInstanceStatusEnum.RUNNING.getCode());
            jobInstanceMapper.updateById(jobInstance);
            // Add job to status checker
            jobStatusChecker.addJobToMonitor(jobInstance);
        } catch (Exception e) {
            log.error("Job execution submission failed.", e);
            JobInstance jobInstance = jobInstanceMapper.selectById(instanceId);
            jobInstance.setStatus(JobInstanceStatusEnum.FAILURE.getCode());
            jobInstance.setEndTime(new Date());
            String stackTrace = ExceptionUtils.getStackTrace(e);
            jobInstance.setLogContent(stackTrace);
            jobInstanceMapper.updateById(jobInstance);
        }
    }

    public void executeByFileSync(JobRes jobVo, Long instanceId) {
        try {
            // Submit job using REST API
            fileSyncService.executeFileSync(jobVo, instanceId);
        } catch (Exception e) {
            log.error("Job execution submission failed.", e);
        }
    }
}
