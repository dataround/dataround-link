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

package io.dataround.link.utils;

/**
 * Utility class containing constant values used throughout the application.
 * Includes constants for user session, job instance status, host status, and plugin names.
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
public class Constants {
    public static final String CURRENT_USER = "currentUser";

    public static final Integer DEFAULT_LOG_RETURN_ROWS = 100;

    public static String PlUGIN_NAME_MYSQL_CDC = "MYSQL-CDC";

    // field mapping
    public static int FIELD_MAPPING_MATCH_BY_SORT = 1;
    public static int FIELD_MAPPING_MATCH_BY_NAME = 2;

    public static String CONNECTION_TYPE_DATABASE = "Database";
    public static String CONNECTION_TYPE_FILE = "File";

    public static String CONNECTOR_TYPE_FILE = "File";
}
