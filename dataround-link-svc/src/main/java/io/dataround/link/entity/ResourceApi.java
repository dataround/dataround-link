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

/**
 * Resource API entity - stores API paths and methods for each resource
 * 
 * @author yuehan124@gmail.com
 * @since 2026/05/03
 */
@Data
@TableName("public.resource_api")
public class ResourceApi {

    @TableId(type = IdType.AUTO)
    private Long id;
    // Parent resource ID
    private Long resourceId;
    // HTTP method (GET/POST/PUT/DELETE)
    private String method;
    // API path (e.g., /api/user/list)
    private String path;
}
