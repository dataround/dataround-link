/*
 * Copyright (C) 2025 yuehan124@gmail.com
 *
 * This repository is licensed under the Dataround Open Source License
 */

package io.dataround.link.entity.vo;

import io.dataround.link.entity.Project;
import io.dataround.link.entity.ProjectUser;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Project vo
 * 
 * @author yuehan124@gmail.com
 * @since 2025/09/21
 */
@Data
public class ProjectVO extends Project {
    // admin members
    private List<ProjectUser> admins = new ArrayList<>(2);
    // normal members
    private List<ProjectUser> members = new ArrayList<>(8);

    public ProjectVO() {
    }

    public ProjectVO(Project project) {
        setId(project.getId());
        setName(project.getName());
        setDescription(project.getDescription());
        setCreatorId(project.getCreatorId());
        setCreateTime(project.getCreateTime());
        setIsAdmin(project.getIsAdmin());
        setSelected(project.getSelected());
        setCreateUser(project.getCreateUser());
    }
}
