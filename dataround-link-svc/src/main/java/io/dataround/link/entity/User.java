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
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

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

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String email;
    private String cellphone;
    private String passwd;
    // Profile picture URL
    private String avatar;
    private String gender;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birthday;
    // Department name
    private String department;
    // Job position
    private String position;
    // Address
    private String address;
    private String wechat;
    // Status: 1-Normal, 2-Disabled, 3-Locked
    private Integer status;
    private String remark;
    private String lastLoginIp;
    private String lastLoginTime;
    private Long creatorId;
    private Long updaterId;
    private Date createTime;
    private Date updateTime;
}
