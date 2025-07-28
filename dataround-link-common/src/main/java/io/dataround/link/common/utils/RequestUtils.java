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

package io.dataround.link.common.utils;

import org.springframework.http.server.reactive.ServerHttpRequest;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Utility class for handling HTTP request related operations.
 * Provides methods for extracting request information such as domain, IP address,
 * port, schema and checking request types.
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
public class RequestUtils {

    private static final String XML_HTTP_REQUEST = "XMLHttpRequest";

    public static String getDomain(HttpServletRequest request) {
        String host = request.getHeader("X-Forwarded-Host");
        if (host == null) {
            host = request.getServerName();
        } else if (host.contains(",")) {
            host = host.substring(0, host.indexOf(","));
        }
        if (host.contains(":")) {
            host = host.substring(0, host.indexOf(":"));
        }
        return host;
    }

    public static String getRemoteIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null) {
            ip = request.getRemoteAddr();
        } else if (ip.contains(",")) {
            ip = ip.substring(0, ip.indexOf(","));
        }
        return ip;
    }

    public static boolean isDefaultPort(HttpServletRequest request) {
        int port = getRequestPort(request);
        String schema = getRequestSchema(request);
        return (schema.equalsIgnoreCase("https") && port == 443)
                || (schema.equalsIgnoreCase("http") && port == 80);
    }

    public static int getRequestPort(HttpServletRequest request) {
        String port = request.getHeader("X-Forwarded-Port");
        if (port != null) {
            if (port.contains(",")) {
                port = port.substring(0, port.indexOf(","));
            }
            return Integer.parseInt(port);
        }
        return request.getServerPort();
    }

    // get original request schema
    public static String getRequestSchema(HttpServletRequest request) {
        String schema = request.getHeader("X-Forwarded-Proto");
        if (schema == null) {
            schema = request.getScheme();
        } else if (schema.contains(",")) {
            schema = schema.substring(0, schema.indexOf(","));
        }
        return schema;
    }

    public static boolean isAjaxRequest(HttpServletRequest request) {
        return XML_HTTP_REQUEST.equalsIgnoreCase(request.getHeader("x-requested-with"));
    }

    public static boolean isAjaxRequest(ServerHttpRequest request) {
        return XML_HTTP_REQUEST.equalsIgnoreCase(request.getHeaders().getFirst("x-requested-with"));
    }
}
