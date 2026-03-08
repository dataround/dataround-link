/*
 * Copyright (C) 2025 yuehan124@gmail.com
 *
 * This repository is licensed under the Dataround Open Source License
 */

package io.dataround.link.interceptor;

import io.dataround.link.common.entity.res.UserResponse;
import io.dataround.link.utils.Constants;
import io.dataround.link.entity.Resource;
import io.dataround.link.service.ResourceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Permission interceptor for API access control, Match rules:
 * Permission	            Matches
 * /api/user	            /api/user:GET, /api/user/list:POST, /api/user/123:DELETE (any method)
 * /api/user:GET	        /api/user:GET, /api/user/list:GET, /api/user/123:GET
 * /api/user/{id}:DELETE	/api/user/123:DELETE
 * /api/user:GET	     ❌/api/user:POST, /api/userrole:GET
 * 
 * @author yuehan124@gmail.com
 * @since 2026/02/21
 */
@Slf4j
@Component
public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    private ResourceService resourceService;

    // Cache for user resources (simple implementation, consider Redis for production)
    private final ConcurrentHashMap<Long, Set<String>> userApiCache = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Skip non-API requests
        String context = request.getContextPath();
        String requestURI = request.getRequestURI();
        if (!requestURI.startsWith(context + "/api")) {
            return true;
        }

        // Get current user from request attribute (set by AuthInterceptor)
        UserResponse currentUser = (UserResponse) request.getAttribute(Constants.CURRENT_USER);
        if (currentUser == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        // Get user's API resources
        Set<String> userApis = userApiCache.get(currentUser.getUserId());
        if (userApis == null) {
            List<Resource> resources = resourceService.getResourcesByUserId(currentUser.getUserId());
            userApis = resources.stream()
                .filter(r -> "api".equals(r.getType()))
                .map(r -> buildApiKey(r.getResKey(), r.getMethod()))
                .filter(Objects::nonNull) // Filter out null keys
                .collect(java.util.stream.Collectors.toSet());
            userApiCache.put(currentUser.getUserId(), userApis);
        }

        // Check permission
        String method = request.getMethod();
        String apiKey = buildApiKey(requestURI.replace(context, ""), method);
        
        // Support path variables like /api/user/{id}
        boolean hasPermission = userApis.stream().anyMatch(api -> matchApiPath(api, apiKey));

        if (!hasPermission) {
            log.warn("User {} has no permission to access {} {}", currentUser.getUserId(), method, requestURI);
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":403,\"message\":\"No permission\"}");
            return false;
        }

        return true;
    }

    /**
     * Build API key from path and method
     * Format: PATH:METHOD or just PATH if method is null
     * e.g., "/api/user:GET", "/api/role/{id}:DELETE", "/api/resource"
     */
    private String buildApiKey(String path, String method) {
        if (method == null || method.trim().isEmpty()) {
            return path;
        }
        return path + ":" + method;
    }

    /**
     * Match API path with path variables support and prefix matching
     * Format: PATH:METHOD or just PATH
     * Examples:
     * - "/api/user/{id}:DELETE" matches "/api/user/12345:DELETE"
     * - "/api/user:GET" matches "/api/user/list:GET", "/api/user/123:GET"
     * - "/api/user" matches "/api/user:GET", "/api/user/list:POST", etc. (any method)
     */
    private boolean matchApiPath(String pattern, String actual) {
        if (pattern.equals(actual)) {
            return true;
        }

        // Split pattern and actual into path and method
        int patternColonIdx = pattern.lastIndexOf(':');
        int actualColonIdx = actual.lastIndexOf(':');
        
        String patternPath;
        String patternMethod = null;
        if (patternColonIdx > 0) {
            patternPath = pattern.substring(0, patternColonIdx);
            patternMethod = pattern.substring(patternColonIdx + 1);
        } else {
            patternPath = pattern;
        }

        String actualPath;
        String actualMethod = null;
        if (actualColonIdx > 0) {
            actualPath = actual.substring(0, actualColonIdx);
            actualMethod = actual.substring(actualColonIdx + 1);
        } else {
            actualPath = actual;
        }

        // If pattern has method, it must match
        if (patternMethod != null && actualMethod != null && !patternMethod.equals(actualMethod)) {
            return false;
        }

        // Check exact match with path variables
        String regex = patternPath.replaceAll("\\{[^}]+\\}", "[^/]+");
        if (actualPath.matches(regex)) {
            return true;
        }

        // Check prefix match: /api/user should match /api/user/list, /api/user/123, etc.
        // Pattern without path variables can match as prefix
        if (!patternPath.contains("{")) {
            // Ensure prefix match is at path segment boundary
            // e.g., /api/user matches /api/user/list but not /api/userrole
            if (actualPath.startsWith(patternPath)) {
                // Must be exact match or followed by /
                if (actualPath.length() == patternPath.length() ||
                    actualPath.charAt(patternPath.length()) == '/') {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Clear cache for a user (call when user's roles change)
     */
    public void clearUserCache(Long userId) {
        userApiCache.remove(userId);
    }

    /**
     * Clear all cache (call when roles or resources change)
     */
    public void clearAllCache() {
        userApiCache.clear();
    }
}
