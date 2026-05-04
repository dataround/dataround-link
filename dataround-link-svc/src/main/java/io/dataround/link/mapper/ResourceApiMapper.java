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
import io.dataround.link.entity.ResourceApi;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Resource API mapper
 * 
 * @author yuehan124@gmail.com
 * @since 2026/05/03
 */
public interface ResourceApiMapper extends BaseMapper<ResourceApi> {

    /**
     * Get API paths by resource IDs
     */
    @Select("<script>SELECT * FROM resource_api WHERE resource_id IN " +
            "<foreach item='id' collection='resourceIds' open='(' separator=',' close=')'>#{id}</foreach></script>")
    List<ResourceApi> listByResourceIds(@Param("resourceIds") List<Long> resourceIds);

    /**
     * Delete by resource ID
     */
    @Select("DELETE FROM resource_api WHERE resource_id = #{resourceId}")
    int deleteByResourceId(Long resourceId);
}
