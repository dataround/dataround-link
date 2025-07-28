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
import { DependencyList, useCallback, useState } from 'react';
import { IOptions, IRes, TApi } from './typings';

function useRequest<P extends Record<any, any> = any, R = any>(
  api: TApi | TApi [],
  options: IOptions = { isCutParams: false },
  paramsArray?: any [],
  deps?: DependencyList,
):IRes<R, P> {
  const {
    code,
    wrapperFun,
    isCutParams,
    defaultData,
    excessParams,
    hasCodeAndData = false,
    wrapperType = 'array',
  } = options;

  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<R>(defaultData);
  const [error, setError] = useState<unknown>();

  const caller = useCallback(async (params?: P) => {
    setLoading(true);
    let result;
    try {
      if (Array.isArray(api)) {
        const promises = api.map((a, idx) => a(paramsArray?.[idx] ?? paramsArray?.[idx]));
        const res = await Promise.all(promises);
        let realRes: TR = res;
        if (wrapperType && wrapperType === 'object') {
          let varObj: TR = {};
          const getName = function () {
            // @ts-ignore
            return this.name || this.toString().match(/function\s*([^(]*)\(/)[1];
          };
          api.forEach((a, idx) => {
            varObj = { ...varObj, [getName.call(a)]: res[idx].data.records || res[idx].data };
          });
          realRes = varObj;
        } else if (wrapperType && wrapperType === 'array') {
          realRes = res.map((a) => a.data.records || a.data);
        }

        result = {
          code: 200,
          data: realRes,
        };
      } else {
        result = isCutParams
          ? await api(...Object.values(params || {}))
          : await api(params);
      }

      const resCode = result.code;      
      if (hasCodeAndData) {
        const resultData = {
          resCode,
          data: result.data,
        };
        setData(result.data);
        return resultData;
      }
      if (code) {
        setData(result.data);
        return resCode;
      }
      if (wrapperFun && result.data) {        
        const wrapperResult = wrapperFun(result.data, excessParams);
        setData(wrapperResult);
        return wrapperResult;
      }
      if (resCode === 200) {
        setData(result.data);
      }

      return result.data || result;
    } catch (err) {
      setError(err);
    } finally {
      setLoading(false);
    }
  }, deps ?? []);

  return {
    data,
    error,
    caller,
    setData,
    loading,
  };
}

export default useRequest;
