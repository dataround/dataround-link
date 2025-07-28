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

package io.dataround.link.service;

import java.util.Map;

import io.dataround.link.entity.Job;
import io.dataround.link.entity.res.JobRes;

/**
 * Service interface for managing job configurations.
 * Provides methods for handling job configuration operations including HOCON generation and VO conversion.
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
public interface JobConfigService {

    String getJobJson(JobRes jobVo);

    JobRes getJobVo(Job job);

    Map<String, String> getProperties(Job job);
}
