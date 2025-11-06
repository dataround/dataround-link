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

package io.dataround.link.connector;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.thrift.TException;

import io.dataround.link.common.utils.ConnectorNameConstants;
import io.dataround.link.common.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * Hive connector implementation based on Hive Metastore
 * 
 * @author yuehan124@gmail.com
 * @since 2025-06-09
 */
@Slf4j
public class HiveConnector extends AbstractTableConnector {

    private final String name = ConnectorNameConstants.HIVE;

    // Hive system databases that should be filtered out
    private static final Set<String> SYSTEM_DATABASES = new HashSet<>(Arrays.asList(
            "information_schema",
            "sys"));

    private HiveMetaStoreClient metaStoreClient;
    private HiveConf hiveConf;

    // Configuration file keys
    private static final String KEY_METASTORE_URI = "metastore_uri";
    private static final String KEY_HDFS_SITE_PATH = "hdfs_site_path";
    private static final String KEY_HIVE_SITE_PATH = "hive_site_path";
    private static final String KEY_KERBEROS_PRINCIPAL = "kerberos_principal";
    private static final String KEY_KERBEROS_KEYTAB_PATH = "kerberos_keytab_path";
    private static final String KEY_KERBEROS_KRB5_CONF_PATH = "kerberos_krb5_conf_path";

