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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import lombok.extern.slf4j.Slf4j;

/**
 * Kafka connector implementation
 * @author yuehan124@gmail.com
 * @since 2025-06-09
 */
@Slf4j
public class KafkaConnector extends AbstractTableConnector {

    private final String name = "KAFKA";
    private AdminClient adminClient;

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void doInitialize() throws Exception {
        Map<String, String> props = getParam().getConfig();
        // Validate required properties
        if (!props.containsKey("bootstrap.servers")) {
            throw new IllegalArgumentException("Missing required property: bootstrap.servers");
        }
        // Initialize Kafka AdminClient
        Properties kafkaProps = new Properties();
        props.forEach(kafkaProps::setProperty);
        adminClient = AdminClient.create(kafkaProps);
        log.info("Successfully connected to Kafka: {}", props.get("bootstrap.servers"));
    }

    @Override
    public List<String> doGetDatabases() {
        // Kafka doesn't have the concept of databases
        return new ArrayList<>();
    }

    @Override
    public List<String> doGetTables(String database) {
        // Kafka doesn't have the concept of tables
        return new ArrayList<>();
    }

    @Override
    public List<String> doGetTables(String database, String schema) {
        // Kafka doesn't have the concept of tables
        return new ArrayList<>();
    }

    @Override
    public List<TableField> doGetTableFields(String database, String table) {
        // Kafka doesn't have the concept of tables, use virtual table
        return new ArrayList<>();
    }

    @Override
    public List<TableField> doGetTableFields(String database, String table, String schema) {
        // Kafka doesn't have the concept of tables, use virtual table
        return new ArrayList<>();
    }

    @Override
    public boolean doTestConnectivity() {
        try {
            // Try to list topics as a connectivity test
            adminClient.listTopics().listings().get();
            return true;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to test Kafka connectivity", e);
            return false;
        }
    }

    @Override
    public void close() throws Exception {
        if (adminClient != null) {
            adminClient.close();
        }
    }
} 