/*
 * Copyright (C) 2025 yuehan124@gmail.com
 *
 * This repository is licensed under the Dataround Open Source License
 */

package io.dataround.link.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * Resource entity (menus and APIs)
 * 
 * @author yuehan124@gmail.com
 * @since 2025/02/19
 */
@Data
@TableName("public.resource")
public class Resource {

    @TableId(type = IdType.AUTO)
    private Long id;
    // Parent resource ID, 0 for root
    private Long pid;
    private String name;
    private String enName;
    // Resource type: 'ui' for frontend menu/button, 'api' for backend API
    private String type;
    // Resource key: ui_key for frontend, api_path for backend
    private String resKey;
    // HTTP method for API resources (GET/POST/PUT/DELETE)
    private String method;
    private String description;
    private Date createTime;
}
