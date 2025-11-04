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

    private String metastoreUri;
    private String hdfsSite; 
    private String hiveSite;
    private String kerberosPrincipal;
    private String kerberosKeytab;  
    private String kerberosKrb5Conf;  

    private final String KEY_METASTORE_URI = "metastore_uri";
    private final String KEY_HDFS_SITE_PATH = "hdfs_site_path";
    private final String KEY_HIVE_SITE_PATH = "hive_site_path";
    private final String KEY_KERBEROS_PRINCIPAL = "kerberos_principal";
    private final String KEY_KERBEROS_KEYTAB_PATH = "krb5_path";
    private final String KEY_KERBEROS_KRB5_CONF_PATH = "kerberos_keytab_path";

    @Override
    public void extractProperties(Connection connection) {
        Map<String, String> config = connection.getConfig();
        config.put(KEY_METASTORE_URI, metastoreUri);
        if (hdfsSite != null) {
            config.put(KEY_HDFS_SITE_PATH, getCachedValue(hdfsSite));
        }
        if (hiveSite != null) {
            config.put(KEY_HIVE_SITE_PATH, getCachedValue(hiveSite));
        }
        if (kerberosPrincipal != null) {
            config.put(KEY_KERBEROS_PRINCIPAL, kerberosPrincipal);
        }
        if (kerberosKeytab != null) {
            config.put(KEY_KERBEROS_KEYTAB_PATH, getCachedValue(kerberosKeytab));
        }
        if (kerberosKrb5Conf != null) {
            config.put(KEY_KERBEROS_KRB5_CONF_PATH, getCachedValue(kerberosKrb5Conf));
        }
        connection.setConfig(config);
    }

    @Override
    public void fillProperties(Map<String, String> config) {
        setMetastoreUri(config.get(KEY_METASTORE_URI));
        setHdfsSite(config.get(KEY_HDFS_SITE_PATH));
        setHiveSite(config.get(KEY_HIVE_SITE_PATH));
        setKerberosPrincipal(config.get(KEY_KERBEROS_PRINCIPAL));
        setKerberosKeytab(config.get(KEY_KERBEROS_KEYTAB_PATH));
        setKerberosKrb5Conf(config.get(KEY_KERBEROS_KRB5_CONF_PATH));

        // remove config items, other items was used to show extra param for web page
        config.remove(KEY_METASTORE_URI);
        config.remove(KEY_HDFS_SITE_PATH);
        config.remove(KEY_HIVE_SITE_PATH);
        config.remove(KEY_KERBEROS_PRINCIPAL);
        config.remove(KEY_KERBEROS_KEYTAB_PATH);
        config.remove(KEY_KERBEROS_KRB5_CONF_PATH);
    }

    private String getCachedValue(String key) {
        HazelcastCacheService cacheService = SpringContextUtil.getBean(HazelcastCacheService.class);
        return cacheService.get(key);
    }
}