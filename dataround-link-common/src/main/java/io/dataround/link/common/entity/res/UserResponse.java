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

package io.dataround.link.common.entity.res;

import lombok.Data;

/**
 * User information class that contains user details and project context.
 * This class stores information about the current user including their ID, name,
 * project context, IP address and session expiration time.
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
@Data
public class UserResponse {
    // user id
    private Long userId;
    // user name
    private String userName;
    // current project id
    private Long projectId;
    // current project name
    private String projectName;
    // user ip
    private String userIp;
    // expire time
    private long expiration;
}
