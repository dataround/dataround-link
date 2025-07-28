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
 * 
 * @auth: tiandengji
 * @date: 2025/5/15
 **/
import { FC, memo, useState, useCallback, useEffect } from 'react';
import { Menu } from 'antd';
import { useNavigate, useLocation } from 'react-router-dom';
import { useRoutes, IMenu } from '../../router/config';

import './index.less';

// eslint-disable-next-line @typescript-eslint/no-empty-interface
interface IProps { }

const S: FC<IProps> = () => {
  const navigate = useNavigate();
  const { pathname } = useLocation();
  const routes = useRoutes();
  const routeMenu = routes[0]?.children?.[0]?.children;

  const defaultSelectKey = pathname !== '/' ? pathname : '/batch/job';
  const [selectedKey, setSelectedKey] = useState(defaultSelectKey);
  const [openKeys, setOpenKeys] = useState<string[]>(() => {
    // default expand
    return ['/', '/file'];
  });

  useEffect(() => {
    navigate(selectedKey);
  }, [selectedKey, navigate]);

  const handleMenuClick = useCallback((item: any) => {
    const { key } = item;
    setSelectedKey(key);
  }, []);

  const onOpenChange = (keys: string[]) => {
    setOpenKeys(keys);
  };

  const wrapRoute: any = (menuArr: IMenu[] | undefined) => {
    if (Array.isArray(menuArr)) {
      return menuArr.map((item) => item.hidden ? null : ({
        key: item.path || item.name,
        label: item.name,
        icon: item.icon,
        children: wrapRoute(item.children),
      })).filter((menu) => menu);
    }
    return null;
  };

  const menuItems = wrapRoute(routeMenu);

  return (
    <div className="left-menu">
      <Menu
        mode="inline"
        items={menuItems}
        openKeys={openKeys}
        onClick={handleMenuClick}
        selectedKeys={[selectedKey]}
        onOpenChange={onOpenChange}
      />
    </div>
  );
};

const Sidebar = memo(S);

export default Sidebar;
