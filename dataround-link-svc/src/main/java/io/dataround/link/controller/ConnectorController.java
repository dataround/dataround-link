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

package io.dataround.link.controller;

import io.dataround.link.common.Result;
import io.dataround.link.common.controller.BaseController;
import io.dataround.link.entity.Connector;
import io.dataround.link.service.ConnectorService;
import io.dataround.link.utils.Constants;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Controller for managing connector information and related operations.
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
@RestController
@RequestMapping("/api/connector")
@Slf4j
public class ConnectorController extends BaseController {

    @Autowired
    private ConnectorService connectorService;

    @GetMapping("/")
    public Result<Map<String, List<String>>> listConnector(Boolean supportSource, Boolean supportSink, Boolean isStream,
            Boolean fileType) {
        LambdaQueryWrapper<Connector> queryWrapper = new LambdaQueryWrapper<>();
        if (supportSource != null) {
            queryWrapper.eq(Connector::getSupportSource, supportSource);
        }
        if (supportSink != null) {
            queryWrapper.eq(Connector::getSupportSink, supportSink);
        }
        if (isStream != null) {
            queryWrapper.eq(Connector::getIsStream, isStream);
        }
        if (fileType != null) {
            if (fileType) {
                queryWrapper.eq(Connector::getType, Constants.CONNECTOR_TYPE_FILE);
            } else {
                queryWrapper.ne(Connector::getType, Constants.CONNECTOR_TYPE_FILE);
            }
        }
        queryWrapper.orderByAsc(Connector::getId);
        List<Connector> connectors = connectorService.list(queryWrapper);
        Map<String, List<String>> map = connectors.stream().collect(
                Collectors.groupingBy(Connector::getType, Collectors.mapping(Connector::getName, Collectors.toList())));
        TreeMap<String, List<String>> treeMap = new TreeMap<>(map);
        // return
        return Result.success(treeMap);
    }
}
