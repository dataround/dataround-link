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
 * @date 2025-05-06
 */
public class BaseController {

    private UserResponse mockUser = new UserResponse();

    public BaseController() {
        mockUser.setUserId(10000L);
        mockUser.setUserName("admin");
        mockUser.setProjectId(10000L);
    }

    public UserResponse getCurrentUser() {
        return mockUser;
    }

    public Long getCurrentUserId() {
        return mockUser.getUserId();
    }

    public Long getCurrentProjectId() {
        return mockUser.getProjectId();
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
