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
  UserOutlined,
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
  children?: IMenu[];
  icon?: ReactNode;
  element?: ReactNode | null;
  hidden?: boolean;
  type?: string;
  permissionKey?: string;  // Resource key for permission check
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
            // Task Management Category
            {
              name: t('menu.taskManagement'),
              type: 'group',
              children: [
                {
                  path: "/batch/job",
                  name: t('menu.batchJob'),
                  icon: <FolderOutlined />,
                  element: lazyReactElement(
                    () => import("../pages/job"),
                    fallback
                  ),
                },
                {
                  path: "/stream/job",
                  name: t('menu.streamJob'),
                  icon: <SwapOutlined />,
                  element: lazyReactElement(
                    () => import("../pages/job"),
                    fallback
                  )
                },
                {
                  path: "/fileSync/job",
                  name: t('menu.fileSync'),
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
            // Running Instances Category
            {
              name: t('menu.runningInstances'),
              type: 'group',
              children: [
                {
                  path: "/batch/instance",
                  name: t('menu.batchInstance'),
                  icon: <RedoOutlined />,
                  element: lazyReactElement(
                    () => import("../pages/instance"),
                    fallback
                  )
                },
                {
                  path: "/stream/instance",
                  name: t('menu.streamInstance'),
                  icon: <SwapRightOutlined />,
                  element: lazyReactElement(
                    () => import("../pages/instance"),
                    fallback
                  )
                },            
                {
                  path: "/fileSync/instance",
                  name: t('menu.fileSyncInstance'),
                  icon: <FileOutlined />,
                  element: lazyReactElement(
                    () => import("../pages/instance"),
                    fallback
                  )
                },
              ]
            },
            // System Management Category
            {
              name: t('menu.systemManagement'),
              type: 'group',
              children: [
                {
                  path: "/connection",
                  name: t('menu.connectionManagement'),
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
                {
                  path: "/settings/project",
                  name: t('menu.projectManagement'),
                  icon: <FolderOutlined />,
                  element: lazyReactElement(() => import("../pages/project"), fallback),
                  permissionKey: 'menu:project',
                },
                {
                  path: "/settings/projectMember",
                  name: t('menu.projectMember'),
                  icon: <UserOutlined />,
                  element: lazyReactElement(() => import("../pages/projectMember"), fallback),
                  permissionKey: 'menu:projectMember',
                },
                {
                  path: "/settings/user",
                  name: t('menu.userManagement'),
                  icon: <TeamOutlined />,
                  element: lazyReactElement(() => import("../pages/user/"), fallback),
                  permissionKey: 'menu:user',
                },                
                {
                  path: "/settings/permission",
                  name: t('menu.permissionManagement'),
                  icon: <LockOutlined />,
                  element: lazyReactElement(() => import("../pages/permission"), fallback),
                  permissionKey: 'menu:permission',
                },
                {
                  path: "/settings/myInfo",
                  name: t('menu.myAccount'),
                  icon: <SettingOutlined />,
                  element: lazyReactElement(
                    () => import("../pages/myInfo"),
                    fallback
                  ),
                  // No permission key - always visible
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
 */
const filterMenusByPermission = (menus: IMenu[]): IMenu[] => {
  return menus
    .filter(menu => {
      // No permission key means always visible
      if (!menu.permissionKey) {
        return true;
      }
      return hasPermission(menu.permissionKey);
    })
    .map(menu => {
      if (menu.children) {
        const filteredChildren = filterMenusByPermission(menu.children);
        // Hide parent menu if all children are filtered out
        if (filteredChildren.length === 0 && menu.permissionKey) {
          return { ...menu, hidden: true };
        }
        return { ...menu, children: filteredChildren };
      }
      return menu;
    });
};