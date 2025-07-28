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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import io.dataround.link.entity.Connector;
import io.dataround.link.entity.Connection;
import io.dataround.link.entity.Job;
import io.dataround.link.entity.VirtualTable;
import io.dataround.link.entity.enums.JobTypeEnum;
import io.dataround.link.entity.res.FieldMapping;
import io.dataround.link.entity.res.JobRes;
import io.dataround.link.entity.res.TableMapping;
import io.dataround.link.service.ConnectionService;
import io.dataround.link.service.ConnectorService;
import io.dataround.link.service.JobConfigService;
import io.dataround.link.service.VirtualTableService;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the JobConfigService interface.
 * Provides concrete implementation for managing job configurations, including HOCON generation
 * and job value object conversion for different data source types.
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
@Slf4j
@Service
public class JobConfigServiceImpl implements JobConfigService {

    @Autowired
    private ConnectorService connectorService;
    @Autowired
    private ConnectionService connectionService;
    @Autowired
    private VirtualTableService virtualTableService;

    @Override
    public String getJobJson(JobRes jobVo) {
        JSONObject jsonConfig = new JSONObject();        
        // Add env section
        JSONObject env = new JSONObject();
        String jobMode = JobTypeEnum.getByCode(jobVo.getJobType()).getDescription();
        env.put("job.mode", jobMode);
        env.put("job.name", jobVo.getId() + "_" + jobVo.getName());
        jsonConfig.put("env", env);

        // Add source section
        List<JSONObject> sources = new ArrayList<>();
        Long sourceConnId = jobVo.getSourceConnId();
        Connection sourceConn = connectionService.getById(sourceConnId);
        Connector sourceConnector = connectorService.getConnector(sourceConn.getConnector());
        Map<String, String> sourceMap = connectionService.connection2Map(sourceConn);
        List<TableMapping> tableMappings = jobVo.getTableMapping();

        if (sourceConnector.getPluginName().startsWith("JDBC")) {
            for (TableMapping table : tableMappings) {
                JSONObject source = new JSONObject();
                source.put("plugin_name", "Jdbc");
                source.put("connection_check_timeout_sec", 30);
                source.put("parallelism", 1);
                source.put("result_table_name", tmpTableName(table.getSourceTable(), jobVo.getId()));
                source.put("query", query(table.getSourceTable(), jobVo));
                source.putAll(sourceMap);
                sources.add(source);
            }
        } else if (sourceConnector.getPluginName().equalsIgnoreCase("KAFKA")) {
            for (TableMapping table : tableMappings) {
                JSONObject source = new JSONObject();
                source.put("plugin_name", "Kafka");
                VirtualTable vtable = virtualTableService.getBy(sourceConnId, table.getSourceDbName(), table.getSourceTable());
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
        } else if (sourceConnector.getPluginName().endsWith("CDC")) {
            for (TableMapping table : tableMappings) {
                JSONObject source = new JSONObject();
                source.put("plugin_name", sourceConnector.getPluginName());
                source.put("base-url", 30);
                source.put("username", 1);
                source.put("table-names", Collections.singletonList(table.getSourceDbName() + "." + table.getSourceTable()));
                source.put("result_table_name", tmpTableName(table.getSourceTable(), jobVo.getId()));
                
                // Add connection properties
                for (Map.Entry<String, String> entry : sourceMap.entrySet()) {
                    source.put(entry.getKey(), entry.getValue());
                }
                sources.add(source);
            }
        }
        jsonConfig.put("source", sources);

        // Add empty transform section
        jsonConfig.put("transform", new ArrayList<>());

        // Add sink section
        List<JSONObject> sinks = new ArrayList<>();
        Long targetConnId = jobVo.getTargetConnId();
        Connection targetConn = connectionService.getById(targetConnId);
        Connector targetConnector = connectorService.getConnector(targetConn.getConnector());
        Map<String, String> targetMap = connectionService.connection2Map(targetConn);

        if (targetConnector.getPluginName().startsWith("JDBC")) {
            for (TableMapping table : tableMappings) {
                JSONObject sink = new JSONObject();
                sink.put("plugin_name", "Jdbc");
                sink.put("connection_check_timeout_sec", 30);
                sink.put("batch_size", 1000);
                sink.put("max_commit_attempts", 3);
                sink.put("max_retries", 1);
                sink.put("support_upsert_by_query_primary_key_exist", false);
                sink.put("source_table_name", tmpTableName(table.getSourceTable(), jobVo.getId()));
                sink.put("database", table.getTargetDbName());
                sink.put("table", table.getTargetTable());
                sink.put("generate_sink_sql", true);
                sink.putAll(targetMap);
                sinks.add(sink);
            }
        } else if (targetConnector.getPluginName().equalsIgnoreCase("KAFKA")) {
            for (TableMapping table : tableMappings) {
                JSONObject sink = new JSONObject();
                sink.put("plugin_name", "Kafka");
                sink.put("source_table_name", tmpTableName(table.getSourceTable(), jobVo.getId()));
                VirtualTable vtable = virtualTableService.getBy(targetConnId, table.getTargetDbName(), table.getTargetTable());
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
        } else if (targetConnector.getPluginName().equalsIgnoreCase("HIVE")) {
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
        }
        jsonConfig.put("sink", sinks);

        return jsonConfig.toJSONString();
    }

    @Override
    public Map<String, String> getProperties(Job job) {
        JobRes vo = getJobVo(job);
        JSONObject jsonObject = JSONObject.from(vo);
        Map<String, String> result = new HashMap<>();
        // conver to map
        for (String key : jsonObject.keySet()) {
            result.put(key, jsonObject.getString(key));
        }
        return result;
    }

    @Override
    public JobRes getJobVo(Job job) {
        // Convert Map config to JobRes object
        String configJson = JSONObject.toJSONString(job.getConfig());
        JobRes vo = JSONObject.parseObject(configJson, JobRes.class);
        
        // Override with actual job entity values
        vo.setId(job.getId());
        vo.setName(job.getName());
        vo.setDescription(job.getDescription());
        vo.setJobType(job.getJobType());
        vo.setScheduleType(job.getScheduleType());
        vo.setCron(job.getCron());
        vo.setStartTime(job.getStartTime());
        vo.setEndTime(job.getEndTime());
        vo.setConfig(job.getConfig());
        return vo;
    }

    public String query(String tableName, JobRes jobVo) {
        String query = StringUtils.EMPTY;
        List<String> fields = fields(tableName, jobVo);
        if (fields.isEmpty()) {
            query = "SELECT * FROM " + tableName;
        } else {
            query = "SELECT " + String.join(",", fields) + " FROM " + tableName;
        }
        String where = whereClause(tableName, jobVo);
        return StringUtils.isEmpty(where) ? query : query + " " + where;
    }

    public List<String> fields(String tableName, JobRes jobVo) {
        List<String> fields = new ArrayList<>();
        for (TableMapping tableMapping : jobVo.getTableMapping()) {
            if (tableMapping.getSourceTable().equals(tableName)) {
                List<FieldMapping> fieldMappings = tableMapping.getFieldData();
                for (FieldMapping fm : fieldMappings) {
                    String fieldName = fm.getSourceFieldName();
                    fields.add(sqlDialect(null, fieldName));
                }
                break;
            }
        }
        return fields;
    }

    public String whereClause(String tableName, JobRes jobVo) {
        String where = StringUtils.EMPTY;
        for (TableMapping tableMapping : jobVo.getTableMapping()) {
            if (tableMapping.getSourceTable().equals(tableName)) {
                where = tableMapping.getWhereClause();
                if (StringUtils.isNotEmpty(where) && !where.toUpperCase().startsWith("WHERE")) {
                    where = "WHERE " + where;
                }
                break;
            }
        }
        return where;
    }

    private String sqlDialect(String dialect, String field) {
        return field;
    }

    private String tmpTableName(String tableName, Long jobId) {
        return "Table_" + tableName + "_" + jobId;
    }

}
