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
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.dataround.link.common.Result;
import io.dataround.link.common.entity.res.UserResponse;
import io.dataround.link.common.controller.BaseController;
import io.dataround.link.entity.User;
import io.dataround.link.service.UserService;
import io.dataround.link.entity.Connection;
import io.dataround.link.entity.Job;
import io.dataround.link.entity.JobInstance;
import io.dataround.link.entity.enums.JobScheduleTypeEnum;
import io.dataround.link.entity.req.JobReq;
import io.dataround.link.entity.res.JobRes;
import io.dataround.link.quartz.SchedulerService;
import io.dataround.link.service.ConnectionService;
import io.dataround.link.service.JobConfigService;
import io.dataround.link.service.JobService;
import io.dataround.link.service.JobInstanceService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Controller for managing job definitions and related operations.
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
@RestController
@RequestMapping("/api/job")
@Slf4j
public class JobController extends BaseController {

    @Autowired
    private JobService jobService;
    @Autowired
    private JobInstanceService jobInstanceService;
    @Autowired
    private SchedulerService schedulerService;
    @Autowired
    private JobConfigService jobConfigService;
    @Autowired
    private ConnectionService connectionService;
    @Autowired
    private UserService userService;


    @GetMapping("/list")
    public Result<Page<JobRes>> list(@Parameter(hidden = true) Page<Job> params, Job jobParams, HttpServletRequest request) {
        params.addOrder(new OrderItem("id", false));
        jobParams.setProjectId(getCurrentProjectId());
        LambdaQueryWrapper<Job> queryWrapper = new LambdaQueryWrapper<>(jobParams);
        // filter by waiting exec
        if ("true".equals(request.getParameter("waitingExec"))) {
            jobParams.setScheduleType(JobScheduleTypeEnum.SCHEDULED.getCode());
            queryWrapper.isNull(Job::getEndTime).or().gt(Job::getEndTime, new Date());
        }
        String sourceConnector = request.getParameter("jobSource");
        if (StringUtils.isNotBlank(sourceConnector)) {
            //where config ->> 'sourceConnector' = 'MySQL';
            queryWrapper.apply("config ->> 'sourceConnector' = {0}", sourceConnector);
        }
        String targetConnector = request.getParameter("jobTarget");
        if (StringUtils.isNotBlank(targetConnector)) {
            //where config ->> 'targetConnector' = 'MySQL';
            queryWrapper.apply("config ->> 'targetConnector' = {0}", targetConnector);
        }
        Page<Job> page = jobService.page(params, queryWrapper);
        Map<Long, JobRes> jobResMap = new LinkedHashMap<>();
        page.getRecords().forEach(job -> jobResMap.put(job.getId(), jobConfigService.getJobVo(job)));

        // get connection name and user name
        Set<Long> creatorIds = new HashSet<>();
        Set<Long> connectionIds = new HashSet<>();
        for (Job job : page.getRecords()) {
            Map<String, Object> configMap = job.getConfig();
            Long sourceConnId = configMap != null ? (Long) configMap.get("sourceConnId") : null;
            Long targetConnId = configMap != null ? (Long) configMap.get("targetConnId") : null;
            connectionIds.add(sourceConnId);
            connectionIds.add(targetConnId);
            creatorIds.add(job.getCreateBy());
            jobResMap.get(job.getId()).setSourceConnId(sourceConnId);
            jobResMap.get(job.getId()).setTargetConnId(targetConnId);
        }
        if (!connectionIds.isEmpty()) {
            Map<Long, String> connectionMap = new HashMap<>();
            List<Connection> connections = connectionService.listByIds(connectionIds);
            connections.forEach(conn -> connectionMap.put(conn.getId(), conn.getName()));
            for (Job job : page.getRecords()) {
                JobRes jobRes = jobResMap.get(job.getId());
                jobRes.setSourceConnectionName(connectionMap.get(jobRes.getSourceConnId()));
                jobRes.setTargetConnectionName(connectionMap.get(jobRes.getTargetConnId()));
            }
        }
        if (!creatorIds.isEmpty()) {
            Map<Long, String> userMap = new HashMap<>();
            List<User> users = userService.listByIds(creatorIds);
            users.forEach(user -> userMap.put(user.getId(), user.getName()));
            for (JobRes jobRes : jobResMap.values()) {
                jobRes.setUpdateUserName(userMap.get(jobRes.getUpdateBy()));
            }
        }
        Page<JobRes> pageJobRes = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        pageJobRes.setRecords(new ArrayList<>(jobResMap.values()));
        return  Result.success(pageJobRes);
    }

    @GetMapping("/{id}")
    public Result<JobRes> get(@PathVariable Long id) {
        Job job = jobService.getById(id);
        return Result.success(jobConfigService.getJobVo(job));
    }

    @PostMapping("/saveOrUpdate")
    public Result<Boolean> saveOrUpdate(@RequestBody JobReq jobVo) {
        UserResponse currentUser = getCurrentUser();
        Date now = new Date();
        Job job = jobVo;
        Long id = jobVo.getId();
        if (id == null) {
            job.setId(IdWorker.getId());
            job.setProjectId(getCurrentProjectId());
            job.setCreateBy(currentUser.getUserId());
            job.setCreateTime(now);
            job.setUpdateBy(currentUser.getUserId());
            job.setUpdateTime(now);
        }
        job.setUpdateBy(currentUser.getUserId());
        job.setUpdateTime(now);
        // Convert JobRes to Map<String, Object> for JSONB storage
        String jsonString = JSONObject.toJSONString(jobVo);
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        Map<String, Object> configMap = jsonObject;
        job.setConfig(configMap);
        boolean bool = jobService.saveOrUpdate(job);
        schedulerService.scheduleJob(job);
        return Result.success(bool);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        Assert.notNull(id, "Job id should not be null");
        // check job used or not
        JobInstance instance = new JobInstance();
        instance.setJobId(id);
        long count = jobInstanceService.count(new QueryWrapper<>(instance));
        if (count > 0) {
            return Result.error("Please delete this job's instance first");
        }
        Job job = jobService.getById(id);
        if (job.getScheduleType() == JobScheduleTypeEnum.SCHEDULED.getCode()) {
            log.info("delete cron job {} from scheduler", id);
            schedulerService.deleteJob(job);
        }
        log.info("delete Job id:{}", id);
        boolean bool = jobService.removeById(id);
        return Result.success(bool);
    }

    @PostMapping("/execute/{id}")
    public Result<Boolean> execute(@PathVariable Long id, HttpServletRequest request) {
        Job job = jobService.getById(id);
        if (job.getScheduleType() == JobScheduleTypeEnum.SCHEDULED.getCode()) {
            log.warn("Not support cron job, jobId:{}", id);
            return Result.error();
        }
        // temporary modify executeType, it will not save to db
        if (job.getScheduleType() == JobScheduleTypeEnum.NOT_RUN.getCode()) {
            job.setScheduleType(JobScheduleTypeEnum.RUN_NOW.getCode());
        }
        Long currentUserId = getCurrentUserId();
        schedulerService.scheduleJob(job, currentUserId);
        return Result.success(true);
    }
}
