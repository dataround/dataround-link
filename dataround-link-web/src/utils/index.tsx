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
import { lazy, Suspense, ReactNode } from "react";
import axios from './ajax';

export const baseAPI: string = '/datalink/api';

export const lazyReactElement = (
  loader: () => Promise<any>,
  fallback?: ReactNode,
  appName?: String
) => {
  const Component = lazy(loader);
  return (
    <Suspense fallback={fallback}>
      <Component appName={appName} />
    </Suspense>
  );
};

export const http = axios;
