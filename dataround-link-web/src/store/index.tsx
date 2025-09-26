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

/**
 * @auth: tiandengji
 * @date: 2025/5/15
 **/
import { proxy } from "valtio"

export const JOB_TYPE_BATCH = 1;
export const JOB_TYPE_STREAM = 2;
export const JOB_TYPE_FILESYNC = 3;
export const SCHEDULE_TYPE_RUNNOW = 1;

export const CRON_PER_MIN = "1";
export const CRON_PER_HOUR = "2";
export const CRON_PER_DAY = "3";
export const CRON_PER_WEEK = "4";
export const CRON_PER_MONTH = "5";

export interface Job {
    id: number
    name: string
    description: string
}

export const vtableStore = proxy({
    values: {} as any,
    getValues: () => {
        return vtableStore.values;
    },
    setValues: (val: any) => {
        vtableStore.values = val;
    },
})

export const connectionStore = proxy({
    values: {} as any,
    getValues: () => {
        return connectionStore.values;
    },
    setValues: (val: any) => {
        connectionStore.values = val;
    },
})