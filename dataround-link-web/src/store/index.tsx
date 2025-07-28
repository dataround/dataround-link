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
import { proxy, useSnapshot } from "valtio"
import dayjs, { Dayjs } from "dayjs";
import { RecordType } from "../pages/job/create/step-source"

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

export const jobStore = proxy({
    // state for job save step
    id: '',
    name: '',
    description: '',
    jobType: JOB_TYPE_BATCH,
    scheduleType: SCHEDULE_TYPE_RUNNOW,
    startTime: '',
    endTime: '',
    cron: '',
    // state for job source step
    sourceConnector: '',
    sourceConnId: '',
    sourceDbName: '',
    targetConnector: '',
    targetConnId: '',
    targetDbName: '',
    tableMapping: [] as RecordType[],

    // action for job save step
    setId: (id: string) => {
        jobStore.id = id
    },
    setName: (name: string) => {
        jobStore.name = name
    },
    setDescription: (description: string) => {
        jobStore.description = description
    },
    setJobType: (jobType: number) => {
        jobStore.jobType = jobType
    },
    setScheduleType: (scheduleType: number) => {
        jobStore.scheduleType = scheduleType
    },
    setCron: (cron: string) => {
        jobStore.cron = cron
    },
    setStartTime: (startTime: string) => {
        jobStore.startTime = startTime
    },  
    setEndTime: (endTime: string) => {
        jobStore.endTime = endTime
    },
    // action for source step
    setSourceConnector: (connector: any) => {
        jobStore.sourceConnector = connector
    },
    setSourceConnId: (connId: string) => {
        jobStore.sourceConnId = connId
    },
    setSourceDbName: (dbName: string) => {
        jobStore.sourceDbName = dbName
    },
    setTargetConnector: (connector: any) => {
        jobStore.targetConnector = connector
    },
    setTargetConnId: (connId: string) => {
        jobStore.targetConnId = connId
    },
    setTargetDbName: (dbName: string) => {
        jobStore.targetDbName = dbName
    },

    setTableMapping: (mapping: RecordType[]) => {
        jobStore.tableMapping = mapping
    },
    cleanAll: () => {
        jobStore.id = '',
        jobStore.name = '',
        jobStore.description = '',
        jobStore.jobType = JOB_TYPE_BATCH,
        jobStore.scheduleType = SCHEDULE_TYPE_RUNNOW,
        jobStore.startTime = '',
        jobStore.endTime = '',
        jobStore.cron = '',
        jobStore.sourceConnector = '',
        jobStore.sourceConnId = '',
        jobStore.sourceDbName= '',
        jobStore.targetConnector = '',
        jobStore.targetConnId = '',
        jobStore.targetDbName = '',
        jobStore.tableMapping = []        
    }
})

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