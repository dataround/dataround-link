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
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import io.dataround.link.entity.Connector;
import io.dataround.link.entity.VirtualField;
import io.dataround.link.entity.VirtualTable;
import io.dataround.link.entity.res.FieldMapping;
import io.dataround.link.entity.res.JobRes;
import io.dataround.link.entity.res.TableMapping;
import io.dataround.link.service.VirtualFieldService;
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
public class KafkaJobConfigGenerator extends AbstractJobConfigGenerator {

    @Autowired
    private VirtualTableService virtualTableService;
    @Autowired
    private VirtualFieldService virtualFieldService;

    private static final String FORMAT_KEY = "format";
    private static final String FORMAT_TEXT = "text";
    private static final String FORMAT_JSON = "json";

    @Override
    public boolean supports(Connector connector) {
        return connector.getPluginName().equalsIgnoreCase("KAFKA");
    }

    @Override
    public List<JSONObject> generateSourceConfig(GeneratorContext context) {
        JobRes jobVo = context.getJobVo();
        List<TableMapping> tableMappings = jobVo.getTableMapping();
        Map<String, String> sourceMap = BeanConvertor.connection2Map(context.getSourceConnection(), context.getSourceConnector());

        List<JSONObject> sources = new ArrayList<>();
        for (TableMapping table : tableMappings) {
            JSONObject source = new JSONObject();
            source.put("plugin_name", "Kafka");
            source.put("result_table_name", sourceResultTableName(table.getSourceTable(), jobVo.getId(), context));
            VirtualTable vtable = virtualTableService.getBy(context.getSourceConnection().getId(), table.getSourceDbName(), table.getSourceTable());
            List<VirtualField> vFields = virtualFieldService.listByTableId(vtable.getId());
            JSONObject tableConfig = JSON.parseObject(vtable.getTableConfig());

            // Add connection properties
            for (Map.Entry<String, String> entry : sourceMap.entrySet()) {
                source.put(entry.getKey(), entry.getValue());
            }
            // Add table config properties
            for (String key : tableConfig.keySet()) {
                source.put(key, tableConfig.get(key));
            }
            // add field properties
            if (FORMAT_TEXT.equals(tableConfig.get(FORMAT_KEY))) {
                int fieldCount = 0;
                JSONObject fields = new JSONObject();
                for (VirtualField field : vFields) {
                    fieldCount = Math.max(fieldCount, Integer.parseInt(field.getPath()));
                }
                // sorted vfields by field path
                List<VirtualField> sortedFields = vFields.stream().sorted((field1, field2) -> {
                    int idx1 = Integer.parseInt(field1.getPath());
                    int idx2 = Integer.parseInt(field2.getPath());
                    return Integer.compare(idx1, idx2);
                }).collect(Collectors.toList());

                int filledIndex = 0;
                for (VirtualField field : sortedFields) {
                    int idx = Integer.parseInt(field.getPath());
                    for (int i = filledIndex; i < idx; i++) {
                        fields.put("col_" + i + "_" + RandomStringUtils.randomAlphanumeric(5), "STRING");
                    }
                    for (FieldMapping fieldMapping : table.getFieldMapping()) {
                        if (fieldMapping.getSourceFieldName().equals(field.getName())) {
                            fields.put(fieldMapping.getSourceFieldName(), fieldMapping.getSourceFieldType());
                            break;
                        }
                    }
                    filledIndex = idx + 1;
                }
                JSONObject schema = new JSONObject();
                schema.put("fields", fields);
                source.put("schema", schema);
            } else if (FORMAT_JSON.equals(tableConfig.get(FORMAT_KEY))) {
                // json source need to convert to text, then use jsonpath transform
                source.put("format", FORMAT_TEXT);
            }
            sources.add(source);
        }

        return sources;
    }

