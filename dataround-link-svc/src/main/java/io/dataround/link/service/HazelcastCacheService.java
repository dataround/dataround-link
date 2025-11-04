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

package io.dataround.link.service;

/**
 * Service interface for caching data in Hazelcast.
 *
 * @author yuehan124@gmail.com
 * @since 2025-10-31
 */
public interface HazelcastCacheService {

    /**
     * Cache content string with specified key.
     *
     * @param key the cache key
     * @param content the content string to cache
     * @return true if cached successfully, false otherwise
     */
    boolean put(String key, String content);

    /**
     * Get cached content by key.
     *
     * @param key the cache key
     * @return the cached content as string, or null if not found
     */
    String get(String key);

    /**
     * Remove cached content by key.
     *
     * @param key the cache key
     * @return true if removed, false if not found
     */
    boolean remove(String key);
}