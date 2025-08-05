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
import io.dataround.link.entity.VirtualTable;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper interface for VirtualTable entity.
 * Provides database operations for virtual tables.
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
@Mapper
public interface VirtualTableMapper extends BaseMapper<VirtualTable> {

    @Select("select distinct database from virtual_table where connection_id = #{connectionId}")
    List<String> getDatabaseByConnectionId(@Param("connectionId") Long connectionId);

    @Select("select distinct table_name from virtual_table where connection_id = #{connectionId} and database = #{databaseName}")
    List<String> getTablesByConnectionIdAndDatabase(@Param("connectionId") Long connectionId, @Param("databaseName") String databaseName);

    @Select("select * from virtual_table where connection_id = #{connectionId} and database = #{databaseName} and table_name = #{tableName}")
    VirtualTable getVirtualTable(@Param("connectionId") Long connectionId, @Param("databaseName") String databaseName, @Param("tableName") String tableName);
}
