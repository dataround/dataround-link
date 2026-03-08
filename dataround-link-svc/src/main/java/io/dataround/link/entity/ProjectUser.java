/*
 * Copyright (C) 2025 yuehan124@gmail.com
 *
 * This repository is licensed under the Dataround Open Source License
 */

package io.dataround.link.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * Project user entity
 * 
 * @author yuehan124@gmail.com
 * @since 2025/09/21
 */
@Data
@TableName("public.project_user")
public class ProjectUser {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long projectId;
    private Long userId;
    private Boolean isAdmin;
    private Boolean selected;

    @TableField(exist = false)
    private String userName;
}
