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

import java.util.HashMap;
import java.util.Map;

import io.dataround.link.entity.Connection;
import io.dataround.link.entity.Connector;
import io.dataround.link.entity.res.JobRes;
import io.dataround.link.utils.BeanConvertor;
import lombok.Getter;

/**
 * Generator context
 * 
 * @author yuehan124@gmail.com
 * @since 2025-10-29
 */
public class GeneratorContext {
    @Getter
    private JobRes jobVo;
    @Getter
    private Connection sourceConnection;
    @Getter
    private Connector sourceConnector;
    @Getter
    private Connection targetConnection;
    @Getter
    private Connector targetConnector;

    private Map<String, String> tableNameMap = new HashMap<>();

    public GeneratorContext(JobRes jobVo, Connection sourceConnection, Connector sourceConnector,
            Connection targetConnection, Connector targetConnector) {
        this.jobVo = jobVo;
        this.sourceConnection = sourceConnection;
        this.sourceConnector = sourceConnector;
        this.targetConnection = targetConnection;
        this.targetConnector = targetConnector;
    }

    public Map<String, String> getSourceConnectionMap() {
        return BeanConvertor.connection2Map(sourceConnection);
    }

    public Map<String, String> getTargetConnectionMap() {
        return BeanConvertor.connection2Map(targetConnection);
    }

    public String sourceResultTableName(String tableName) {
        String result = sourceTableName(tableName);
        return tableNameMap.computeIfAbsent(result, k -> result);
    }

    public String transformResultTableName(String tableName) {
        String result = transformTableName(tableName);
        return tableNameMap.computeIfAbsent(result, k -> result);
    }

    /**
     * Get the sink source table name, If transform table exists, return transform table name
     * @param tableName
     * @return
     */
    public String sinkSourceTableName(String tableName) {
        if (tableNameMap.containsKey(transformTableName(tableName))) {
            return tableNameMap.get(transformTableName(tableName));
        } else {
            return tableNameMap.get(sourceTableName(tableName));
        }
    }

    private String sourceTableName(String tableName) {
        return "TableSource_" + tableName;
    }

    private String transformTableName(String tableName) {
        return "TableTransform_" + tableName;
    }
 }
