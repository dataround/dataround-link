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

import io.dataround.link.entity.VirtualTable;
import io.dataround.link.entity.req.VirtualTableReq;
import io.dataround.link.entity.res.VirtualTableRes;

/**
 * Service interface for managing virtual tables.
 * Provides methods for CRUD operations on virtual tables including retrieval by ID or connection details.
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
public interface VirtualTableService extends IService<VirtualTable> {

    VirtualTableRes getById(Long id);

    VirtualTable getBy(Long connId, String database, String table);

    boolean saveOrUpdate(VirtualTableReq virtualTable);

    boolean removeById(Long id);

}
