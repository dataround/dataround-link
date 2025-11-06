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

package io.dataround.link.entity.vo;

import java.util.Map;

import io.dataround.link.SpringContextUtil;
import io.dataround.link.entity.Connection;
import io.dataround.link.service.HazelcastCacheService;
import io.dataround.link.utils.JobConfigParamConstants;
import lombok.Getter;
import lombok.Setter;

/**
 * Hive connection value object
 * 
 * @author yuehan124@gmail.com
 * @date 2025-09-26
 */
@Getter
@Setter
public class HiveConnectionVo extends ConnectionVo {

    // form name 
    private String metastoreUri;
    private String hdfsSite; 
    private String hiveSite;
    private String kerberosPrincipal;
    private String kerberosKeytab;  
    private String kerberosKrb5Conf;  

    @Override
    public void extractProperties(Connection connection) {
        Map<String, String> config = connection.getConfig();
        config.put(JobConfigParamConstants.HIVE_JOB_METASTORE_URI, metastoreUri);
        if (hdfsSite != null) {
            config.put(JobConfigParamConstants.HIVE_JOB_HDFS_SITE_PATH, getCachedValue(hdfsSite));
        }
        if (hiveSite != null) {
            config.put(JobConfigParamConstants.HIVE_JOB_HIVE_SITE_PATH, getCachedValue(hiveSite));
        }
        if (kerberosPrincipal != null) {
            config.put(JobConfigParamConstants.HIVE_JOB_KERBEROS_PRINCIPAL, kerberosPrincipal);
        }
        if (kerberosKeytab != null) {
            config.put(JobConfigParamConstants.HIVE_JOB_KERBEROS_KEYTAB_PATH, getCachedValue(kerberosKeytab));
        }
        if (kerberosKrb5Conf != null) {
            config.put(JobConfigParamConstants.HIVE_JOB_KERBEROS_KRB5_PATH, getCachedValue(kerberosKrb5Conf));
        }
        connection.setConfig(config);
    }

    @Override
    public void fillProperties(Map<String, String> config) {
        setMetastoreUri(config.get(JobConfigParamConstants.HIVE_JOB_METASTORE_URI));
        setHdfsSite(config.get(JobConfigParamConstants.HIVE_JOB_HDFS_SITE_PATH));
        setHiveSite(config.get(JobConfigParamConstants.HIVE_JOB_HIVE_SITE_PATH));
        setKerberosPrincipal(config.get(JobConfigParamConstants.HIVE_JOB_KERBEROS_PRINCIPAL));
        setKerberosKeytab(config.get(JobConfigParamConstants.HIVE_JOB_KERBEROS_KEYTAB_PATH));
        setKerberosKrb5Conf(config.get(JobConfigParamConstants.HIVE_JOB_KERBEROS_KRB5_PATH));

        // remove config items, other items was used to show extra param for web page
        config.remove(JobConfigParamConstants.HIVE_JOB_METASTORE_URI);
        config.remove(JobConfigParamConstants.HIVE_JOB_HDFS_SITE_PATH);
        config.remove(JobConfigParamConstants.HIVE_JOB_HIVE_SITE_PATH);
        config.remove(JobConfigParamConstants.HIVE_JOB_KERBEROS_PRINCIPAL);
        config.remove(JobConfigParamConstants.HIVE_JOB_KERBEROS_KEYTAB_PATH);
        config.remove(JobConfigParamConstants.HIVE_JOB_KERBEROS_KRB5_PATH);
    }

    private String getCachedValue(String key) {
        HazelcastCacheService cacheService = SpringContextUtil.getBean(HazelcastCacheService.class);
        return cacheService.get(key);
    }
}