/*
 * Copyright (C) 2025 yuehan124@gmail.com
 *
 * This repository is licensed under the Dataround Open Source License
 */

package io.dataround.link.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.dataround.link.common.entity.res.UserResponse;
import io.dataround.link.common.enums.UserStatusEnum;
import io.dataround.link.entity.Project;
import io.dataround.link.entity.ProjectUser;
import io.dataround.link.entity.User;
import io.dataround.link.mapper.UserMapper;
import io.dataround.link.service.ProjectService;
import io.dataround.link.service.ProjectUserSerivce;
import io.dataround.link.service.UserService;
import io.dataround.link.utils.Constants;
import io.dataround.link.utils.SHA256Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * User service impl
 * 
 * @author yuehan124@gmail.com
 * @since 2025/09/21
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectUserSerivce projectUserSerivce;

    @Override
    public UserResponse login(String name, String passwd) {
        // If the user name and password is not correctly, return null
        return userMapper.login(name, SHA256Util.getSHA256(passwd));
    }

    @Override
    public Map<Long, String> listNameByIds(Set<Long> userIds) {
        if (userIds.isEmpty()) {
            return new HashMap<>();
        }
        List<User> users = this.listByIds(userIds);
        return users.stream().collect(Collectors.toMap(User::getId, User::getName));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveUserWithDefaultProject(User user, Long currentUserId) {
        Date now = new Date();
        boolean isNewUser = (user.getId() == null);
        
        // Set common fields
        if (isNewUser) {
            user.setCreatorId(currentUserId);
            user.setCreateTime(now);
        }
        if (user.getStatus() == null) {
            user.setStatus(UserStatusEnum.NORMAL.getCode());
        }
        if (StringUtils.isNotBlank(user.getPasswd())) {
            user.setPasswd(SHA256Util.getSHA256(user.getPasswd().trim()));
        }
        user.setUpdaterId(currentUserId);
        user.setUpdateTime(now);
        
        // Save or update user
        boolean result = this.saveOrUpdate(user);
        
        // If it's a new user, add to default project
        if (result && isNewUser) {
            // Find default project
            Project defaultProject = projectService.lambdaQuery().eq(Project::getName, Constants.DEFAULT_PROJECT_NAME).one();
            if (defaultProject != null) {
                // Add user to default project
                ProjectUser projectUser = new ProjectUser();
                projectUser.setUserId(user.getId());
                projectUser.setProjectId(defaultProject.getId());
                projectUser.setIsAdmin(false);
                projectUser.setSelected(true);
                projectUserSerivce.save(projectUser);
                log.info("New user {} automatically added to default project", user.getName());
            } else {
                log.warn("Default project not found, user {} not added to any project", user.getName());
            }
        }
        
        return result;
    }
}
