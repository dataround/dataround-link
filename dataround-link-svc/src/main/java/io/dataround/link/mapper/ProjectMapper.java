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

package io.dataround.link.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.dataround.link.entity.Project;
import io.dataround.link.entity.User;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Project mapper
 * 
 * @author yuehan124@gmail.com
 * @since 2025/09/21
 */
public interface ProjectMapper extends BaseMapper<Project> {

    @Select("select a.*, b.is_admin, b.selected from project a, project_user b where a.id=b.project_id and b.user_id = #{userId}")
    List<Project> myProject(Long userId);

    @Update("update project_user set selected = true where project_id = #{projectId} and user_id = #{userId}")
    int updateSelected(Long userId, Long projectId);

    @Update("update project_user set selected = false where user_id = #{userId}")
    int updateUnSelected(Long userId);

    @Select("select u.* from project_user pu, user u where pu.user_id = u.id and pu.project_id = #{projectId}")
    List<User> getProjectUsers(Long projectId);
}