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
        vo.setCreateBy(job.getCreateBy());
        vo.setUpdateBy(job.getUpdateBy());
        return vo;
    }
    
    /**
     * Convert a connection to a map.
     * @param connection the connection to convert
     * @return the map
     */
    public static Map<String, String> connection2Map(Connection connection) {
        Map<String, String> map = connection.getConfig();
        if (connection.getHost() != null) {
            map.put("host", connection.getHost());
        }
        if (connection.getPort() != null) {
            map.put("port", connection.getPort().toString());
        }
        if (connection.getUser() != null) {
            map.put("user", connection.getUser());
        }
        if (connection.getPasswd() != null) {
            map.put("password", connection.getPasswd());
        }
        return map;
    }
}