    @Override
    public String getName() {
        return this.name;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void doInitialize() throws Exception {
        Map<String, String> props = getParam().getConfig();
        String metastoreUri = props.get(KEY_METASTORE_URI);
        if (metastoreUri == null || metastoreUri.isEmpty()) {
            throw new IllegalArgumentException("Metastore URI is required for Hive connector");
        }
        // Create Hive configuration
        hiveConf = new HiveConf();
        hiveConf.setVar(HiveConf.ConfVars.METASTOREURIS, metastoreUri);
        // Handle configuration files
        handleConfigFiles(props);       
        // Set additional properties from config
        props.forEach((key, value) -> {
            // Skip file content keys, they've been handled as files
            if (!key.equals(KEY_METASTORE_URI) &&
                    !key.equals(KEY_HDFS_SITE_PATH) &&
                    !key.equals(KEY_HIVE_SITE_PATH) &&
                    !key.equals(KEY_KERBEROS_PRINCIPAL) &&
                    !key.equals(KEY_KERBEROS_KEYTAB_PATH) &&
                    !key.equals(KEY_KERBEROS_KRB5_CONF_PATH)) {
                hiveConf.setVar(HiveConf.ConfVars.valueOf(key), value);
            }
        });
        // Handle Kerberos authentication if configured
        handleKerberosAuth(props);
        // Create metastore client
        try {
            metaStoreClient = new HiveMetaStoreClient(hiveConf);
            log.info("Successfully connected to Hive Metastore: {}", metastoreUri);
        } catch (MetaException e) {
            log.error("Failed to connect to Hive Metastore: {}", metastoreUri, e);
            throw new RuntimeException("Failed to connect to Hive Metastore", e);
        }
    }

    /**
     * Handle configuration files by writing them to temporary files
     */
    private void handleConfigFiles(Map<String, String> props) throws IOException {
        // Handle hdfs-site.xml
        String hdfsSiteContent = props.get(KEY_HDFS_SITE_PATH);
        if (hdfsSiteContent != null && !hdfsSiteContent.isEmpty()) {
            Path hdfsSitePath = FileUtils.createTempFile("hdfs-site", ".xml", hdfsSiteContent);
            hiveConf.addResource(hdfsSitePath.toString());
            log.info("Added hdfs-site.xml from temporary file: {}", hdfsSitePath);
        }

        // Handle hive-site.xml
        String hiveSiteContent = props.get(KEY_HIVE_SITE_PATH);
        if (hiveSiteContent != null && !hiveSiteContent.isEmpty()) {
            Path hiveSitePath = FileUtils.createTempFile("hive-site", ".xml", hiveSiteContent);
            hiveConf.addResource(hiveSitePath.toString());
            log.info("Added hive-site.xml from temporary file: {}", hiveSitePath);
        }
    }

    /**
     * Handle Kerberos authentication configuration
     */
    private void handleKerberosAuth(Map<String, String> props) {
        String kerberosPrincipal = props.get(KEY_KERBEROS_PRINCIPAL);
        if (kerberosPrincipal != null && !kerberosPrincipal.isEmpty()) {
            hiveConf.set("hive.metastore.sasl.enabled", "true");
            hiveConf.set("hive.security.authorization.enabled", "true");
            hiveConf.set("hive.metastore.kerberos.principal", kerberosPrincipal);

            // Handle keytab file
            String keytabContent = props.get(KEY_KERBEROS_KEYTAB_PATH);
            if (keytabContent != null && !keytabContent.isEmpty()) {
                Path keytabPath = FileUtils.createTempFile("hive-keytab", ".keytab", keytabContent);
                hiveConf.set("hive.metastore.kerberos.keytab.file", keytabPath.toString());
                log.info("Added Kerberos keytab from temporary file: {}", keytabPath);
            }

            // Handle krb5.conf file
            String krb5ConfContent = props.get(KEY_KERBEROS_KRB5_CONF_PATH);
            if (krb5ConfContent != null && !krb5ConfContent.isEmpty()) {
                Path krb5ConfPath = FileUtils.createTempFile("krb5", ".conf", krb5ConfContent);
                System.setProperty("java.security.krb5.conf", krb5ConfPath.toString());
                log.info("Set Kerberos configuration file: {}", krb5ConfPath);
            }
        }
    }

    @Override
    public List<String> doGetDatabases() {
        try {
            List<String> databases = metaStoreClient.getAllDatabases();
            return databases.stream()
                    .filter(db -> !SYSTEM_DATABASES.contains(db.toLowerCase()))
                    .collect(Collectors.toList());
        } catch (TException e) {
            log.error("Failed to get databases from Hive Metastore", e);
            throw new RuntimeException("Failed to get databases", e);
        }
    }

    @Override
    public List<String> doGetTables(String database) {
        try {
            return metaStoreClient.getAllTables(database);
        } catch (TException e) {
            log.error("Failed to get tables from Hive Metastore for database: {}", database, e);
            throw new RuntimeException("Failed to get tables", e);
        }
    }

    @Override
    public List<String> doGetTables(String database, String tableNamePattern) {
        // For simplicity, we'll get all tables and then filter by pattern
        try {
            List<String> tables = metaStoreClient.getAllTables(database);
            if (tableNamePattern != null && !tableNamePattern.isEmpty()) {
                return tables.stream()
                        .filter(table -> table.toLowerCase().contains(tableNamePattern.toLowerCase()))
                        .collect(Collectors.toList());
            }
            return tables;
        } catch (TException e) {
            log.error("Failed to get tables from Hive Metastore for database: {}", database, e);
            throw new RuntimeException("Failed to get tables", e);
        }
    }

    @Override
    public List<TableField> doGetTableFields(String database, String table) {
        try {
            List<FieldSchema> fields = metaStoreClient.getFields(database, table);
            return fields.stream()
                    .map(field -> {
                        TableField tableField = new TableField();
                        tableField.setName(field.getName());
                        tableField.setType(field.getType());
                        tableField.setComment(field.getComment());
                        // In Hive, we can't easily determine nullable and primary key from metastore
                        tableField.setNullable(true);
                        tableField.setPrimaryKey(false);
                        return tableField;
                    })
                    .collect(Collectors.toList());
        } catch (TException e) {
            log.error("Failed to get table fields from Hive Metastore for table: {}.{}", database, table, e);
            throw new RuntimeException("Failed to get table fields", e);
        }
    }

    @Override
    public List<TableField> doGetTableFields(String database, String table, String columnNamePattern) {
        List<TableField> fields = doGetTableFields(database, table);
        if (columnNamePattern != null && !columnNamePattern.isEmpty()) {
            return fields.stream()
                    .filter(field -> field.getName().toLowerCase().contains(columnNamePattern.toLowerCase()))
                    .collect(Collectors.toList());
        }
        return fields;
    }

    @Override
    public boolean doTestConnectivity() {
        try {
            // Try to get all databases as a connectivity test
            metaStoreClient.getAllDatabases();
            return true;
        } catch (TException e) {
            log.error("Failed to test Hive Metastore connectivity", e);
            return false;
        }
    }

    @Override
    public void close() throws Exception {
        if (metaStoreClient != null) {
            metaStoreClient.close();
        }
    }
}