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

import java.util.List;
import java.util.function.Supplier;

import io.dataround.link.common.connector.Param;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract base class for connector implementations
 * Provides common functionality like property management and classloader switching
 *
 * @author yuehan124@gmail.com
 * @date 2025-06-24
 */
@Slf4j
public abstract class AbstractTableConnector implements TableConnector {
    
    // connector properties
    @Getter
    private Param param;
    private final ThreadLocal<ClassLoader> originalClassLoader = new ThreadLocal<>();

    @Override
    public void initialize(Param param) {
        withCustomClassLoader(() -> {
            try {
                this.param = param;
                doInitialize();
                return null;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public List<String> getDatabases() {
        return withCustomClassLoader(this::doGetDatabases);
    }

    @Override
    public List<String> getTables(String database) {
        return withCustomClassLoader(()->doGetTables(database));
    }

    @Override
    public List<String> getTables(String database, String tableNamePattern) {
        return withCustomClassLoader(()->doGetTables(database, tableNamePattern));
    }

    @Override
    public List<TableField> getTableFields(String database, String table) {
        return withCustomClassLoader(()->doGetTableFields(database, table));
    }

    @Override
    public List<TableField> getTableFields(String database, String table, String columnNamePattern) {
        return withCustomClassLoader(()->doGetTableFields(database, table, columnNamePattern));
    }

    @Override
    public boolean testConnectivity() {
        return withCustomClassLoader(this::doTestConnectivity);
    }

    // Abstract methods that subclasses must implement
    public abstract void doInitialize() throws Exception;

    public abstract List<String> doGetDatabases();

    public abstract List<String> doGetTables(String database);

    public abstract List<String> doGetTables(String database, String tableNamePattern);

    public abstract List<TableField> doGetTableFields(String database, String table);

    public abstract List<TableField> doGetTableFields(String database, String table, String columnNamePattern);

    public abstract boolean doTestConnectivity();

    private <T> T withCustomClassLoader(Supplier<T> action) {
        ClassLoader currentLoader = Thread.currentThread().getContextClassLoader();
        try {
            originalClassLoader.set(currentLoader);
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            return action.get();
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader.get());
            originalClassLoader.remove();
        }
    }

} 