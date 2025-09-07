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

package io.dataround.link.utils;

import java.util.Map;

import com.alibaba.fastjson2.JSONObject;

import io.dataround.link.entity.Connection;
import io.dataround.link.entity.Connector;
import io.dataround.link.entity.Job;
import io.dataround.link.entity.res.JobRes;

/**
 * Utility class for converting between different bean types.
 * Provides methods for converting between different bean types.
 *
 * @author yuehan124@gmail.com
 * @since 2025-09-07
 */
public class BeanConvertor {

    /**
     * Convert a job to a JobRes object.
     * @param job the job to convert
     * @return the JobRes object
     */
    public static JobRes job2JobRes(Job job) {
        // Convert Map config to JobRes object
        String configJson = JSONObject.toJSONString(job.getConfig());
        JobRes vo = JSONObject.parseObject(configJson, JobRes.class);
        
        // Override with actual job entity values
        vo.setId(job.getId());
        vo.setName(job.getName());
        vo.setDescription(job.getDescription());
        vo.setJobType(job.getJobType());
        vo.setScheduleType(job.getScheduleType());
        vo.setCron(job.getCron());
        vo.setStartTime(job.getStartTime());
        vo.setEndTime(job.getEndTime());
        vo.setConfig(job.getConfig());
        return vo;
    }
    
    /**
     * Convert a connection to a map.
     * @param connection the connection to convert
     * @param connector the connector to use
     * @return the map
     */
    public static Map<String, String> connection2Map(Connection connection, Connector connector) {
        Map<String, String> map = connection.getConfig();
        Map<String, String> properties = connector.getProperties();
        if (properties != null) {
            map.put("driver", properties.get("driver"));
        }
        map.put("type", connector.getType());
        map.put("host", connection.getHost());
        if (connection.getPort() != null) {
            map.put("port", connection.getPort().toString());
        }
        map.put("user", connection.getUser());
        map.put("password", connection.getPasswd());
        // rename broker to bootstrap.servers
        if (map.containsKey("broker")) {
            map.put("\"bootstrap.servers\"", map.get("broker"));
            map.remove("broker");
        }
        // MYSQL-CDC use base-url and username, different property key
        String pluginName = connector.getPluginName();
        if (Constants.PlUGIN_NAME_MYSQL_CDC.equals(pluginName)) {
            map.put("base-url", map.get("url"));
            map.put("username", map.get("user"));
        }
        return map;
    }
}
