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
import axios, { AxiosRequestConfig, Canceler } from 'axios';

export default class CancelToken {
  // Declare a Map to store request identifiers and cancel functions
  private static pending: Map<string, Canceler> = new Map()

  // Whitelist, add API names here
  private static whiteRequest: string[] = []

  /**
   * Get URL in this format
   * @param {AxiosRequestConfig} config
   * @returns
   */
  private static getUrl(config: AxiosRequestConfig) {
    return [config.method, config.url].join('&');
  }

  /**
   * Add request
   * @param {AxiosRequestConfig} config
   */
  public static addPending(config: AxiosRequestConfig) {
    const url = this.getUrl(config);
    config.cancelToken = new axios.CancelToken((cancel) => {
      if (!this.pending.has(url)) { // If the current request doesn't exist in pending, add it
        this.pending.set(url, cancel);
      }
    });
  }

  public static hasPending(config: AxiosRequestConfig) {
    return this.getUrl(config);
  }

  /**
   * Remove request
   * @param {AxiosRequestConfig} config
   */
  public static removePending(config: AxiosRequestConfig) {
    const url = this.getUrl(config);
    const method = url.split('&')[1];
    // eslint-disable-next-line max-len
    // If the current request exists in pending, cancel it and remove
    if (this.pending.has(url) && !this.whiteRequest.includes(method)) { 
      const cancel = this.pending.get(url);
      // eslint-disable-next-line
      // @ts-ignore
      cancel?.({ url, reason: 'cancel request' });
      this.pending.delete(url);
    }
  }

  /**
   * Clear all pending requests (called during route navigation)
   */
  public static clearPending() {
    // eslint-disable-next-line
    // @ts-ignore
    for (const [url, cancel] of this.pending) {
      // eslint-disable-next-line
      // @ts-ignore
      cancel({ url, reason: 'cancel request' });
    }
    this.pending.clear();
  }
}
