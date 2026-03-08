/**
 * Copyright (C) 2025 yuehan124@gmail.com
 *
 * This repository is licensed under the Dataround Open Source License
 */
package io.dataround.link.common.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * CookieUtils
 * 
 * @author yuehan124@gmail.com
 * @since 2025/09/21
 */
public class CookieUtils {

    public static final String COOKIE_KEY_UID = "uid";
    public static final String COOKIE_KEY_PID = "pid";
    public static final String COOKIE_KEY_CAPTCHA = "cap";
    public static final int REFRESH_THRESHOLD = 600;
    public static final int EXPIRATION_TIME = 1800;

    public static Cookie addUidCookie(String token, HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie(COOKIE_KEY_UID, token);
        String domain = RequestUtils.getDomain(request);
        cookie.setDomain(getCookieDomain(domain));
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return cookie;
    }

    public static void addCaptchaCookie(String value, HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie(COOKIE_KEY_CAPTCHA, value);
        String domain = RequestUtils.getDomain(request);
        cookie.setDomain(getCookieDomain(domain));
        cookie.setPath("/");
        // allow js read
        cookie.setHttpOnly(false);
        response.addCookie(cookie);
    }

    public static void addProjectCookie(String value, HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie(COOKIE_KEY_PID, value);
        String domain = RequestUtils.getDomain(request);
        cookie.setDomain(getCookieDomain(domain));
        cookie.setMaxAge(EXPIRATION_TIME * 2 * 24 * 3600);
        cookie.setPath("/");
        // allow js read
        cookie.setHttpOnly(false);
        response.addCookie(cookie);
    }

    public static String getCookie(String key, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (key.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static void cleanCookie(String name, HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie == null) {
                    continue;
                }
                if (name == null || name.equals(cookie.getName())) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }

    public static void cleanAllCookies(HttpServletRequest request, HttpServletResponse response) {
        cleanCookie(null, request, response);
    }

    /**
     * set cookie domain to it's top domain
     */
    private static String getCookieDomain(String domain) {
        // escape domain name isn't ipv4
        if (domain.matches("^\\d+\\.\\d+\\.\\d+\\.\\d+$")) {
            return domain;
        }
        String[] array = domain.split("\\.");
        if (array.length > 2) {
            domain = array[array.length - 2] + "." + array[array.length - 1];
        }
        return domain;
    }
}
