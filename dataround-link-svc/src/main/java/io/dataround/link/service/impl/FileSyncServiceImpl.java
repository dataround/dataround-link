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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${dataround.link.fileSync.minThreadCount:2}")
    private Integer minThreadCount;
    @Value("${dataround.link.fileSync.maxThreadCount:10}")
    private Integer maxThreadCount;
    private ExecutorService executorService;

    @Autowired
    private ConnectionService connectionService;
    @Autowired
    private ConnectorService connectorService;
    @Autowired
    private JobInstanceService jobInstanceService;

    @PostConstruct
    public void init() {
        this.executorService = new ThreadPoolExecutor(minThreadCount, maxThreadCount, 10L,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    }

    @Override
    public boolean executeFileSync(JobRes jobVo, Long instanceId) {
        long startTime = System.currentTimeMillis();
        Long sourceConnId = jobVo.getSourceConnId();
        Long targetConnId = jobVo.getTargetConnId();

        
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
        AtomicLong readCount = new AtomicLong(0);
        AtomicLong writeCount = new AtomicLong(0);
        AtomicLong readBytes = new AtomicLong(0);
        AtomicLong writeBytes = new AtomicLong(0);

        JobInstance jobInstance = jobInstanceService.getById(instanceId);
        // If exception occurs, the upper layer will handle it
        for (String file : files) {
            executorService.submit(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    InputStream inputStream = null;
                    OutputStream outputStream = null;
                    try {
                        String srcFile = sourcePath.endsWith("/") ? sourcePath + file : sourcePath + "/" + file;
                        String targetFile = targetPath.endsWith("/") ? targetPath + file : targetPath + "/" + file;
                        outputStream = targetConnector.writeFile(targetFile);
                        inputStream = sourceConnector.readFile(srcFile);
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        readCount.addAndGet(1);
                        readBytes.addAndGet(file.length());
                        writeCount.addAndGet(1);
                        writeBytes.addAndGet(file.length());
                        long duration = System.currentTimeMillis() - startTime;
                        updateJobInstanceMetrics(jobInstance, readCount, readBytes, writeCount, writeBytes, duration);
                        return null;
                    } finally {
                        IOUtils.closeQuietly(inputStream);
                        IOUtils.closeQuietly(outputStream);
                    }
                }
            });
        }
        // update job instance metrics
        jobInstance.setEndTime(new Date());
        jobInstance.setStatus(JobInstanceStatusEnum.SUCCESS.getCode());
        jobInstanceService.updateById(jobInstance);
        return true;
    }

    private void updateJobInstanceMetrics(JobInstance jobInstance, AtomicLong readCount, AtomicLong readBytes,
            AtomicLong writeCount, AtomicLong writeBytes, long duration) {
        jobInstance.setReadCount(readCount.get());
        jobInstance.setReadBytes(readBytes.get());
        jobInstance.setWriteCount(writeCount.get());
        jobInstance.setWriteBytes(writeBytes.get());
        jobInstance.setReadQps(readBytes.get() / (duration / 1000.0));
        jobInstance.setWriteQps(writeBytes.get() / (duration / 1000.0));
        jobInstanceService.updateById(jobInstance);
    }
}