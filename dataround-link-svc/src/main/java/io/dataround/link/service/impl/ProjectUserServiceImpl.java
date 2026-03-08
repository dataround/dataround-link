/*
 * Copyright (C) 2025 yuehan124@gmail.com
 *
 * This repository is licensed under the Dataround Open Source License
 */

package io.dataround.link.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.dataround.link.entity.ProjectUser;
import io.dataround.link.mapper.ProjectUserMapper;
import io.dataround.link.service.ProjectUserSerivce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Project user service impl
 * 
 * @author yuehan124@gmail.com
 * @since 2025/09/21
 */
@Service
public class ProjectUserServiceImpl extends ServiceImpl<ProjectUserMapper, ProjectUser> implements ProjectUserSerivce {

    @Autowired
    private ProjectUserMapper projectMapper;

    @Override
    public List<ProjectUser> listByProjectIds(List<Long> projectIds) {
        return projectMapper.listByProjectIds(projectIds);
    }
}
