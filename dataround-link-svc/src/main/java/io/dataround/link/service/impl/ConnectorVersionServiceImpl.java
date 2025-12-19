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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.dataround.link.entity.ConnectorVersion;
import io.dataround.link.mapper.ConnectorVersionMapper;
import io.dataround.link.service.ConnectorVersionService;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the ConnectorVersionService interface.
 *
 * @author yuehan124@gmail.com
 * @date 2025-12-17
 */
@Slf4j
@Service
public class ConnectorVersionServiceImpl extends ServiceImpl<ConnectorVersionMapper, ConnectorVersion> implements ConnectorVersionService {
 
    @Autowired
    private ConnectorVersionMapper connectorVersionMapper;


    @Override
    public ConnectorVersion getByIdOrDefault(Long id, String connector) {
        if (id != null) { 
            return this.getById(id);
        }
        LambdaQueryWrapper<ConnectorVersion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ConnectorVersion::getConnector, connector);        
        queryWrapper.eq(ConnectorVersion::getIsDefault, true);
        return this.getOne(queryWrapper);
    }

    @Override
    public List<ConnectorVersion> getByConnector(String connector) {
        return connectorVersionMapper.getByConnector(connector);
    }
}
