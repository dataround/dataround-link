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

import io.dataround.link.entity.Connection;
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

    @Override
    public void extractProperties(Connection connection) {
        Map<String, String> config = connection.getConfig();
        config.put("metastore.uri", metastoreUri);
        config.put("hdfs.site", hdfsSite);
        config.put("hive.site", hiveSite);
        config.put("kerberos.principal", kerberosPrincipal);
        config.put("kerberos.keytab", kerberosKeytab);
        config.put("kerberos.krb5.conf", kerberosKrb5Conf);
        connection.setConfig(config);
    }

    @Override
    public void fillProperties(Map<String, String> config) {
        setMetastoreUri(config.get("metastore.uri"));
        setHdfsSite(config.get("hdfs.site"));
        setHiveSite(config.get("hive.site"));
        setKerberosPrincipal(config.get("kerberos.principal"));
        setKerberosKeytab(config.get("kerberos.keytab"));
        setKerberosKrb5Conf(config.get("kerberos.krb5.conf"));

        // remove config items, other items was used to show extra param for web page
        config.remove("metastore.uri");
        config.remove("hdfs.site");
        config.remove("hive.site");
        config.remove("kerberos.principal");
        config.remove("kerberos.keytab");
        config.remove("kerberos.krb5.conf");
    }
}
