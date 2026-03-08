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
 * Role entity
 * 
 * @author yuehan124@gmail.com
 * @since 2026/02/19
 */
@Data
@TableName("public.role")
public class Role {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private Date createTime;
}
