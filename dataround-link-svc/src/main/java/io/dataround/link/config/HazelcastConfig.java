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

package io.dataround.link.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizePolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Hazelcast configuration for caching uploaded configuration files.
 *
 * @author yuehan124@gmail.com
 * @since 2025-10-31
 */
@Configuration
public class HazelcastConfig {

    /**
     * Create Hazelcast configuration bean.
     *
     * @return Hazelcast Config instance
     */
    @Bean("hazelcastConfigBean")
    public Config hazelcastConfig() {
        Config config = new Config();
        config.setInstanceName("dataround-link-hazelcast-instance");
        
        // Configure map for storing uploaded configuration files
        MapConfig cacheMapConfig = new MapConfig();
        cacheMapConfig.setName("mapCache");
        cacheMapConfig.setInMemoryFormat(InMemoryFormat.BINARY);
        
        EvictionConfig evictionConfig = new EvictionConfig();
        evictionConfig.setEvictionPolicy(EvictionPolicy.LRU);
        evictionConfig.setMaxSizePolicy(MaxSizePolicy.PER_NODE);
        evictionConfig.setSize(1000);
        cacheMapConfig.setEvictionConfig(evictionConfig);
        
        cacheMapConfig.setTimeToLiveSeconds(3600); // 1 hour TTL
        
        config.addMapConfig(cacheMapConfig);
        
        return config;
    }
}