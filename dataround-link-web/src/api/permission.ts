/**
 * @author: yuehan124@gmail.com
 * @since: 2025/02/19
 **/
import { http, baseAPI } from "../utils";

// ============ Role APIs ============

export const getRoleList = (params: any) => {
  let reqParams = params ? "?" + Object.keys(params).map(key => key + "=" + params[key]).join("&") : "";
  return http.get(baseAPI + "/role/list" + reqParams);
};

export const getAllRoles = () => {
  return http.get(baseAPI + "/role/all");
};

export const saveOrUpdateRole = (data: any) => {
  return http.post(baseAPI + "/role/saveOrUpdate", data);
};

export const deleteRole = (id: string) => {
  return http.delete(baseAPI + "/role/" + id);
};

export const getRoleResources = (roleId: string) => {
  return http.get(baseAPI + "/role/" + roleId + "/resources");
};

export const assignRoleResources = (roleId: string, resourceIds: string[]) => {
  return http.post(baseAPI + "/role/" + roleId + "/resources", resourceIds, {
    headers: { 'Content-Type': 'application/json' }
  });
};

// ============ Resource APIs ============

export const getResourceList = (params: any) => {
  let reqParams = params ? "?" + Object.keys(params).map(key => key + "=" + params[key]).join("&") : "";
  return http.get(baseAPI + "/resource/list" + reqParams);
};

export const getAllResources = () => {
  return http.get(baseAPI + "/resource/all");
};

export const getResourceTree = () => {
  return http.get(baseAPI + "/resource/tree");
};

export const getUserResources = () => {
  return http.get(baseAPI + "/resource/user");
};

export const saveOrUpdateResource = (data: any) => {
  return http.post(baseAPI + "/resource/saveOrUpdate", data);
};

export const deleteResource = (id: string) => {
  return http.delete(baseAPI + "/resource/" + id);
};

// ============ UserRole APIs ============

export const getUserRoleList = (params: any) => {
  let reqParams = params ? "?" + Object.keys(params).map(key => key + "=" + params[key]).join("&") : "";
  return http.get(baseAPI + "/userRole/list" + reqParams);
};

export const getUserRoleIds = (userId: string) => {
  return http.get(baseAPI + "/userRole/" + userId + "/roles");
};

export const assignUserRoles = (userId: string, roleIds: string[]) => {
  return http.post(baseAPI + "/userRole/" + userId + "/roles", roleIds);
};
