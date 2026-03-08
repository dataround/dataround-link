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

import java.util.Date;

/**
 * Project entity
 * 
 * @author yuehan124@gmail.com
 * @since 2025/09/21
 */
@Data
@TableName("public.project")
public class Project {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private Long creatorId;
    private Date createTime;

    // Non db field
    @TableField(exist = false)
    private Boolean isAdmin;
    @TableField(exist = false)
    private Boolean selected;
    @TableField(exist = false)
    private String createUser;

}
