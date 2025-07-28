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
 * Enum representing job schedule types in the system.
 * Defines constants for run now, scheduled, and not run job schedules.
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
public enum JobScheduleTypeEnum {

    RUN_NOW(1, "RUN_NOW"), SCHEDULED(2, "SCHEDULED"), NOT_RUN(3, "NOT_RUN");

    private final int code;
    private final String description;

    JobScheduleTypeEnum(int code, String description) {
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
