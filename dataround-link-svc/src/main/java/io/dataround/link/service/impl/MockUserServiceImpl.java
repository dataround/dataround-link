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

package io.dataround.link.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.dataround.link.entity.User;
import io.dataround.link.mapper.UserMapper;
import io.dataround.link.service.UserService;

/**
 * Implementation of the UserService interface.
 * Provides concrete implementation for managing user operations and user data retrieval.
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
@Service
public class MockUserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public Map<Long, String> listNameByIds(Collection<? extends Serializable> userIds) {
        if (userIds.isEmpty()) {
            return new HashMap<>();
        }
        List<User> users = this.listByIds(userIds);
        return users.stream().collect(Collectors.toMap(User::getId, User::getName));
    }

    @Override
    public List<User> listByIds(Collection<? extends Serializable> userIds) {
        List<User> users = new ArrayList<>();
        for (Serializable id: userIds) {
            User mockUser = new User();
            mockUser.setId((Long)id);
            mockUser.setName("admin");
        }
        return users;
    }

    @Override
    public User getById(Serializable id) {
        User mockUser = new User();
        mockUser.setId((Long)id);
        mockUser.setName("admin");
        return mockUser;
    }
}
