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

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Menu configuration properties
 * 
 * @author yuehan124@gmail.com
 * @since 2026/02/23
 */
@Data
@Component
@ConfigurationProperties(prefix = "dataround.link.menu")
public class MenuProperties {

    private List<MenuItem> items = new ArrayList<>();

    @Data
    public static class MenuItem {
        private String key;
        private String labelKey;
        private String url;
        private boolean external;
    }
}
