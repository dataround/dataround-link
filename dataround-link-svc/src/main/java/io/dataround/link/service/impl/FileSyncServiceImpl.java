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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.dataround.link.common.connector.Param;
import io.dataround.link.connector.ConnectorFactory;
import io.dataround.link.connector.FileConnector;
import io.dataround.link.entity.Connection;
import io.dataround.link.entity.Connector;
import io.dataround.link.entity.JobInstance;
import io.dataround.link.entity.enums.JobInstanceStatusEnum;
import io.dataround.link.entity.res.JobRes;
import io.dataround.link.service.ConnectionService;
import io.dataround.link.service.ConnectorService;
import io.dataround.link.service.FileSyncService;
import io.dataround.link.service.JobInstanceService;
import io.dataround.link.utils.ParamParser;
import lombok.extern.slf4j.Slf4j;

/**
 * Service implementation for FileSync operations.
 *
 * @author yuehan124@gmail.com
 * @date 2025-07-22
 */
@Slf4j
@Service
public class FileSyncServiceImpl implements FileSyncService {

    @Autowired
    private ConnectionService connectionService;
    @Autowired
    private ConnectorService connectorService;
    @Autowired
    private JobInstanceService jobInstanceService;

    @Override
    public boolean executeFileSync(JobRes jobVo, Long instanceId) {
        Long sourceConnId = jobVo.getSourceConnId();
        Long targetConnId = jobVo.getTargetConnId();
        

        JobInstance jobInstance = jobInstanceService.getById(instanceId);
        String sourcePath = jobVo.getSourcePath();
        String targetPath = jobVo.getTargetPath();
        String filePattern = jobVo.getFilePattern();

        // Initialize the connector with properties
        Connection sourceConn = connectionService.getById(sourceConnId);
        Connection targetConn = connectionService.getById(targetConnId);
        Connector sourceDbConnector = connectorService.getConnector(sourceConn.getConnector());
        Connector targetDbConnector = connectorService.getConnector(targetConn.getConnector());
        // Find the connector with matching name
        Param sourceParam = ParamParser.from(sourceConn, sourceDbConnector);
        Param targetParam = ParamParser.from(targetConn, targetDbConnector);
        FileConnector sourceConnector = ConnectorFactory.createFileConnector(sourceParam);
        FileConnector targetConnector = ConnectorFactory.createFileConnector(targetParam);
        List<String> files = sourceConnector.getFiles(sourcePath, filePattern);
        long readCount = 0;
        long writeCount = 0;
        long readBytes = 0;
        long writeBytes = 0;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        long startTime = System.currentTimeMillis();
        try {
            for (String file : files) {
                String srcFile = sourcePath.endsWith("/") ? sourcePath + file : sourcePath + "/" + file;
                String targetFile = targetPath.endsWith("/") ? targetPath + file : targetPath + "/" + file;
                outputStream = targetConnector.writeFile(targetFile);
                inputStream = sourceConnector.readFile(srcFile);
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                readCount++;
                readBytes += file.length();
                writeCount++;
                writeBytes += file.length();
            }
        } catch (IOException e) {
            log.error("Error reading file: {}", e);
            return false;
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        double readQps = readBytes / (duration / 1000.0);
        double writeQps = writeBytes / (duration / 1000.0);
        // update job instance metrics
        jobInstance.setReadCount(readCount);
        jobInstance.setReadBytes(readBytes);
        jobInstance.setWriteCount(writeCount);
        jobInstance.setWriteBytes(writeBytes);
        jobInstance.setReadQps(readQps);
        jobInstance.setWriteQps(writeQps);
        jobInstance.setStatus(JobInstanceStatusEnum.SUCCESS.getCode());
        jobInstance.setEndTime(new Date());
        jobInstanceService.updateById(jobInstance);
        return true;
    }
}