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

package io.dataround.link.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.dataround.link.entity.VirtualField;
import io.dataround.link.entity.VirtualTable;
import io.dataround.link.entity.req.VirtualTableReq;
import io.dataround.link.entity.res.VirtualTableRes;
import io.dataround.link.mapper.VirtualTableMapper;
import io.dataround.link.service.VirtualFieldService;
import io.dataround.link.service.VirtualTableService;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of the VirtualTableService interface.
 * Provides concrete implementation for managing virtual tables, including CRUD operations
 * and field management for virtual tables.
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
@Service
public class VirtualTableServiceImpl extends ServiceImpl<VirtualTableMapper, VirtualTable> implements VirtualTableService {

    @Autowired
    private VirtualFieldService virtualFieldService;

    @Override
    public VirtualTableRes getById(Long id) {
        VirtualTable vt = super.getById(id);
        VirtualField params = new VirtualField();
        params.setTableId(id);
        List<VirtualField> fieldList = virtualFieldService.list(new QueryWrapper<>(params));
        VirtualTableRes res = new VirtualTableRes();
        BeanUtils.copyProperties(vt, res);
        res.setFields(fieldList);
        return res;
    }

    @Override
    public VirtualTable getBy(Long connId, String database, String table) {
        VirtualTable params = new VirtualTable();
        params.setConnectionId(connId);
        params.setDatabase(database);
        params.setTableName(table);
        return getOne(new QueryWrapper<>(params));
    }

    @Override
    public boolean saveOrUpdate(VirtualTableReq virtualTable) {
        // do save or update
        super.saveOrUpdate(virtualTable);
        Long id = virtualTable.getId();
        virtualTable.getFields().forEach(field -> field.setTableId(id));
        // save or update, mybatis-plus will check id exist in db or not
        return virtualFieldService.saveOrUpdateBatch(virtualTable.getFields());
    }

    @Override
    public boolean removeById(Long id) {
        virtualFieldService.removeByTableId(id);
        return super.removeById(id);
    }
}
