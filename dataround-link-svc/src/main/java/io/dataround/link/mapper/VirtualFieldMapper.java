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
import io.dataround.link.entity.VirtualField;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Mapper interface for VirtualField entity.
 * Provides database operations for virtual fields.
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
@Mapper
public interface VirtualFieldMapper extends BaseMapper<VirtualField> {

    @Delete("delete from virtual_field where table_id = #{tableId}")
    void removeByTableId(@Param("tableId") Long tableId);
}
