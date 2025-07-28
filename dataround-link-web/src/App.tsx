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

import {
  FC,
  memo,
} from 'react';
import { ConfigProvider } from "antd";
import Router from './router/index';

interface IProps {
}

const A: FC<IProps> = () => {
  return (
    <div className="app">
      <ConfigProvider
        theme={{
          token: {
            colorPrimary: "#617ef2", 
            borderRadius: 6,
            colorError: "#FF5C5E", 
            colorSuccess: "#43ba9a", 
            colorWarning: "#FA9746", 
            colorText: "#323233"
          },
        }}
      >
        <Router />
      </ConfigProvider>
    </div>
  );
};

const App = memo(A);

export default App;

