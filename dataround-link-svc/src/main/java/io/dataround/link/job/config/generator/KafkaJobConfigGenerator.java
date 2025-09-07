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

package io.dataround.link.job.config.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import io.dataround.link.entity.Connector;
import io.dataround.link.entity.Connection;
import io.dataround.link.entity.VirtualTable;
import io.dataround.link.entity.res.JobRes;
import io.dataround.link.entity.res.TableMapping;
import io.dataround.link.service.VirtualTableService;
import io.dataround.link.utils.BeanConvertor;

/**
 * Kafka connector job configuration generator.
 * Handles configuration generation for Kafka connectors.
 *
 * @author yuehan124@gmail.com
 * @since 2025-09-07
 */
@Component
public class KafkaJobConfigGenerator implements JobConfigGenerator {

    @Autowired
    private VirtualTableService virtualTableService;

    @Override
    public boolean supports(Connector connector) {
        return connector.getPluginName().equalsIgnoreCase("KAFKA");
    }

    @Override
    public List<JSONObject> generateSourceConfig(JobRes jobVo, Connection connection, Connector connector) {
        List<TableMapping> tableMappings = jobVo.getTableMapping();
        Map<String, String> sourceMap = BeanConvertor.connection2Map(connection, connector);

        List<JSONObject> sources = new ArrayList<>();
        
        for (TableMapping table : tableMappings) {
            JSONObject source = new JSONObject();
            source.put("plugin_name", "Kafka");
            VirtualTable vtable = virtualTableService.getBy(connection.getId(), table.getSourceDbName(), table.getSourceTable());
            JSONObject tableConfig = JSON.parseObject(vtable.getTableConfig());
            
            // Add connection properties
            for (Map.Entry<String, String> entry : sourceMap.entrySet()) {
                source.put(entry.getKey(), entry.getValue());
            }
            // Add table config properties
            for (String key : tableConfig.keySet()) {
                source.put(key, tableConfig.get(key));
            }
            sources.add(source);
        }
        
        return sources;
    }

    @Override
    public List<JSONObject> generateSinkConfig(JobRes jobVo, Connection connection, Connector connector) {
        List<TableMapping> tableMappings = jobVo.getTableMapping();
        Map<String, String> targetMap = BeanConvertor.connection2Map(connection, connector);

        List<JSONObject> sinks = new ArrayList<>();
        
        for (TableMapping table : tableMappings) {
            JSONObject sink = new JSONObject();
            sink.put("plugin_name", "Kafka");
            sink.put("source_table_name", tmpTableName(table.getSourceTable(), jobVo.getId()));
            VirtualTable vtable = virtualTableService.getBy(connection.getId(), table.getTargetDbName(), table.getTargetTable());
            JSONObject tableConfig = JSON.parseObject(vtable.getTableConfig());
            
            // Add connection properties
            for (Map.Entry<String, String> entry : targetMap.entrySet()) {
                sink.put(entry.getKey(), entry.getValue());
            }
            // Add table config properties
            for (String key : tableConfig.keySet()) {
                sink.put(key, tableConfig.get(key));
            }
            sinks.add(sink);
        }
        
        return sinks;
    }

    private String tmpTableName(String tableName, Long jobId) {
        return "Table_" + tableName + "_" + jobId;
    }
}
