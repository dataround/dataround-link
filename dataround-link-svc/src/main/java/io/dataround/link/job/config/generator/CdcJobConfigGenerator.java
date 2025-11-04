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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSONObject;

import io.dataround.link.entity.Connector;
import io.dataround.link.entity.res.JobRes;
import io.dataround.link.entity.res.TableMapping;
import io.dataround.link.utils.BeanConvertor;

/**
 * CDC connector job configuration generator.
 * Handles configuration generation for CDC connectors.
 *
 * @author yuehan124@gmail.com
 * @since 2025-09-07
 */
@Component
public class CdcJobConfigGenerator extends AbstractJobConfigGenerator {

    @Override
    public boolean supports(Connector connector) {
        return connector.getPluginName().endsWith("CDC");
    }

    @Override
    public List<JSONObject> generateSourceConfig(GeneratorContext context) {
        JobRes jobVo = context.getJobVo();
        List<JSONObject> sources = new ArrayList<>();
        List<TableMapping> tableMappings = jobVo.getTableMapping();
        Map<String, String> sourceMap = BeanConvertor.connection2Map(context.getSourceConnection());

        for (TableMapping table : tableMappings) {
            JSONObject source = new JSONObject();
            source.put("plugin_name", context.getSourceConnector().getPluginName());
            // rename url to base-url, user to username
            source.put("base-url", sourceMap.get("url"));
            source.put("username", sourceMap.get("user"));
            source.put("table-names", Collections.singletonList(table.getSourceDbName() + "." + table.getSourceTable()));
            source.put("result_table_name", sourceResultTableName(table.getSourceTable(), jobVo.getId(), context));
            
            // Add connection properties
            for (Map.Entry<String, String> entry : sourceMap.entrySet()) {
                source.put(entry.getKey(), entry.getValue());
            }
            sources.add(source);
        }
        
        return sources;
    }

    @Override
    public List<JSONObject> generateSinkConfig(GeneratorContext context) {
        // CDC connectors typically don't have sink configurations
        return new ArrayList<>();
    }
}
