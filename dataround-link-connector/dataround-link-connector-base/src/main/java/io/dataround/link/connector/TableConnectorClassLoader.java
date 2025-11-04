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
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * TableConnectorClassLoader
 *
 * @author yuehan124@gmail.com
 * @date 2025-06-09
 */
@Slf4j
public class TableConnectorClassLoader extends URLClassLoader {

    private final ClassLoader parentClassLoader;

    public static final String[] DEFAULT_PARENT_FIRST_PATTERNS =
            new String[] {
                "java.",
                "javax.xml",
                "org.xml",
                "org.w3c",
                "scala.",
                "javax.annotation.",
                "org.slf4j",
                "org.apache.logging",
                "com.fasterxml.jackson"
            };

    public TableConnectorClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
        this.parentClassLoader = parent;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        try {
            for (String alwaysParentFirstPattern : DEFAULT_PARENT_FIRST_PATTERNS) {
                if (name.startsWith(alwaysParentFirstPattern)) {
                    return parentClassLoader.loadClass(name);
                }
            }
            Class<?> loadedClass = findLoadedClass(name);
            if (loadedClass != null) {
                return loadedClass;
            }
            log.info("name:{} currentClassLoader:{}", name, Thread.currentThread().getContextClassLoader().toString());
            return findClass(name);
        } catch (ClassNotFoundException e1) {
            try {
                // Check if the class is already loaded by the current class loader or its parent
                Class<?> loadedClass = findLoadedClass(name);
                if (loadedClass == null) {
                    loadedClass = parentClassLoader.loadClass(name);
                }
                return loadedClass;
            } catch (ClassNotFoundException e2) {
                log.error("parentClassLoader load class is error: {}", e2.getMessage());
                throw new ClassNotFoundException("parentClassLoader Failed to load class: " + name, e2);
            }
        }
    }

    @Override
    public URL getResource(String name) {
        // first, try and find it via the URLClassloader
        URL urlClassLoaderResource = findResource(name);
        if (urlClassLoaderResource != null) {
            return urlClassLoaderResource;
        }
        // delegate to super
        return parentClassLoader.getResource(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        // first get resources from URLClassloader
        Enumeration<URL> urlClassLoaderResources = findResources(name);
        final List<URL> result = new ArrayList<>();
        while (urlClassLoaderResources.hasMoreElements()) {
            result.add(urlClassLoaderResources.nextElement());
        }
        // get parent urls
        if (parentClassLoader != null) {
            Enumeration<URL> parentResources = parentClassLoader.getResources(name);
            while (parentResources.hasMoreElements()) {
                result.add(parentResources.nextElement());
            }
        }
        // return merged urls
        return new Enumeration<URL>() {
            final Iterator<URL> iter = result.iterator();

            public boolean hasMoreElements() {
                return iter.hasNext();
            }
            public URL nextElement() {
                return iter.next();
            }
        };
    }
}
