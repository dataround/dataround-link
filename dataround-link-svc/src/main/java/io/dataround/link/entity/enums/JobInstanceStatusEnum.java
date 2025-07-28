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

package io.dataround.link.entity.enums;

/**
 * Job instance status enum
 * @author yuehan124@gmail.com
 * @since 2025-06-09
 */
public enum JobInstanceStatusEnum {
    SUBMITTED(1, "SUBMITTED"), RUNNING(2, "RUNNING"), SUCCESS(3, "SUCCESS"), FAILURE(4, "FAILURE"),
    CANCELLED(5, "CANCELLED");

    private final int code;
    private final String description;

    JobInstanceStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
