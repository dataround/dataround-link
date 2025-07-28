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

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * Entity class representing a job execution instance.
 * Contains execution details including status, timing, metrics and configuration information.
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
@Data
@TableName("job_instance")
public class JobInstance {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long jobId;
    private Long projectId;
    // 0:waiting, 1:submitted, 2:running, 3:success, 4:failed
    private Integer status;
    private String jobConfig;
    private String seatunnelId;
    private Date startTime;
    private Date endTime;
    private Long readCount;
    private Long writeCount;
    private Double readQps;
    private Double writeQps;
    private Long readBytes;
    private Long writeBytes;
    private String logContent;
    private Long updateBy;
    private Date updateTime;
}
