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

package io.dataround.link.common;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Generic result wrapper class for API responses.
 * This class provides a standardized way to wrap API responses with status code,
 * data and message.
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> {

    public static final int STATUS_OK = 200;
    public static final int STATUS_ERR = 500;
    protected int code = STATUS_OK;
    protected T data;
    protected String msg;

    public Result() {
    }

    private Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private Result(int code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public static <T> Result<T> success() {
        return new Result<>(STATUS_OK, null, null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(STATUS_OK, data, null);
    }

    public static <T> Result<T> success(String msg) {
        return new Result<>(STATUS_OK, msg);
    }

    public static <T> Result<T> error() {
        return new Result<>(STATUS_ERR, null, null);
    }

    public static <T> Result<T> error(String msg) {
        return new Result<>(STATUS_ERR, msg);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
