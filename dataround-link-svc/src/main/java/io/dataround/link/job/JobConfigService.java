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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSONObject;

import io.dataround.link.entity.Connector;
import io.dataround.link.entity.Connection;
import io.dataround.link.entity.enums.JobTypeEnum;
import io.dataround.link.entity.res.JobRes;
import io.dataround.link.service.ConnectionService;
import io.dataround.link.service.ConnectorService;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the JobConfigService interface.
 * Provides concrete implementation for managing job configurations, including HOCON generation
 * and job value object conversion for different data source types.
 *
 * @author yuehan124@gmail.com
 * @since 2025-09-07
 */
@Slf4j
@Component
public class JobConfigService {

    @Autowired
    private ConnectorService connectorService;
    @Autowired
    private ConnectionService connectionService;
    @Autowired
    private JobConfigGeneratorFactory configGeneratorFactory;

    public String getJobJson(JobRes jobVo, Long instanceId) {
        JSONObject jsonConfig = new JSONObject();        
        
        // Add env section
        JSONObject env = new JSONObject();
        String jobMode = JobTypeEnum.getByCode(jobVo.getJobType()).getDescription();
        env.put("job.mode", jobMode);
        env.put("job.name", instanceId + "_" + jobVo.getName());
        jsonConfig.put("env", env);

        // Add source section
        Long sourceConnId = jobVo.getSourceConnId();
        Connection sourceConn = connectionService.getById(sourceConnId);
        Connector sourceConnector = connectorService.getConnector(sourceConn.getConnector());

        List<JSONObject> sources = configGeneratorFactory.generateSourceConfig(jobVo, sourceConn, sourceConnector);
        jsonConfig.put("source", sources);

        // Add empty transform section
        jsonConfig.put("transform", new ArrayList<>());

        // Add sink section
        Long targetConnId = jobVo.getTargetConnId();
        Connection targetConn = connectionService.getById(targetConnId);
        Connector targetConnector = connectorService.getConnector(targetConn.getConnector());

        List<JSONObject> sinks = configGeneratorFactory.generateSinkConfig(jobVo, targetConn, targetConnector);
        jsonConfig.put("sink", sinks);

        return jsonConfig.toJSONString();
    }

}
