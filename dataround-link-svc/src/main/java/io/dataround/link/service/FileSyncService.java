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

import io.dataround.link.entity.res.JobRes;

/**
 * Service interface for FileSync operations.
 *
 * @author yuehan124@gmail.com
 * @date 2025-06-05
 */
public interface FileSyncService {
    
    /**
     * Execute file synchronization task.
     *
     * @param properties the file sync task config
     * @param instanceId the job instance id
     * @return true if executed successfully, false otherwise
     */
    boolean executeFileSync(JobRes jobVo, Long instanceId);
} 