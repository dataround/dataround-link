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

package io.dataround.link.job.config.generator;

import java.util.List;

import com.alibaba.fastjson2.JSONObject;

import io.dataround.link.entity.Connector;
import io.dataround.link.entity.Connection;
import io.dataround.link.entity.res.JobRes;

/**
 * Abstract interface for generating job configurations for different connector types.
 * Each connector type should implement this interface to provide specific configuration generation logic.
 *
 * @author yuehan124@gmail.com
 * @since 2025-09-07
 */
public interface JobConfigGenerator {

    /**
     * Check if this generator supports the given connector
     * @param connector the connector to check
     * @return true if supported, false otherwise
     */
    boolean supports(Connector connector);

    /**
     * Generate source configurations for the given job
     * @param jobVo the job configuration
     * @param connection the source connection
     * @param connector the source connector
     * @return list of source configurations
     */
    List<JSONObject> generateSourceConfig(JobRes jobVo, Connection connection, Connector connector);

    /**
     * Generate sink configurations for the given job
     * @param jobVo the job configuration
     * @param connection the target connection
     * @param connector the target connector
     * @return list of sink configurations
     */
    List<JSONObject> generateSinkConfig(JobRes jobVo, Connection connection, Connector connector);
}
