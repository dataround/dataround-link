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

export const getVirtualTableById = (id: string) => {
  return http.get(baseAPI + "/vtable/" + id);
};

export const getVirtualTableList = () => {
  return http.get(baseAPI + "/vtable/list");
};

export const saveOrUpdateVirtualTable = (vtable: any) => {
  return http.post(baseAPI + "/vtable/saveOrUpdate", vtable);
}

export const deleteVirtualTable = (id: string) => {
  return http.delete(baseAPI + "/vtable/" + id);
}