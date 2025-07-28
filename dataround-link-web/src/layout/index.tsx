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
import { FC, memo } from 'react';
import { Layout } from 'antd';
import { Outlet } from 'react-router-dom';
import Header from './header/index';

import './index.less';

interface IProps {}

const L: FC<IProps> = () => {

  return (
    <Layout className="layout">
      <Header />
      <div style={{ flex: 1 }}>
        <Outlet />
      </div>
    </Layout>
  );
};

const AppLayout = memo(L);

export default AppLayout;
