/*
 * Copyright (C) 2025 yuehan124@gmail.com
 *
 * This repository is licensed under the Dataround Open Source License
 */

package io.dataround.link.interceptor;

import io.dataround.link.common.utils.CookieUtils;
import io.dataround.link.common.utils.JwtUtil;
import io.dataround.link.common.entity.res.UserResponse;
import io.dataround.link.utils.Constants;
import io.dataround.link.common.utils.RequestUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

import static io.dataround.link.common.utils.CookieUtils.EXPIRATION_TIME;
import static io.dataround.link.common.utils.CookieUtils.REFRESH_THRESHOLD;

/**
 * Auth interceptor
 * 
 * @author yuehan124@gmail.com
 * @since 2025/09/21
 */
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException {
        try {
            String remoteIp = RequestUtils.getRemoteIp(request);
            String value = CookieUtils.getCookie(CookieUtils.COOKIE_KEY_UID, request);
            UserResponse user = JwtUtil.verifyToken(value, remoteIp);
            // extension cookie expire time automatically
            if (user.getExpiration() < System.currentTimeMillis() + REFRESH_THRESHOLD * 1000) {
                long expire = System.currentTimeMillis() + EXPIRATION_TIME * 1000;
                user.setExpiration(expire);
                CookieUtils.addUidCookie(JwtUtil.genToken(user), request, response);
            }
            // login success and continue
            request.setAttribute(Constants.CURRENT_USER, user);
            return true;
        } catch (Exception e) {
            log.error("authentication exception", e);
        }
        redirectToLogin(request, response);
        return false;
    }

    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (RequestUtils.isAjaxRequest(request)) {
                log.info("ajax request with invalid cookie, response status 401");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
            } else {
                // clean cookie
                CookieUtils.cleanAllCookies(request, response);
                String redirectUrl = RequestUtils.getLoginUrl(request);
                response.sendRedirect(redirectUrl);
            }
        } catch (IOException e) {
            log.error("response.sendRedirect exception", e);
        }
    }


}
