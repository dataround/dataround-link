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
import io.dataround.link.entity.UserRole;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * User-Role mapper
 * 
 * @author yuehan124@gmail.com
 * @since 2026/02/19
 */
public interface UserRoleMapper extends BaseMapper<UserRole> {

    @Select("SELECT role_id FROM user_role WHERE user_id = #{userId}")
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    @Select("SELECT r.* FROM role r INNER JOIN user_role ur ON r.id = ur.role_id WHERE ur.user_id = #{userId}")
    List<io.dataround.link.entity.Role> selectRolesByUserId(@Param("userId") Long userId);
}
