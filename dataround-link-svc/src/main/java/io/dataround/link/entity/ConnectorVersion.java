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

import lombok.Data;

import java.util.Date;

/**
 * Entity class representing a connector version.
 *
 * @author yuehan124@gmail.com
 * @date 2025-12-16
 */
@Data
@TableName("connector_version")
public class ConnectorVersion {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String connector;
    private String label;
    @TableField("\"value\"")
    private String value;
    private Boolean isDefault;
    private String description;
    private Long createBy;
    private Long updateBy;
    private Date createTime;
    private Date updateTime;
} 