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

export const getInstanceById = (id: string) => {
  return http.get(baseAPI + "/instance/" + id);
};

export const getInstanceList = (params: any) => {
  let reqParams = params ? "?" + Object.keys(params).map(key => key + "=" + params[key]).join("&") : "";
  return http.post(baseAPI + "/instance/list", params);
};

export const deleteInstance = (id: string) => {
  return http.delete(baseAPI + "/instance/" + id);
}

export const stopInstance = (id: string) => {
  return http.post(baseAPI + "/instance/stop/" + id);
}
export const restoreInstance = (id: string) => {
  return http.post(baseAPI + "/instance/restore/" + id);
}