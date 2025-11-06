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
import io.dataround.link.common.utils.FileUtils;
import io.dataround.link.entity.res.JobRes;
import io.dataround.link.entity.res.TableMapping;
import io.dataround.link.utils.JobConfigParamConstants;

/**
 * Hive connector job configuration generator.
 *
 * @author yuehan124@gmail.com
 * @since 2025-09-07
 */
@Component
public class HiveJobConfigGenerator extends AbstractJobConfigGenerator {

    @Override
    public boolean supports(Connector connector) {
        return connector.getPluginName().equalsIgnoreCase(ConnectorNameConstants.HIVE);
    }

    @Override
    public List<JSONObject> generateSourceConfig(GeneratorContext context) {
        // Hive connectors typically don't have source configurations
        return new ArrayList<>();
    }

    @Override
    public List<JSONObject> generateSinkConfig(GeneratorContext context) {
        JobRes jobVo = context.getJobVo();
        List<TableMapping> tableMappings = jobVo.getTableMapping();
        Map<String, String> targetMap = context.getTargetConnectionMap();

        List<JSONObject> sinks = new ArrayList<>();
        String hdfsSiteKey = JobConfigParamConstants.HIVE_JOB_HDFS_SITE_PATH;
        String hiveSiteKey = JobConfigParamConstants.HIVE_JOB_HIVE_SITE_PATH;
        String kerberosKeytabKey = JobConfigParamConstants.HIVE_JOB_KERBEROS_KEYTAB_PATH;
        String kerberosKrb5Key = JobConfigParamConstants.HIVE_JOB_KERBEROS_KRB5_PATH;
        for (TableMapping table : tableMappings) {
            JSONObject sink = new JSONObject();
            sink.put("plugin_name", "Hive");
            sink.put("source_table_name", context.sinkSourceTableName(table.getSourceTable()));
            sink.put("table_name", table.getTargetDbName() + "." + table.getTargetTable());
            sink.put(JobConfigParamConstants.HIVE_JOB_METASTORE_URI, targetMap.get(JobConfigParamConstants.HIVE_JOB_METASTORE_URI));
            if (targetMap.get(hdfsSiteKey) != null) {
                sink.put(hdfsSiteKey, FileUtils.createTempFile("hdfs-site", ".xml", targetMap.get(hdfsSiteKey)));
            }
            if (targetMap.get(hiveSiteKey) != null) {
                sink.put(hiveSiteKey, FileUtils.createTempFile("hive-site", ".xml", targetMap.get(hiveSiteKey)));
            }
            if (targetMap.get(kerberosKeytabKey) != null) {
                sink.put(kerberosKeytabKey, FileUtils.createTempFile("hive-keytab", ".keytab", targetMap.get(kerberosKeytabKey)));
            }
            if (targetMap.get(kerberosKrb5Key) != null) {
                sink.put(kerberosKrb5Key, FileUtils.createTempFile("krb5", ".conf", targetMap.get(kerberosKrb5Key)));
            }
            // Add connection properties
            Map<String, String> hadoopConfMap = new HashMap<>();
            for (Map.Entry<String, String> entry : targetMap.entrySet()) {
                if (entry.getKey().equals(hdfsSiteKey) || entry.getKey().equals(hiveSiteKey) || entry.getKey().equals(kerberosKeytabKey) 
                    || entry.getKey().equals(kerberosKrb5Key) || entry.getKey().equals(JobConfigParamConstants.HIVE_JOB_METASTORE_URI)
                    || entry.getKey().equals("host") || entry.getKey().equals("port")) {
                    continue;
                }
                hadoopConfMap.put(entry.getKey(), entry.getValue());                
            }
            if (!hadoopConfMap.isEmpty()) {
                sink.put("hive.hadoop.conf", hadoopConfMap);
            }
            sinks.add(sink);
        }

        return sinks;
    }
}
