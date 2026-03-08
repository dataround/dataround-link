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
import io.dataround.link.entity.Resource;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Resource mapper
 * 
 * @author yuehan124@gmail.com
 * @since 2025/02/19
 */
public interface ResourceMapper extends BaseMapper<Resource> {

    @Select("""
        SELECT DISTINCT r.* FROM resource r
        INNER JOIN role_resource rr ON r.id = rr.resource_id
        INNER JOIN user_role ur ON rr.role_id = ur.role_id
        WHERE ur.user_id = #{userId}
        ORDER BY r.id
        """)
    List<Resource> selectResourcesByUserId(@Param("userId") Long userId);

    @Select("""
        SELECT DISTINCT r.* FROM resource r
        INNER JOIN role_resource rr ON r.id = rr.resource_id
        WHERE rr.role_id = #{roleId}
        ORDER BY r.id
        """)
    List<Resource> selectResourcesByRoleId(@Param("roleId") Long roleId);
}
