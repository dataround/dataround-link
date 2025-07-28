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

package io.dataround.link.common.connector;

import java.util.Map;

import lombok.Data;

/**
 * Connector parameter
 * 
 * @author yuehan124@gmail.com
 * @date 2025-07-27
 */
@Data
public class Param {

    private String name;
    private String type;
    private String host;
    private Integer port;
    private String user;
    private String password;
    private Map<String, String> config;
    // lib directory, used to load connector jar
    private String libDir;
}
