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

package io.dataround.link.entity.dto;

import io.dataround.link.entity.Connector;
import io.dataround.link.entity.ConnectorVersion;
import lombok.Data;

/** 
 * ConnectorDto is the data transfer object for connector and connector version.
 * 
 * @author yuehan124@gmail.com
 * @since 2026-01-17
 */
@Data
public class ConnectorDto {
    
    private Connector connector;
    private ConnectorVersion connectorVersion;

    public ConnectorDto(Connector connector, ConnectorVersion connectorVersion) {
        this.connector = connector;
        this.connectorVersion = connectorVersion;
    }
}
