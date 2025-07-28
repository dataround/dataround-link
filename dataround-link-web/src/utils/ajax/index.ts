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
import axios from 'axios';
import asyncErrorHandle from './asyncErrorHandle';
import CancelToken from './CancelToken';
import { message } from 'antd';

// http request interceptors
axios.interceptors.request.use(
  (config: any) => {
    CancelToken.addPending(config);
    if (config.method.toUpperCase() === 'GET') {
      config.url += (config.url.indexOf('?') > 0 ? '&' : '?') + 'clearCache=' + new Date().valueOf();
    }
    if (config.method.toUpperCase() === 'POST') {
      if (!config.headers['Content-Type']) {
        config.headers['Content-Type'] = 'application/json';
      }
    }
    config.headers['X-Requested-With'] = 'xmlhttprequest';
    return config;
  },
  (error: any) => Promise.reject(error),
);

// http response interceptors
axios.interceptors.response.use(
  (data: any) => {
    CancelToken.removePending(data.config);
    if (
      data.headers['content-type']
      && data.headers['content-type'].toLowerCase().includes('application/json')
    ) {
      const { code, msg } = data.data;
      
      // Handle message display in interceptor
      if (code === 200 && msg) {
        message.success(msg);
      } else if (code !== 200 && msg) {
        message.error(msg);
      }
      
      // Return the original response structure
      return data.data;
    }
    if (
      data.config.responseType === 'arraybuffer'
      && data.headers['content-type'].indexOf('application/vnd.openxml') > -1
    ) {
      // Response is a binary stream judgment
      return data;
    }
    return Promise.reject('Please login first');
  },
  (err: any) => {
    // If the request is canceled, no error is reported.
    if (err?.message?.reason === 'cancel request') {
      return;
    }
    if (err && err.response) {
      switch (err.response.status) {
        case 400:
          err.message = 'request error';
          break;
        case 401:
          // If the ajax request with invalid cookie, backend service will return 401 
          sessionStorage.removeItem('info');
          if (window.parent) {
            window.parent.location.reload();
          } else {
            window.location.reload();
          }
          break;
        case 408:
          err.message = 'request timeout';
          break;
        case 500:
          err.message = 'server error';
          break;
        default:
      }
    }
    asyncErrorHandle(err.message);
    return Promise.reject(err);
  },
);
export default axios;
