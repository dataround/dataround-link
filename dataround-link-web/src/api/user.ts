/**
 * @author: yuehan124@gmail.com
 * @since: 2025/09/01
 **/
import { http, baseAPI } from "../utils";

export const getUserById = () => {
  return http.get(baseAPI + "/user/info");
};

export const getUserList = (params: any) => {
  let reqParams = params ? "?" + Object.keys(params).map(key => key + "=" + params[key]).join("&") : "";
  return http.get(baseAPI + "/user/list" + reqParams);
};

export const saveOrUpdateUser = (data: any) => {
  return http.post(baseAPI + "/user/saveOrUpdate", data);
};

export const getProjects = (params: any) => {
  let reqParams = params ? "?" + Object.keys(params).map(key => key + "=" + params[key]).join("&") : "";
  return http.get(baseAPI + "/project/list" + reqParams);
};

export const getMyProjects = () => {
  return http.get(baseAPI + "/project/mine");
};

export const deleteProject = (projectId: string) => {
  return http.delete(baseAPI + "/project/" + projectId);
};

export const saveOrUpdateProject = (data: any) => {
  return http.post(baseAPI + "/project/saveOrUpdate", data);
};

export const updateSelected = (projectId: string) => {
  return http.post(baseAPI + "/project/selected/" + projectId);
};

export const getProjectMembers = (projectId?: string) => {
  let reqParams = projectId ? "?projectId=" + projectId : "";
  return http.get(baseAPI + "/project/member/list" + reqParams);
};

export const saveProjectMember = (data: any) => {
  return http.post(baseAPI + "/project/member/save", data);
};

export const deleteProjectMember = (id: string) => {
  return http.delete(baseAPI + "/project/member/" + id);
};

export const updatePasswd = (data: { oldPasswd: string; newPasswd: string }) => {
  return http.post(baseAPI + "/user/updatePasswd", data);
};