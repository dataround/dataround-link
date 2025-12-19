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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
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
    // track the cancel flag for each file sync task
    private final Map<Long, AtomicBoolean> cancelFlags = new ConcurrentHashMap<>();
    // track the running file sync tasks
    private final Map<Long, List<Future<Void>>> runningTasks = new ConcurrentHashMap<>();

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
        boolean includeSubdirectories = jobVo.getIncludeSubdirectories();

        // Initialize the connector with properties
        Connection sourceConn = connectionService.getById(sourceConnId);
        Connection targetConn = connectionService.getById(targetConnId);
        Connector sourceDbConnector = connectorService.getConnector(sourceConn.getConnector());
        Connector targetDbConnector = connectorService.getConnector(targetConn.getConnector());
        // Find the connector with matching name
        Param sourceParam = ParamParser.from(sourceConn, sourceDbConnector, null);
        Param targetParam = ParamParser.from(targetConn, targetDbConnector, null);
        List<String> sourceFiles = getSourceFiles(sourceParam, sourcePath, filePattern, includeSubdirectories);
        AtomicLong readCount = new AtomicLong(0);
        AtomicLong writeCount = new AtomicLong(0);
        AtomicLong readBytes = new AtomicLong(0);
        AtomicLong writeBytes = new AtomicLong(0);

        JobInstance jobInstance = jobInstanceService.getById(instanceId);
        List<Future<Void>> futures = new ArrayList<>();

        // track the running file sync tasks, and initialize the cancel flag
        runningTasks.put(instanceId, futures);
        AtomicBoolean cancelFlag = new AtomicBoolean(false);
        cancelFlags.put(instanceId, cancelFlag);
        for (String file : sourceFiles) {
            Future<Void> future = executorService.submit(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    InputStream inputStream = null;
                    OutputStream outputStream = null;
                    String srcFile = sourcePath.endsWith("/") ? sourcePath + file : sourcePath + "/" + file;
                    String targetFile = targetPath.endsWith("/") ? targetPath + file : targetPath + "/" + file;
                    try (FileConnector srcConnector = ConnectorFactory.createFileConnector(sourceParam);
                        FileConnector tgtConnector = ConnectorFactory.createFileConnector(targetParam)) {
                        log.debug("start to sync file: {} -> {}", srcFile, targetFile);
                        outputStream = tgtConnector.writeFile(targetFile);
                        inputStream = srcConnector.readFile(srcFile);
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            // check the cancel flag and the thread interrupt status
                            if (cancelFlag.get() || Thread.currentThread().isInterrupted()) {
                                log.info("File sync cancelled for file: {} -> {}", srcFile, targetFile);
                                throw new InterruptedException("File sync cancelled");
                            }
                            readBytes.addAndGet(bytesRead);
                            outputStream.write(buffer, 0, bytesRead);
                            writeBytes.addAndGet(bytesRead);
                        }
                        readCount.addAndGet(1);
                        writeCount.addAndGet(1);
                        long duration = System.currentTimeMillis() - startTime;
                        updateJobInstanceMetrics(jobInstance, readCount, readBytes, writeCount, writeBytes, duration);
                        return null;
                    } catch (InterruptedException e) {
                        // restore the interrupted status
                        Thread.currentThread().interrupt();
                        throw e;
                    } finally {
                        IOUtils.closeQuietly(inputStream);
                        IOUtils.closeQuietly(outputStream);
                    }
                }
            });
            futures.add(future);
        }
        // If exception occurs, the upper layer will handle it
        try {
            for (Future<Void> future : futures) {
                try {
                    future.get();
                } catch (Exception e) {
                    // check if this is a cancellation
                    if (cancelFlag.get() || e.getCause() instanceof InterruptedException) {
                        // update the job instance status to cancelled and return
                        jobInstance.setEndTime(new Date());
                        jobInstance.setStatus(JobInstanceStatusEnum.CANCELLED.getCode());
                        jobInstanceService.updateById(jobInstance);
                        // Return false to indicate cancellation
                        return false;
                    }
                    throw new RuntimeException(e);
                }
            }
            // update job instance metrics
            jobInstance.setEndTime(new Date());
            jobInstance.setStatus(JobInstanceStatusEnum.SUCCESS.getCode());
            jobInstanceService.updateById(jobInstance);
            return true;
        } finally {
            // remove the task from the tracking map after the task is completed
            runningTasks.remove(instanceId);
            cancelFlags.remove(instanceId);
        }
    }

    private List<String> getSourceFiles(Param sourceParam, String sourcePath, String filePattern, boolean recursive) {
        try (FileConnector sourceConnector = ConnectorFactory.createFileConnector(sourceParam)) {
            return sourceConnector.getFiles(sourcePath, filePattern, recursive);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    @Override
    public boolean cancelFileSync(Long instanceId) {
        List<Future<Void>> futures = runningTasks.get(instanceId);
        if (futures == null || futures.isEmpty()) {
            log.warn("No running file sync task found for instance: {}", instanceId);
            return false;
        }
        AtomicBoolean cancelFlag = cancelFlags.get(instanceId);
        log.info("Cancelling file sync task for instance: {}", instanceId);
        // set the cancel flag, so that the file being transferred can respond to the
        // cancel
        if (cancelFlag != null) {
            cancelFlag.set(true);
        }
        int cancelledCount = 0;
        for (Future<Void> future : futures) {
            if (future.cancel(true)) {
                cancelledCount++;
            }
        }
        // remove the task from the tracking map
        runningTasks.remove(instanceId);
        cancelFlags.remove(instanceId);
        // update the job instance status to cancelled
        try {
            JobInstance jobInstance = jobInstanceService.getById(instanceId);
            if (jobInstance != null) {
                jobInstance.setEndTime(new Date());
                jobInstance.setStatus(JobInstanceStatusEnum.CANCELLED.getCode());
                jobInstanceService.updateById(jobInstance);
            }
        } catch (Exception e) {
            log.error("Failed to update job instance status to cancelled for instance: {}", instanceId, e);
        }
        log.info("Cancelled {} out of {} file sync tasks for instance: {}", cancelledCount, futures.size(), instanceId);
        return cancelledCount > 0;
    }
}