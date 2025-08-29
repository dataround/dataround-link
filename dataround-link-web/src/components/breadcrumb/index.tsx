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
 * @description: breadcrumb component
 * 
 * @author: yuehan124@gmail.com
 * @since: 2025/8/29
 **/
import { FC, memo, useMemo } from 'react';
import { Breadcrumb } from 'antd';
import { HomeOutlined } from '@ant-design/icons';
import { useLocation, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useRoutes, IMenu } from '../../router/config';

import './index.less';

interface IProps {}

const B: FC<IProps> = () => {
  const { pathname } = useLocation();
  const navigate = useNavigate();
  const { t } = useTranslation();
  const routes = useRoutes();

  const breadcrumbItems = useMemo(() => {
    const items: { title: string | React.ReactNode; path?: string }[] = [];
    
    // add home
    items.push({
      title: (
        <span>
          <HomeOutlined style={{ marginRight: 4 }} />
          {t('menu.dataIntegration')}
        </span>
      ),
      path: '/batch/job'
    });

    // generate breadcrumb by current path
    const pathSegments = pathname.split('/').filter(Boolean);
    
    if (pathSegments.length === 0) {
      return items;
    }

    // find menu item by current path
    const findMenuByPath = (menus: IMenu[] | undefined, path: string): IMenu | null => {
      if (!menus) return null;
      
      for (const menu of menus) {
        if (menu.path === path) {
          return menu;
        }
        if (menu.children) {
          const found = findMenuByPath(menu.children, path);
          if (found) return found;
        }
      }
      return null;
    };

    const routeMenu = routes[0]?.children?.[0]?.children;
    
    // build path
    let currentPath = '';
    for (let i = 0; i < pathSegments.length; i++) {
      currentPath += `/${pathSegments[i]}`;
      const menu = findMenuByPath(routeMenu, currentPath);
      
      if (menu && !menu.hidden) {
        items.push({
          title: menu.name || pathSegments[i],
          path: currentPath
        });
      }
    }

    return items;
  }, [pathname, routes, t]);

  const handleBreadcrumbClick = (path?: string) => {
    if (path) {
      navigate(path);
    }
  };

  return (
    <div className="breadcrumb-container">
      <Breadcrumb
        items={breadcrumbItems.map((item, index) => {
          const isLastItem = index === breadcrumbItems.length - 1;
          const isCurrentPath = item.path === pathname;
          // If the last item and the path matches, it cannot be clicked
          if (isLastItem && isCurrentPath) {
            return {
              title: <span className="breadcrumb-current">{item.title}</span>
            };
          }
          // Other cases can be clicked
          return {
            title: (
              <span 
                className={isLastItem ? "breadcrumb-current breadcrumb-clickable" : "breadcrumb-link"}
                onClick={() => handleBreadcrumbClick(item.path)}
              >
                {item.title}
              </span>
            )
          };
        })}
      />
    </div>
  );
};

const BreadcrumbComponent = memo(B);

export default BreadcrumbComponent; 