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
import io.dataround.link.connector.TableField;
import io.dataround.link.entity.Connection;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service interface for managing database connections.
 * Provides methods for testing connections, retrieving database metadata,
 * and managing connection-related operations.
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
public interface ConnectionService extends IService<Connection> {

    boolean testConnection(Connection connection);

    List<String> getDatabases(Long connectionId);

    List<String> getTableNames(Long connectionId, String databaseName);

    List<String> getTableNames(Long connectionId, String databaseName, String filterName, Integer size);

    List<TableField> getTableFields(Long connectionId, String databaseName, String tableName);

    Map<Long, String> listNameByIds(Set<Long> connectionIds);

    boolean checkProjectUsed(Long projectId);
}
