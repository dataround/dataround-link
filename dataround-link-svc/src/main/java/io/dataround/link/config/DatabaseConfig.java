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

import jakarta.annotation.PostConstruct;
import lombok.Getter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


/**
 * Database configuration class to initialize database type used in JsonbTypeHandler
 *
 * @author yuehan124@gmail.com
 * @date 2025-11-29
 */
@Configuration
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Getter
    private Boolean isPostgreSQL;

    @PostConstruct
    public void init() {
        String lowerUrl = databaseUrl.toLowerCase();
        if (lowerUrl.contains(":postgresql:") || lowerUrl.contains(":postgres:")) {
            isPostgreSQL = true;
        }  else {
            isPostgreSQL = false;
        }
    }
}