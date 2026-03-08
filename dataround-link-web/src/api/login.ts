/**
 * @author: yuehan124@gmail.com
 * @since: 2025/09/01
 **/

import { http, baseAPI } from '../utils';

export const doLogin = (k8s: any) => {
  return http.post(baseAPI + '/login', k8s);
}

export const doLogout = () => {
  return http.get(baseAPI + '/logout');
}
