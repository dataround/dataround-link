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
 * @auth: dmx
 * @time: 2023/06/08
 **/
import type { Dispatch } from 'react';
import type { AxiosResponse } from 'axios';

interface IOpt {
  wrapperFun?: IFun<any>;
  defaultData?: any; 
}

export interface IRes<R, P> {
  loading: boolean;
  data: R;
  error: any;
  caller: (params?: P) => Promise<R>;
  setData: Dispatch<R>;
}

export type IFun<T, R = (T & any)> = (data: T, excessParams?: IOptions['excessParams']) => R;

export type TApi = (...args: any []) => Promise<AxiosResponse<TR> | any>;

// useRequestList
export interface IListOptions extends IOpt {
  memoParams?: boolean; 
  filterParams?: string [];
}

export interface IOptions extends IOpt {
  isCutParams?: boolean;
  code?: boolean;
  hasCodeAndData?: boolean;
  isList?: boolean;
  wrapperType?: 'object' | 'array';
  excessParams?: any;
}
