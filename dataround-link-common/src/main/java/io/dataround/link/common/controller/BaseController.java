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

package io.dataround.link.common.controller;

import io.dataround.link.common.entity.res.UserResponse;
import io.dataround.link.common.exception.LinkException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Base controller class that provides common functionality for all controllers.
 * This class includes methods for accessing current user information and HTTP
 * request/response objects.
 *
 * @author yuehan124@gmail.com
 * @since 2025-05-06
 */
public class BaseController {
     
    public UserResponse getCurrentUser() {
        String uid = getRequest().getHeader("uid");
        String pid = getRequest().getHeader("pid");
        if (uid == null || pid == null) {
            throw new LinkException("User ID or Project ID is required, please login again");
        }
        UserResponse currentUser = new UserResponse();
        currentUser.setUserId(Long.parseLong(uid));
        currentUser.setProjectId(Long.parseLong(pid));
        return currentUser;
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getUserId();
    }

    public Long getCurrentProjectId() {
        return getCurrentUser().getProjectId();
    }

    public HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            return attributes.getRequest();
        }
        return null;
    }

    public HttpServletResponse getResponse() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            return attributes.getResponse();
        }
        return null;
    }
}
