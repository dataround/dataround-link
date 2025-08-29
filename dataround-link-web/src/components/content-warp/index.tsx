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
import {
  FC,
  memo,
  ReactNode,
} from 'react';
import BreadcrumbComponent from '../breadcrumb/index';

import './index.less';

interface IProps {
  children: ReactNode;
}

const C: FC<IProps> = (props) => {
  return (
    <div className="content-wrap">
      <BreadcrumbComponent />
      <div className="content-body">
        { props.children }
      </div>
    </div>
  );
};

const ContentWrap = memo(C);

export default ContentWrap;
