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
import { notification } from 'antd';
import { Subject } from 'rxjs';

import {
  groupBy, distinctUntilChanged, map, mergeMap,
} from 'rxjs/operators';

notification.config({
  placement: 'topRight',
  top: 60,
  duration: 3,
});

const resSubject = new Subject();

resSubject
  .pipe(
    groupBy((data: any) => data.description),
    mergeMap((group$) => group$.pipe(
      map((data) => {
        data.timer = new Date().getTime();
        return data;
      }),
      distinctUntilChanged(
        (prev, curr) => (new Date()).getTime() - prev.timer < 3000
          && prev.description === curr.description,
      ),
    )),
  )
  .subscribe((data) => {
    const { message, description, key } = data;
    if (description === 'Network Error') {
      const { href } = window.location;
      return;
    }
    if (description !== 'need login') {
      notification.warning({
        message,
        description,
        key,
      });
    }
  });

const asyncErrorHandle = function (msg: string) {
  const message = 'Error occurred';
  const description = msg || 'Unknown exception';
  const key = `open${Date.now()}`;
  resSubject.next({ message, description, key });
};

export default asyncErrorHandle;
