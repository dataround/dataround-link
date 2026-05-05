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
import { ReactNode } from "react";
import {
  DatabaseOutlined, FolderOutlined, FileOutlined,
  SwapOutlined, SwapRightOutlined,
  RadiusBottomrightOutlined,
  RedoOutlined,
  TeamOutlined,
  SettingOutlined,
  LockOutlined
} from "@ant-design/icons";
import AppLayout from "../layout/index";
import { lazyReactElement } from "../utils/index";
import { useTranslation } from "react-i18next";
import { AuthRouter } from "./authRouter";
import LoginLayout from "../layout/login";
import { hasPermission, useStore } from "../store";

export interface IMenu {
  name?: string;
  path?: string;
  resKey?: string;
  children?: IMenu[];
  icon?: ReactNode;
  element?: ReactNode | null;
  hidden?: boolean;
  type?: string;
}

const fallback = <div>loading</div>;

export const useRoutes = () => {
  const { t } = useTranslation();
  useStore();  // Subscribe to store changes for re-render

  const routes: IMenu[] = [
    {
      element: (
        <AuthRouter>
          <AppLayout />
        </AuthRouter>
      ),
      children: [
        {
          name: "datalink",
          path: "/",
          element: lazyReactElement(() => import("../pages/home"), fallback),
          children: [
            {
              name: t('menu.jobManagement'),
              resKey: 'menu:jobManagement',
              type: 'group',
              children: [
                {
                  path: "/batch/job",
                  name: t('menu.batchJob'),
                  resKey: 'menu:batchJob',  
                  icon: <FolderOutlined />,
                  element: lazyReactElement(
                    () => import("../pages/job"),
                    fallback
                  ),
                },
                {
                  path: "/stream/job",
                  name: t('menu.streamJob'),
                  resKey: 'menu:streamJob',
                  icon: <SwapOutlined />,
                  element: lazyReactElement(
                    () => import("../pages/job"),
                    fallback
                  )
                },
                {
                  path: "/fileSync/job",
                  name: t('menu.fileSync'),
                  resKey: 'menu:fileSync',
                  icon: <FileOutlined />,
                  element: lazyReactElement(
                    () => import("../pages/job"),
                    fallback
                  )
                },
                {
                  path: "/batch/job/create",
                  name: "newJob",
                  hidden: true,
                  element: lazyReactElement(
                    () => import("../pages/job/create"),
                    fallback
                  )
                },
                {
                  path: "/fileSync/create",
                  name: t('menu.fileSync'),
                  hidden: true,
                  icon: <FileOutlined />,
                  element: lazyReactElement(
                    () => import("../pages/fileSync/create"),
                    fallback
                  )
                },
              ]
            },
            {
              name: t('menu.runningInstances'),
              resKey: 'menu:runningInstances',
              type: 'group',
              children: [
                {
                  path: "/batch/instance",
                  name: t('menu.batchInstance'),
                  resKey: 'menu:batchInstance',
                  icon: <RedoOutlined />,
                  element: lazyReactElement(
                    () => import("../pages/instance"),
                    fallback
                  )
                },
                {
                  path: "/stream/instance",
                  name: t('menu.streamInstance'),
                  resKey: 'menu:streamInstance',
                  icon: <SwapRightOutlined />,
                  element: lazyReactElement(
                    () => import("../pages/instance"),
                    fallback
                  )
                },            
                {
                  path: "/fileSync/instance",
                  name: t('menu.fileSyncInstance'),
                  resKey: 'menu:fileSyncInstance',
                  icon: <FileOutlined />,
                  element: lazyReactElement(
                    () => import("../pages/instance"),
                    fallback
                  )
                },
              ]
            },
            {
              name: t('menu.connectionAndTable'),
              resKey: 'menu:connectionAndTable',
              type: 'group',
              children: [
                {
                  path: "/connection",
                  name: t('menu.connectionManagement'),
                  resKey: 'menu:connectionManagement',
                  icon: <DatabaseOutlined />,
                  element: lazyReactElement(
                    () => import("../pages/connection"),
                    fallback
                  )
                },
                {
                  path: "/connection/create",
                  name: t('menu.createConnection'),
                  hidden: true,
                  element: lazyReactElement(
                    () => import("../pages/connection/create"),
                    fallback
                  )
                },
                {
                  path: "/vtable",
                  name: t('menu.virtualTable'),
                  resKey: 'menu:virtualTable',
                  icon: <RadiusBottomrightOutlined />,
                  element: lazyReactElement(
                    () => import("../pages/virtualtable"),
                    fallback
                  )
                },
                {
                  path: "/vtable/create",
                  name: t('menu.createVirtualTable'),
                  hidden: true,
                  element: lazyReactElement(
                    () => import("../pages/virtualtable/create"),
                    fallback
                  )
                },                
              ]
            },
            {
              name: t('menu.systemManagement'),
              resKey: 'menu:systemManagement',
              type: 'group',
              children: [                
                {
                  path: "/settings/user",
                  name: t('menu.userManagement'),
                  resKey: 'menu:userManagement',
                  icon: <TeamOutlined />,
                  element: lazyReactElement(() => import("../pages/user/"), fallback),
                },           
                {
                  path: "/settings/project",
                  name: t('menu.projectManagement'),
                  resKey: 'menu:projectManagement',
                  icon: <FolderOutlined />,
                  element: lazyReactElement(() => import("../pages/project"), fallback),
                },     
                {
                  path: "/settings/permission",
                  name: t('menu.permissionManagement'),
                  resKey: 'menu:permissionManagement',
                  icon: <LockOutlined />,
                  element: lazyReactElement(() => import("../pages/permission"), fallback),
                },
                {
                  path: "/settings/myInfo",
                  name: t('menu.myAccount'),
                  resKey: 'menu:myAccount',
                  icon: <SettingOutlined />,
                  element: lazyReactElement(
                    () => import("../pages/myInfo"),
                    fallback
                  ),
                },
              ]
            }            
          ]
        },
      ],
    },
    {
      element: <LoginLayout />,
      path: "/login",
    }
  ];

  // Filter routes by permission
  return filterMenusByPermission(routes);
};

/**
 * Filter menus by user permission
 * Menu is visible only if the user's resources contain the matching resKey
 */
const filterMenusByPermission = (menus: IMenu[]): IMenu[] => {
  return menus
    .filter(menu => {
      // No resKey means always visible (layout, hidden routes, etc.)
      if (!menu.resKey) {
        return true;
      }
      return hasPermission(menu.resKey);
    })
    .map(menu => {
      if (menu.children) {
        const filteredChildren = filterMenusByPermission(menu.children);
        // Hide parent menu if all children are filtered out
        if (filteredChildren.length === 0 && menu.resKey) {
          return { ...menu, hidden: true };
        }
        return { ...menu, children: filteredChildren };
      }
      return menu;
    });
};