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
/**
 * Resource type definition
 */
export interface Resource {
  id: string;
  pid: string;
  name: string;
  resKey: string;
  description: string;
  createTime: string;
}

/**
 * Global state store
 */
export const store = proxy<{
  userInfo: any;
  resources: Resource[];
}>({
  userInfo: null,
  resources: [],
});

/**
 * Set user info
 */
export const setUserInfo = (user: any) => {
  store.userInfo = user;
};

/**
 * Set user resources (call after login)
 */
export const setResources = (resources: Resource[]) => {
  store.resources = resources;
};

/**
 * Clear all state (call on logout)
 */
export const clearState = () => {
  store.userInfo = null;
  store.resources = [];
};

/**
 * Check if user has permission for a UI resource
 * @param resKey - Resource key (e.g., 'menu:user', 'btn:user:add')
 * @returns true if user has permission
 */
export const hasPermission = (resKey: string): boolean => {
  return store.resources.some(r => r.resKey === resKey);
};

/**
 * Check if user has permission for multiple UI resources (any one)
 * @param resKeys - Array of resource keys
 * @returns true if user has permission for any of the resources
 */
export const hasAnyPermission = (resKeys: string[]): boolean => {
  return resKeys.some(key => hasPermission(key));
};

/**
 * Check if user has permission for all UI resources
 * @param resKeys - Array of resource keys
 * @returns true if user has permission for all resources
 */
export const hasAllPermissions = (resKeys: string[]): boolean => {
  return resKeys.every(key => hasPermission(key));
};

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

/**
 * Hook to use store snapshot
 */
export const useStore = () => useSnapshot(store);
