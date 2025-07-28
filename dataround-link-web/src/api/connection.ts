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

export const getConnectionById = (id: string) => {
  return http.get(baseAPI + "/connection/" + id);
};

export const testConnection = (conn: any) => {
  return http.post(baseAPI + "/connection/test", conn);
};

export const deleteConnection = (id: string) => {
  return http.delete(baseAPI + "/connection/" + id);
};

export const getConnections = (params: any) => {
  let reqParams = params ? "?" + Object.keys(params).map(key => key + "=" + params[key]).join("&") : "";
  return http.get(baseAPI + "/connection/list" + reqParams);
};

export const saveOrUpdateConnection = (conn: any) => {
  return http.post(baseAPI + "/connection/saveOrUpdate", conn);
}

export const getDatabaseList = (connId: string) => {
  return http.get(baseAPI + "/connection/" + connId + "/dbs");
}

export const getTableList = (params: any) => {
  return http.get(baseAPI + "/connection/" + params.connId + "/" + params.dbName + "/tables");
}

export const getTableColumns = (params: any) => {
  return http.get(baseAPI + "/connection/" + params.connId + "/" + params.dbName + "/" + params.tableName + "/columns");
}

export const getAllTableColumns = (params: any) => {
  let subPath1 =  params.sourceConnId + "/" + params.sourceDbName + "/" + params.sourceTable;
  let subPath2 = params.targetConnId + "/" + params.targetDbName + "/" + params.targetTable;
  return http.get(baseAPI + "/connection/" + subPath1 + "/" + subPath2 + "?matchMethod=" + params.matchMethod);
}

export const getConnector = (params: any) => {
  let reqParams = params ? "?" + Object.keys(params).map(key => key + "=" + params[key]).join("&") : "";
  return http.get(baseAPI + "/connector/" + reqParams);
}


export const formatConnector = (res: any) => {
  interface TreeNode {
    value: string;
    label?: string;
    title?: string;
    selectable: boolean;
    children?: TreeNode[];
  }
  
  const treeDataSource: TreeNode[] = [];
  
  // Iterate through all categories in the response
  Object.entries(res).forEach(([category, items]) => {
    if (Array.isArray(items) && items.length > 0) {
      const children: TreeNode[] = items.map(item => ({
        value: item,
        label: item,
        selectable: true
      }));
      
      treeDataSource.push({
        value: category,
        title: category,
        selectable: false,
        children
      });
    }
  });
  
  return treeDataSource;
};