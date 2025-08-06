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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

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
        String config = jobConfigService.getJobJson(vo, instanceId);
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
    
    public void executeBySeaTunnel(String jobConfig, Long instanceId) {
        // Submit job using REST API
        String seatunnelId = seaTunnelRestClient.submitJob(jobConfig);
        // Update job instance with seatunnelId
        JobInstance jobInstance = jobInstanceMapper.selectById(instanceId);
        jobInstance.setSeatunnelId(seatunnelId);
        jobInstance.setStatus(JobInstanceStatusEnum.RUNNING.getCode());
        jobInstanceMapper.updateById(jobInstance);
        // Add job to status checker
        jobStatusChecker.addJobToMonitor(jobInstance);
    }

    public void executeByFileSync(JobRes jobVo, Long instanceId) {
        fileSyncService.executeFileSync(jobVo, instanceId);
    }
}
