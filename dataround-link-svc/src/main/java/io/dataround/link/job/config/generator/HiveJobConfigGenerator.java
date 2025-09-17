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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSONObject;

import io.dataround.link.entity.Connector;
import io.dataround.link.common.utils.ConnectorNameConstants;
import io.dataround.link.entity.Connection;
import io.dataround.link.entity.res.JobRes;
import io.dataround.link.entity.res.TableMapping;
import io.dataround.link.utils.BeanConvertor;

/**
 * Hive connector job configuration generator.
 * Handles configuration generation for Hive connectors.
 *
 * @author yuehan124@gmail.com
 * @since 2025-09-07
 */
@Component
public class HiveJobConfigGenerator implements JobConfigGenerator {

    @Override
    public boolean supports(Connector connector) {
        return connector.getPluginName().equalsIgnoreCase(ConnectorNameConstants.HIVE);
    }

    @Override
    public List<JSONObject> generateSourceConfig(JobRes jobVo, Connection connection, Connector connector) {
        // Hive connectors typically don't have source configurations
        return new ArrayList<>();
    }

    @Override
    public List<JSONObject> generateSinkConfig(JobRes jobVo, Connection connection, Connector connector) {
        List<TableMapping> tableMappings = jobVo.getTableMapping();
        Map<String, String> targetMap = BeanConvertor.connection2Map(connection, connector);

        List<JSONObject> sinks = new ArrayList<>();
        
        for (TableMapping table : tableMappings) {
            JSONObject sink = new JSONObject();
            sink.put("plugin_name", "Hive");
            sink.put("source_table_name", tmpTableName(table.getSourceTable(), jobVo.getId()));
            sink.put("table_name", table.getTargetDbName() + "." + table.getTargetTable());
            
            Map<String, String> hadoopConfig = new HashMap<>();
            hadoopConfig.put("fs.defaultFS", "hdfs://vm2.test.com:30802");
            sink.put("hive.hadoop.conf", hadoopConfig);
            
            // Add connection properties
            for (Map.Entry<String, String> entry : targetMap.entrySet()) {
                sink.put(entry.getKey(), entry.getValue());
            }
            sinks.add(sink);
        }
        
        return sinks;
    }

    private String tmpTableName(String tableName, Long jobId) {
        return "Table_" + tableName + "_" + jobId;
    }
}
