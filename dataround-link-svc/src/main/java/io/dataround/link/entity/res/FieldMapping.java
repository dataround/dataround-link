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

package io.dataround.link.entity.res;

import lombok.Data;

/**
 * Value object representing field mapping between source and target tables.
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
@Data
public class FieldMapping {
    private String sourceFieldName;
    private String sourceFieldType;
    private Boolean sourcePrimaryKey;
    private Boolean sourceNullable;
    private String targetFieldName;
    private String targetFieldType;
    private Boolean targetPrimaryKey;
    private Boolean targetNullable;
}
