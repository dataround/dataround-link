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
 * Entity class representing a user in the system.
 * Contains user information including name, contact details and account status.
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
@Data
@TableName("public.user")
public class User {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String name;
    private String email;
    private String cellphone;
    private String passwd;
    private Boolean disabled;
    private Long createBy;
    private Long updateBy;
    private Date createTime;
    private Date updateTime;
}
