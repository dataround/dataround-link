/**
 * @author: yuehan124@gmail.com
 * @since: 2026/02/23
 **/

import { http, baseAPI } from '../utils';

export interface MenuItem {
  key: string;
  labelKey: string;
  url?: string;
  external?: boolean;
}

export interface MenuData {
  items: MenuItem[];
}

export const getMenuItems = (): Promise<{ data: MenuData }> => {
  return http.get(`${baseAPI}/menu/items`);
};
