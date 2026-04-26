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

package io.dataround.link.connector;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

import io.dataround.link.common.utils.ConnectorNameConstants;

/**
 * Doris JDBC connector
 * Doris is compatible with MySQL protocol
 * 
 * @author yuehan124@gmail.com
 * @since 2026-04-25
 */
@Slf4j
public class DorisJdbcConnector extends MySQLJdbcConnector {

    private final String name = ConnectorNameConstants.DORIS;

    @Override
    public String getName() {
        return this.name;
    }

}
