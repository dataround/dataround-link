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

package io.dataround.link.entity.req;

import java.util.List;

import com.alibaba.fastjson2.JSONObject;

import io.dataround.link.entity.VirtualField;
import io.dataround.link.entity.VirtualTable;
import lombok.Getter;
import lombok.Setter;

/**
 * Request entity for VirtualTable.
 * Extends VirtualTable to include request-specific fields.
 * 
 * @author yuehan124@gmail.com
 * @date 2025-06-18
 */
@Getter
@Setter
public class VirtualTableReq extends VirtualTable {
    
    private JSONObject jsonConfig;
    
    private List<VirtualField> fields;
}
