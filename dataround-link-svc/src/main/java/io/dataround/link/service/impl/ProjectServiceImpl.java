/*
 * Copyright (C) 2025 yuehan124@gmail.com
 *
 * This repository is licensed under the Dataround Open Source License
 */

package io.dataround.link.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.dataround.link.entity.Project;
import io.dataround.link.entity.ProjectUser;
import io.dataround.link.mapper.ProjectMapper;
import io.dataround.link.service.ProjectService;
import io.dataround.link.service.ProjectUserSerivce;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Project service impl
 * 
 * @author yuehan124@gmail.com
 * @since 2025/09/21
 */
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {

    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private ProjectUserSerivce projectUserSerivce;

    @Transactional
    @Override
    public boolean saveOrUpdate(Project project, List<ProjectUser> members) {
        boolean result = super.saveOrUpdate(project);
        // Set project ID for each member
        for (ProjectUser member : members) {
            member.setProjectId(project.getId());
        }
        result = result && projectUserSerivce.saveOrUpdateBatch(members);
        return result;
    }

    @Override
    public List<Project> myProject(Long userId) {
        return projectMapper.myProject(userId);
    }

    @Override
    public boolean updateSelected(Long userId, Long projectId) {
        projectMapper.updateUnSelected(userId);
        int updated = projectMapper.updateSelected(userId, projectId);
        return updated > 0;
    }
}
