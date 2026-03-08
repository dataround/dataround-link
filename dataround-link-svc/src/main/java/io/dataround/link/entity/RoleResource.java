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
 * Role-Resource mapping entity
 * 
 * @author yuehan124@gmail.com
 * @since 2025/02/19
 */
@Data
@TableName("public.role_resource")
public class RoleResource {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long roleId;
    private Long resourceId;
    private Date createTime;
}
