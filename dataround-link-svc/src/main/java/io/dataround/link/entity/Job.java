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

package io.dataround.link.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.dataround.link.typehandler.JsonbTypeHandler;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * Entity class representing a job definition.
 * Contains job configuration including type, schedule, timing and connection information.
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
@Data
@TableName("job")
public class Job {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long projectId;
    private String name;
    private String description;
    // 1: batch, 2: streaming
    private Integer jobType;
    // 1: run once, 2: run every day, 3: not run
    private Integer scheduleType;
    private String cron;
    private Date startTime;
    private Date endTime;
    @TableField(typeHandler = JsonbTypeHandler.class)
    private Map<String, Object> config;
    private Long createBy;
    private Long updateBy;
    private Date createTime;
    private Date updateTime;
}
