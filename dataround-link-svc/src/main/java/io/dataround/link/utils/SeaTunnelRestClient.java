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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Utility class for making REST API calls to SeaTunnel engine.
 */
@Slf4j
@Component
public class SeaTunnelRestClient {

    @Value("${dataround.seatunnel.api.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // seatunnel log be of no use when return the following string
    private static final String EMPTY_LOG = "<html><head><title>Seatunnel log</title></head>\n" +
            "<body>\n" +
            " <h2>Seatunnel log</h2>\n" +
            " <ul>\n" +
            " </ul>\n" +
            "</body></html>";

    public SeaTunnelRestClient() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Submit a job to SeaTunnel engine
     *
     * @param jobConfig Job configuration content
     * @return Job ID
     */
    public String submitJob(String jobConfig) {
        try {
            // Get job name from config
            JsonNode configJson = objectMapper.readTree(jobConfig);
            String jobName = configJson.get("env").get("job.name").asText();
            if (checkJobExistOrNot(jobName)) {
                log.error("jobName: {} already exists, please check", jobName);
                throw new RuntimeException("jobName: " + jobName + " already exists, please check");
            }
            // Submit job
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(jobConfig, headers);
            String response = restTemplate.postForObject(baseUrl + "/submit-job", request, String.class);
            JsonNode responseJson = objectMapper.readTree(response);
            return responseJson.get("jobId").asText();
        } catch (Exception e) {
            log.error("Failed to submit job", e);
            throw new RuntimeException("Failed to submit job", e);
        }
    }

    /**
     * Check if job exists by name
     *
     * @param jobName Job name
     * @return boolean
     */
    private boolean checkJobExistOrNot(String jobName) {
        try {
            // Get all running jobs
            String response = restTemplate.getForObject(baseUrl + "/running-jobs", String.class);
            JsonNode jobsJson = objectMapper.readTree(response);
            for (JsonNode job : jobsJson) {
                if (jobName.equals(job.get("jobName").asText())) {
                    return true;
                }
            }
            // Get all finished jobs
            response = restTemplate.getForObject(baseUrl + "/finished-jobs", String.class);
            jobsJson = objectMapper.readTree(response);
            for (JsonNode job : jobsJson) {
                if (jobName.equals(job.get("jobName").asText())) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            log.error("Failed to get job by name: {}", jobName, e);
            throw new RuntimeException("Failed to get job by name", e);
        }
    }

    /**
     * Get job detail from SeaTunnel engine
     *
     * @param jobId Job ID
     * @return Job detail
     */
    public JsonNode getJobDetail(String jobId) {
        try {
            String response = restTemplate.getForObject(baseUrl + "/job-info/" + jobId, String.class);
            JsonNode responseJson = objectMapper.readTree(response);
            return responseJson;
        } catch (Exception e) {
            log.error("Failed to get job detail for job {}", jobId, e);
            throw new RuntimeException("Failed to get job detail", e);
        }
    }

    /**
     * Stop a running job
     *
     * @param jobId Job ID
     */
    public void stopJob(String jobId) {
        stopJob(jobId, false);
    }

    /**
     * Stop a running job
     *
     * @param jobId             Job ID
     * @param stopWithSavePoint Whether to stop with save point
     */
    public void stopJob(String jobId, boolean stopWithSavePoint) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            // Create request body
            String requestBody = String.format("{\"jobId\": %s, \"isStopWithSavePoint\": %b}", jobId,
                    stopWithSavePoint);
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
            restTemplate.postForObject(baseUrl + "/stop-job", request, String.class);
        } catch (Exception e) {
            log.error("Failed to stop job {}", jobId, e);
            throw new RuntimeException("Failed to stop job", e);
        }
    }

    /**
     * Get job logs
     *
     * @param jobId Job ID
     * @return Job logs as string
     */
    public String getJobLogs(String jobId) {
        try {
            String logs = restTemplate.getForObject(baseUrl + "/logs/" + jobId, String.class);
            // EMPTY_LOG be of no use, return empty string
            return EMPTY_LOG.equals(logs) ? "" : logs;
        } catch (Exception e) {
            log.error("Failed to get logs for job {}", jobId, e);
            throw new RuntimeException("Failed to get job logs", e);
        }
    }
}