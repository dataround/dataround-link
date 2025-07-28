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

package io.dataround.link.entity.req;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

import io.dataround.link.entity.Job;
import io.dataround.link.entity.res.TableMapping;
import lombok.Getter;
import lombok.Setter;

/**
 * Value object for job definition with connector and mapping details.
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobReq extends Job {

    private String sourceConnector;
    private String targetConnector;
    private Long sourceConnId;
    private Long targetConnId;
    private String sourceDbName;
    private String targetDbName;
    private List<TableMapping> tableMapping;

    // Non db field
    private String sourceConnectionName;
    private String targetConnectionName;
    private String updateUserName;

    // FileSync
    private String sourcePath;
    private String targetPath;
    private String filePattern;
    private Boolean includeSubdirectories;
}
