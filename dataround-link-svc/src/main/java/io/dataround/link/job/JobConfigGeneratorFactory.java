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

package io.dataround.link.job;

import io.dataround.link.job.config.generator.JobConfigGenerator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSONObject;

import io.dataround.link.entity.Connector;
import io.dataround.link.entity.Connection;
import io.dataround.link.entity.res.JobRes;

/**
 * Factory class for managing job configuration generators.
 * Provides a centralized way to get the appropriate configuration generator for a given connector.
 *
 * @author yuehan124@gmail.com
 * @since 2025-09-07
 */
@Component
public class JobConfigGeneratorFactory {

    @Autowired
    private List<JobConfigGenerator> generators;

    /**
     * Get the appropriate configuration generator for the given connector
     * @param connector the connector to get generator for
     * @return the configuration generator, or null if none found
     */
    public JobConfigGenerator getGenerator(Connector connector) {
        for (JobConfigGenerator generator : generators) {
            if (generator.supports(connector)) {
                return generator;
            }
        }
        return null;
    }

    /**
     * Generate source configurations using the appropriate generator
     * @param jobVo the job configuration
     * @param connection the source connection
     * @param connector the source connector
     * @return list of source configurations
     */
    public List<JSONObject> generateSourceConfig(JobRes jobVo, Connection connection, Connector connector) {
        JobConfigGenerator generator = getGenerator(connector);
        if (generator != null) {
            return generator.generateSourceConfig(jobVo, connection, connector);
        }
        return new ArrayList<>();
    }

    /**
     * Generate sink configurations using the appropriate generator
     * @param jobVo the job configuration
     * @param connection the target connection
     * @param connector the target connector
     * @return list of sink configurations
     */
    public List<JSONObject> generateSinkConfig(JobRes jobVo, Connection connection, Connector connector) {
        JobConfigGenerator generator = getGenerator(connector);
        if (generator != null) {
            return generator.generateSinkConfig(jobVo, connection, connector);
        }
        return new ArrayList<>();
    }
}
