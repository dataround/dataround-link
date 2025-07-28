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

package io.dataround.link.entity.res;

import lombok.Data;

import java.util.Date;

/**
 * Response entity for file synchronization task with extended connection information.
 *
 * @author yuehan124@gmail.com
 * @date 2025-06-05
 */
@Data
public class FileSyncRes {
    
    private Long id;
    private Long projectId;
    private String name;
    private String description;
    
    // Source configuration
    private Long sourceConnectionId;
    private String sourceConnectionName;
    private String sourcePath;
    private String filePattern;
    private Boolean includeSubdirectories;
    
    // Target configuration
    private Long targetConnectionId;
    private String targetConnectionName;
    private String targetPath;
    
    // Sync configuration
    private String syncMode;
    private Boolean enabled;
    
    // Audit fields
    private Long createBy;
    private Long updateBy;
    private Date createTime;
    private Date updateTime;
} 