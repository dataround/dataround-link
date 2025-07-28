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
 * @description: api
 * @author: yuehan124@gmail.com
 * @date: 2026-06-05
 */
import { http, baseAPI } from "../utils";

export const getJobId = (id: string) => {
  return http.get(baseAPI + "/job/" + id);
};

export const getJobList = (params: any) => {
  let reqParams = params ? "?" + Object.keys(params).map(key => key + "=" + params[key]).join("&") : "";
  return http.get(baseAPI + "/job/list" + reqParams);
};

export const saveJob = (params:any) => {
  return http.post(baseAPI + "/job/saveOrUpdate", params);
};

export const deleteJob = (id: string) => {
  return http.delete(baseAPI + "/job/" + id);
}

export const executeJob = (id: string) => {
  return http.post(baseAPI + "/job/execute/" +id);
};