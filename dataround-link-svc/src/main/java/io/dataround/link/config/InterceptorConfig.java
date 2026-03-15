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

package io.dataround.link.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.dataround.link.interceptor.AuthInterceptor;
import io.dataround.link.interceptor.PermissionInterceptor;

/**
 * Interceptor configuration for authentication.
 *
 * @author yuehan124@gmail.com
 * @since 2025/01/31
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private WebConfig webConfig;
    @Autowired
    private AuthInterceptor authInterceptor;
    @Autowired
    @Lazy
    private PermissionInterceptor permissionInterceptor;

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        String[] escapePatterns = webConfig.getLoginEscape().split(",");
        for (int i = 0; i < escapePatterns.length; i++) {
            escapePatterns[i] = escapePatterns[i].trim();
        }
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**") // intercept all request
                .excludePathPatterns(escapePatterns); // exclude path that do not require login
        // Permission interceptor (runs after auth interceptor)
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/api/**") // only intercept API requests
                .excludePathPatterns(escapePatterns);
    }
}
