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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.dataround.link.entity.VirtualField;
import io.dataround.link.mapper.VirtualFieldMapper;
import io.dataround.link.service.VirtualFieldService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of the VirtualFieldService interface.
 * Provides concrete implementation for managing virtual field operations and field metadata.
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
@Service
public class VirtualFieldServiceImpl extends ServiceImpl<VirtualFieldMapper, VirtualField> implements VirtualFieldService {

    @Autowired
    private VirtualFieldMapper virtualFieldMapper;

    @Override
    public void removeByTableId(Long tableId) {
        virtualFieldMapper.removeByTableId(tableId);
    }

    @Override
    public Map<Long, List<VirtualField>> listByTableIds(List<Long> tableIds) {
        Map<Long, List<VirtualField>> result = new HashMap<>();
        if (tableIds== null || tableIds.isEmpty()) {
            return result;
        }
        LambdaQueryWrapper<VirtualField> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(VirtualField::getTableId, tableIds);
        List<VirtualField> virtualFields = virtualFieldMapper.selectList(queryWrapper);
        for (VirtualField virtualField : virtualFields) {
            result.computeIfAbsent(virtualField.getTableId(), k -> new ArrayList<>()).add(virtualField);
        }
        return result;
    }
}
