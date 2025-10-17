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

package io.dataround.link.entity.vo;

import java.util.Map;

import io.dataround.link.entity.Connection;
import lombok.Getter;
import lombok.Setter;

/**
 * Jdbc connection value object
 * 
 * @author yuehan124@gmail.com
 * @date 2025-09-26
 */
@Getter
@Setter
public class JdbcConnectionVo extends ConnectionVo {

    private String url;
    private String driver;
    private String database;
    // oracle sid or svc name
    private String svcType;

    @Override
    public void extractProperties(Connection connection) {
        Map<String, String> config = connection.getConfig();
        config.put("url", url);
        config.put("driver", driver);
        config.put("database", database);
        if (svcType != null) {
            config.put("svcType", svcType);
        }
    }

    @Override
    public void fillProperties(Map<String, String> config) {
        setUrl(config.get("url"));
        setDriver(config.get("driver"));
        setDatabase(config.get("database"));
        setSvcType(config.get("svcType"));
        // remove config items, other items was used to show extra param for web page
        config.remove("url");
        config.remove("driver");
        config.remove("database");
        config.remove("svcType");
    }
}