    @Override
    public List<JSONObject> generateTransformConfig(GeneratorContext context) {
        JobRes jobVo = context.getJobVo();
        List<TableMapping> tableMappings = jobVo.getTableMapping();

        List<JSONObject> transforms = new ArrayList<>();
        for (TableMapping table : tableMappings) {
            VirtualTable vtable = virtualTableService.getBy(context.getSourceConnection().getId(), table.getSourceDbName(), table.getSourceTable());
            List<VirtualField> vFields = virtualFieldService.listByTableId(vtable.getId());
            JSONObject tableConfig = JSON.parseObject(vtable.getTableConfig());

            String sourceTableName = sourceResultTableName(table.getSourceTable(), jobVo.getId(), context);
            if (FORMAT_TEXT.equals(tableConfig.get(FORMAT_KEY))) {
                JSONObject transform = new JSONObject();
                transform.put("plugin_name", "sql");
                transform.put("source_table_name", sourceTableName);
                transform.put("result_table_name", transformResultTableName(table.getTargetTable(), jobVo.getId(), context));
                String sql = "SELECT ";
                for (int i = 0; i < table.getFieldMapping().size(); i++) {
                    FieldMapping field = table.getFieldMapping().get(i);
                    sql += field.getSourceFieldName();
                    if (i < table.getFieldMapping().size() - 1) {
                        sql += ", ";
                    }
                }
                sql += " FROM " + sourceTableName;
                transform.put("query", sql);
                transforms.add(transform);
            } else if (FORMAT_JSON.equals(tableConfig.get(FORMAT_KEY))) {
                JSONObject jsonPathTransform = new JSONObject();
                jsonPathTransform.put("plugin_name", "jsonpath");
                jsonPathTransform.put("source_table_name", sourceTableName);
                jsonPathTransform.put("result_table_name", transformResultTableName(table.getTargetTable(), jobVo.getId(), context));
                JSONArray fieldArray = new JSONArray();
                JSONObject fieldMapper = new JSONObject();
                for (FieldMapping fieldMapping : table.getFieldMapping()) {
                    JSONObject field = new JSONObject();
                    // default field name is content
                    field.put("src_field", "content");
                    field.put("dest_field", fieldMapping.getTargetFieldName());
                    field.put("dest_type", fieldMapping.getSourceFieldType());
                    for (VirtualField vField : vFields) {
                        if (vField.getName().equals(fieldMapping.getSourceFieldName())) {
                            field.put("path", vField.getPath());
                            break;
                        }
                    }
                    fieldArray.add(field);
                    fieldMapper.put(fieldMapping.getTargetFieldName(), fieldMapping.getTargetFieldName());
                }
                jsonPathTransform.put("columns", fieldArray);
                // remove original fields: content
                JSONObject fieldMapperTransform = new JSONObject();
                fieldMapperTransform.put("plugin_name", "FieldMapper");
                fieldMapperTransform.put("source_table_name", transformResultTableName(table.getTargetTable(), jobVo.getId(), context));
                fieldMapperTransform.put("result_table_name", transformResultTableName("fieldmapper_" + table.getTargetTable(), jobVo.getId(), context));
                fieldMapperTransform.put("field_mapper", fieldMapper);
                // add to transforms
                transforms.add(jsonPathTransform);
                transforms.add(fieldMapperTransform);
            }
        }
        return transforms;
    }

    @Override
    public List<JSONObject> generateSinkConfig(GeneratorContext context) {
        JobRes jobVo = context.getJobVo();
        List<TableMapping> tableMappings = jobVo.getTableMapping();
        Map<String, String> targetMap = BeanConvertor.connection2Map(context.getTargetConnection(), context.getTargetConnector());

        List<JSONObject> sinks = new ArrayList<>();
        for (TableMapping table : tableMappings) {
            JSONObject sink = new JSONObject();
            sink.put("plugin_name", "Kafka");
            sink.put("source_table_name", prevStepResultTableName(context));
            VirtualTable vtable = virtualTableService.getBy(context.getTargetConnection().getId(), table.getTargetDbName(), table.getTargetTable());
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

}
