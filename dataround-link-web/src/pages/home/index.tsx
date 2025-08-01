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
 * @author: yuehan124@gmail.com
 * @date: 2026-06-05
 */
import {
  FC,
  memo,
} from 'react';
import { Outlet } from 'react-router-dom';
import Sidebar from '../../layout/sidbar/index';
import ContentWrap from '../../components/content-warp/index';

import './index.less';

// eslint-disable-next-line @typescript-eslint/no-empty-interface
interface IProps {
}

const H: FC<IProps> = () => {

  return (
    <div className="home">
      <Sidebar />
      <div className="content">
        <ContentWrap>
          <Outlet />
        </ContentWrap>
      </div>
    </div>
  );
};

const Home = memo(H);

export default Home;
