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

import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;

/**
 * Value object representing table mapping and field correspondence.
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
@Data
public class TableMapping {

    private String sourceDbName;
    private String sourceTable;
    private String whereClause;
    private String targetDbName;
    private String targetTable;
    // 1: insert, 2: upsert
    private Integer writeType;
    // 1: by name, 2: by sort
    private Integer matchMethod;
    // field mapping
    private List<FieldMapping> fieldMapping;

    /**
     * Get field mappings sorted with primary key fields first
     * 
     * @return sorted field mappings with primary key fields at the beginning
     */
    public List<FieldMapping> getSorteFieldMappings() {
        if (fieldMapping == null) {
            return null;
        }
        return fieldMapping.stream()
                .sorted(Comparator.comparing(
                    (FieldMapping fm) -> Boolean.FALSE.equals(fm.getTargetPrimaryKey())
                ))
                .collect(Collectors.toList());
    }

    /**
     * Get primary key fields
     * 
     * @return primary key fields
     */
    public List<String> getPrimaryKeyFields() {
        if (fieldMapping == null) {
            return null;
        }
        return fieldMapping.stream()
                .filter(fm -> Boolean.TRUE.equals(fm.getTargetPrimaryKey()))
                .map(fm -> fm.getTargetFieldName())
                .collect(Collectors.toList());
    }
}
