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
 * Entity class representing a connector definition.
 * Contains connector configuration including name, type, plugin information and properties.
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
@Data
@TableName("connector")
public class Connector {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String type;
    private String pluginName;
    private Boolean supportSource;
    private Boolean supportSink;
    private Boolean isStream;
    private Boolean virtualTable;
    @TableField(typeHandler = JsonbTypeHandler.class)
    private Map<String, String> properties;
    private Long createBy;
    private Long updateBy;
    private Date createTime;
    private Date updateTime;
} 