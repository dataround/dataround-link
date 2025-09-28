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

import io.dataround.link.entity.Connection;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Kafka connection value object
 * 
 * @author yuehan124@gmail.com
 * @date 2025-09-26
 */
@Getter
@Setter
public class KafkaConnectionVo extends ConnectionVo {

    private String broker;
    private static final String KEY_BROKER_SERVER = "bootstrap.servers";

    @Override
    public void extractProperties(Connection connection) {
        Map<String, String> config = connection.getConfig();
        config.put(KEY_BROKER_SERVER, broker);
    }

    @Override
    public void fillProperties(Map<String, String> config) {
        setBroker(config.get(KEY_BROKER_SERVER));
        // remove config items, other items was used to show extra param for web page
        config.remove(KEY_BROKER_SERVER);
    }
}
