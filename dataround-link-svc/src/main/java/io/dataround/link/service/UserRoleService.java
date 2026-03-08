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

package io.dataround.link.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.dataround.link.entity.UserRole;

import java.util.List;

/**
 * User-Role service
 * 
 * @author yuehan124@gmail.com
 * @since 2026/02/19
 */
public interface UserRoleService extends IService<UserRole> {

    /**
     * Get role IDs by user ID
     */
    List<Long> getRoleIdsByUserId(Long userId);

    /**
     * Assign roles to user
     */
    void assignRolesToUser(Long userId, List<Long> roleIds);

    /**
     * Remove user's all roles
     */
    void removeUserRoles(Long userId);
}
