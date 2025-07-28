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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.dataround.link.common.Result;
import io.dataround.link.common.entity.res.UserResponse;
import io.dataround.link.common.controller.BaseController;
import io.dataround.link.entity.Job;
import io.dataround.link.entity.JobInstance;
import io.dataround.link.entity.req.JobInstanceReq;
import io.dataround.link.entity.res.JobInstanceRes;
import io.dataround.link.service.JobInstanceService;
import io.dataround.link.service.JobService;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for managing job instances and related operations.
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
@RestController
@RequestMapping("/api/instance")
@Slf4j
public class JobInstanceController extends BaseController {

    @Autowired
    private JobService jobService;

    @Autowired
    private JobInstanceService jobInstanceService;

    @PostMapping("/list")
    public Result<Page<JobInstanceRes>> list(@RequestBody JobInstanceReq jobInstanceRequest) {
        jobInstanceRequest.setProjectId(getCurrentProjectId());
        Page<JobInstance> page = jobInstanceService.selectPage(jobInstanceRequest);
        if (page.getRecords().isEmpty()) {
            return Result.success(new Page<>());
        }
        // get job name
        Collection<Long> jobIds = page.getRecords().stream().map(JobInstance::getJobId).distinct().collect(Collectors.toList());
        List<Job> jobs = jobService.listByIds(jobIds);
        Map<Long, String> jobMap = jobs.stream().collect(Collectors.toMap(Job::getId, Job::getName));
        // convert to JobInstanceRes
        List<JobInstanceRes> jobInstanceResList = new ArrayList<>();
        page.getRecords().forEach(instance -> {
            String jobName = jobMap.get(instance.getJobId());
            JobInstanceRes jobInstanceRes = new JobInstanceRes();
            BeanUtils.copyProperties(instance, jobInstanceRes);
            jobInstanceRes.setJobName(jobName);
            jobInstanceResList.add(jobInstanceRes);
        });
        Page<JobInstanceRes> jobInstanceResPage = new Page<>();
        jobInstanceResPage.setRecords(jobInstanceResList);
        jobInstanceResPage.setTotal(page.getTotal());
        jobInstanceResPage.setSize(page.getSize());
        jobInstanceResPage.setCurrent(page.getCurrent());
        return Result.success(jobInstanceResPage);
    }

    @PostMapping("/saveOrUpdate")
    public Result<Boolean> saveOrUpdate(@RequestBody JobInstance instance) {
        UserResponse currentUser = getCurrentUser();
        Date now = new Date();
        instance.setUpdateTime(now);
        if (instance.getId() == null) {
            instance.setProjectId(getCurrentProjectId());
            instance.setUpdateBy(currentUser.getUserId());
        }
        boolean bool = jobInstanceService.saveOrUpdate(instance);
        return Result.success(bool);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        Assert.notNull(id, "jobInstance id should not be null");
        UserResponse currentUser = getCurrentUser();
        log.info("user {} delete jobInstance id:{}", currentUser.getUserId(), id);
        boolean bool = jobInstanceService.removeById(id);
        return Result.success(bool);
    }

    @PostMapping("/stop/{id}")
    public Result<Boolean> stop(@PathVariable Long id) {
        UserResponse currentUser = getCurrentUser();
        Assert.notNull(id, "jobInstance id should not be null");
        log.info("userId {} stop jobInstance id:{}", currentUser.getUserId(), id);
        boolean bool = jobInstanceService.cancel(id);
        return Result.success(bool);
    }
}
