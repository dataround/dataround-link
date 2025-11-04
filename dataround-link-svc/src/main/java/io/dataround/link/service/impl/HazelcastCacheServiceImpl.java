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

package io.dataround.link.service.impl;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import io.dataround.link.service.HazelcastCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * Implementation of HazelcastCacheService for caching data in Hazelcast.
 *
 * @author yuehan124@gmail.com
 * @since 2025-10-31
 */
@Slf4j
@Service
public class HazelcastCacheServiceImpl implements HazelcastCacheService {

    private final HazelcastInstance hazelcastInstance;

    @Autowired
    public HazelcastCacheServiceImpl(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    /**
     * Cache content string in Hazelcast with specified key.
     *
     * @param key the cache key
     * @param content the content string to cache
     * @return true if cached successfully, false otherwise
     */
    @Override
    public boolean put(String key, String content) {
        try {
            IMap<String, byte[]> cache = hazelcastInstance.getMap("mapCache");
            cache.put(key, content.getBytes(StandardCharsets.UTF_8));
            log.info("Cached content with key: {} and size: {} bytes", key, content.length());
            return true;
        } catch (Exception e) {
            log.error("Failed to cache content with key: {}", key, e);
            return false;
        }
    }

    /**
     * Get cached content from Hazelcast by key.
     *
     * @param key the cache key
     * @return the cached content as string, or null if not found
     */
    @Override
    public String get(String key) {
        IMap<String, byte[]> cache = hazelcastInstance.getMap("mapCache");
        byte[] content = cache.get(key);
        if (content != null) {
            return new String(content, StandardCharsets.UTF_8);
        }
        return null;
    }

    /**
     * Remove cached content from Hazelcast by key.
     *
     * @param key the cache key
     * @return true if removed, false if not found
     */
    @Override
    public boolean remove(String key) {
        IMap<String, byte[]> cache = hazelcastInstance.getMap("mapCache");
        return cache.remove(key) != null;
    }
}