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

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import io.dataround.link.common.connector.Param;
import lombok.extern.slf4j.Slf4j;

/**
 * ConnectorFactory
 * 
 * @author yuehan124@gmail.com
 * @date 2025-06-09
 */
@Slf4j
public class ConnectorFactory {

    private static final String HOME_DIR_PROPERTY = "dataround.link.homeDir";
    private static final String CONNECTOR_LIB_DIR = "lib/connector";
    // cache ClassLoader, key is connectorPath
    private static final Map<String, URLClassLoader> classLoaderCache = new ConcurrentHashMap<>();

    private static final String CONNECTOR_TYPE_FILE = "File";

    /**
     * Create a connector instance based on the type of the connector
     * 
     * @param param connector parameters
     * @return instance of the specified Connector type
     */
    public static Connector createConnector(Param param) {
        String type = param.getType();
        if (CONNECTOR_TYPE_FILE.equalsIgnoreCase(type)) {
            return createFileConnector(param);
        } else {
            return createTableConnector(param);
        }
    }

    /**
     * Generic method to create an instance of a specified Connector type
     * 
     * @param param          connector parameters
     * @param connectorClass expected Connector type
     * @param <T>            Connector subclass type
     * @return instance of the specified Connector type
     */
    @SuppressWarnings("unchecked")
    public static <T extends Connector> T createConnector(Param param, Class<T> connectorClass) {
        if (FileConnector.class.isAssignableFrom(connectorClass)) {
            return (T) createFileConnector(param);
        } else if (TableConnector.class.isAssignableFrom(connectorClass)) {
            return (T) createTableConnector(param);
        } else {
            throw new IllegalArgumentException("Unsupported connector class: " + connectorClass.getName());
        }
    }

    /**
     * Create a table connector instance
     * 
     * @param param connector properties
     * @return instance of the table connector
     */
    public static TableConnector createTableConnector(Param param) {
        String name = param.getName();
        try {
            // get or create ClassLoader
            URLClassLoader classLoader = classLoaderCache.computeIfAbsent(name, k -> {
                try {
                    String connectorPath = getConnectorPath(name, param.getLibDir());
                    // Create custom classloader for this connector
                    File connectorDir = new File(connectorPath);
                    if (!connectorDir.exists() || !connectorDir.isDirectory()) {
                        throw new IllegalArgumentException(
                                "Connector directory does not exist: " + connectorDir.getAbsolutePath());
                    }
                    URL[] urls = getJarUrls(connectorDir);
                    if (urls.length == 0) {
                        throw new IllegalStateException(
                                "No jar files found in connector directory: " + connectorDir.getAbsolutePath());
                    }
                    log.debug("Creating new ClassLoader for connector: {} with {} jar files", name, urls.length);
                    return new TableConnectorClassLoader(urls, Thread.currentThread().getContextClassLoader());
                } catch (Exception e) {
                    log.error("Failed to create ClassLoader for connector: {}", name, e);
                    throw new RuntimeException("Failed to create ClassLoader for connector: " + name, e);
                }
            });

            Connector connector = getConnector(param, classLoader);
            // Initialize the connector with properties
            TableConnector tableConnector = (TableConnector) connector;
            tableConnector.initialize(param);
            return tableConnector;
        } catch (Exception e) {
            log.error("Failed to create connector: {}", name, e);
            throw new RuntimeException("Failed to create connector: " + name, e);
        }
    }

    /**
     * Create a file connector instance
     * 
     * @param param connector properties
     * @return instance of the file connector
     */
    public static FileConnector createFileConnector(Param param) {
        Connector connector = getConnector(param, null);
        // Initialize the connector with properties
        FileConnector fileConnector = (FileConnector) connector;
        fileConnector.initialize(param);
        return fileConnector;
    }

    private static Connector getConnector(Param param, URLClassLoader classLoader) {
        // Use ServiceLoader to find and instantiate the connector
        ServiceLoader<Connector> serviceLoader = ServiceLoader.load(Connector.class, classLoader);
        // Find the connector with matching name
        for (Connector c : serviceLoader) {
            if (c.getName().equalsIgnoreCase(param.getName())) {
                return c;
            }
        }
        throw new IllegalStateException("No connector found with name: " + param.getName());
    }

    private static String getConnectorPath(String name, String libDir) {
        String homeDir = System.getProperty(HOME_DIR_PROPERTY);
        if (homeDir == null || homeDir.trim().isEmpty()) {
            log.warn("System property '" + HOME_DIR_PROPERTY + "' is not set");
        }
        File file = new File(libDir);
        if (!file.exists()) {
            StringBuilder pathBuilder = new StringBuilder(homeDir);
            pathBuilder.append(File.separator).append(CONNECTOR_LIB_DIR).append(File.separator);
            pathBuilder.append(libDir == null ? name : libDir);
            libDir = pathBuilder.toString();
        }
        return libDir;
    }

    private static URL[] getJarUrls(File directory) {
        File[] jarFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".jar"));
        if (jarFiles == null || jarFiles.length == 0) {
            return new URL[0];
        }

        URL[] urls = new URL[jarFiles.length];
        int validUrlCount = 0;
        for (int i = 0; i < jarFiles.length; i++) {
            try {
                urls[validUrlCount] = jarFiles[i].toURI().toURL();
                log.debug("Added jar file to classpath: {}", jarFiles[i].getName());
                validUrlCount++;
            } catch (Exception e) {
                log.error("Failed to convert jar file to URL: {}", jarFiles[i], e);
            }
        }

        // Return only valid URLs
        if (validUrlCount != urls.length) {
            URL[] validUrls = new URL[validUrlCount];
            System.arraycopy(urls, 0, validUrls, 0, validUrlCount);
            log.warn("Only {} out of {} jar files were successfully loaded", validUrlCount, urls.length);
            return validUrls;
        }
        return urls;
    }

}
