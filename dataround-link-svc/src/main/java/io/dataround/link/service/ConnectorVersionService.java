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

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import io.dataround.link.entity.ConnectorVersion;

/**
 * Service interface for ConnectorVersion.
 *
 * @author yuehan124@gmail.com
 * @date 2025-12-17
 */
public interface ConnectorVersionService extends IService<ConnectorVersion> {

    /**
     * Get connector version by id, if not exists return default connector version.
     *
     * @param id the connector version id
     * @param connector the connector name
     * @return the connector version
     */
    ConnectorVersion getByIdOrDefault(Long id, String connector);

    /**
     * Get connector versions by connector name.
     *
     * @param connector the connector name
     * @return the list of connector versions
     */
    List<ConnectorVersion> getByConnector(String connector);
}
