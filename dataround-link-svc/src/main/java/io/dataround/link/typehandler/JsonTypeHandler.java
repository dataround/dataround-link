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

package io.dataround.link.typehandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.postgresql.util.PGobject;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.dataround.link.SpringContextUtil;
import io.dataround.link.config.DatabaseConfig;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.type.TypeReference;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * Custom TypeHandler for handling JSON/JSONB data type in both PostgreSQL and H2 databases.
 * Provides conversion between Java Map<String, Object> and database JSON/JSONB type.
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
@Slf4j
public class JsonTypeHandler extends BaseTypeHandler<Map<String, Object>> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Map<String, Object> parameter, JdbcType jdbcType) throws SQLException {
        DatabaseConfig databaseConfig = SpringContextUtil.getBean(DatabaseConfig.class);
        try {
            // Check if we're using PostgreSQL or H2
            if (databaseConfig.getIsPostgreSQL()) {
                PGobject jsonObject = new PGobject();
                jsonObject.setType("jsonb");
                jsonObject.setValue(objectMapper.writeValueAsString(parameter));
                ps.setObject(i, jsonObject);
            } else {
                // For H2 database
                ps.setString(i, objectMapper.writeValueAsString(parameter));
            }
        } catch (Exception e) {
            throw new SQLException("Error converting Map to JSON", e);
        }
    }

    @Override
    public Map<String, Object> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parseJson(rs.getObject(columnName));
    }

    @Override
    public Map<String, Object> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parseJson(rs.getObject(columnIndex));
    }

    @Override
    public Map<String, Object> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parseJson(cs.getObject(columnIndex));
    }

    private Map<String, Object> parseJson(Object jsonValue) throws SQLException {
        if (jsonValue == null) {
            return null;
        }
        try {
            String jsonString = null;
            if (jsonValue instanceof PGobject) {
                // Handle PostgreSQL JSONB type
                jsonString = ((PGobject) jsonValue).getValue();
            } else if (jsonValue instanceof byte[]) {
                // Handle H2 JSON type
                jsonString = new String((byte[]) jsonValue);         
            } else {
                log.warn("Unsupported JSON type: " + jsonValue.getClass());
            }
            if (jsonString == null || jsonString.trim().isEmpty()) {
                return null;
            }
            
            // Handle escaped string case (when string contains escaped quotes)
            if (jsonString.startsWith("\"") && jsonString.endsWith("\"") && jsonString.length() > 1) {
                // Try to parse as escaped JSON string
                try {
                    String unescaped = objectMapper.readValue(jsonString, String.class);
                    return objectMapper.readValue(unescaped, new TypeReference<Map<String, Object>>() {});
                } catch (Exception e) {
                    // If that fails, treat as regular JSON
                    return objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {});
                }
            }
            
            return objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new SQLException("Error parsing JSON to Map: " + jsonValue, e);
        }
    }
    
}