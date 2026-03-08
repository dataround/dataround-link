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

package io.dataround.link.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.dataround.link.entity.Project;
import io.dataround.link.entity.ProjectUser;

import java.util.List;

/**
 * Project service
 * 
 * @author yuehan124@gmail.com
 * @since 2025/09/21
 */
public interface ProjectService extends IService<Project> {

    boolean saveOrUpdate(Project project, List<ProjectUser> members);

    List<Project> myProject(Long userId);

    boolean updateSelected(Long userId, Long projectId);
}
