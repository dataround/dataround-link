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

package io.dataround.link.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.dataround.link.common.Result;
import io.dataround.link.common.controller.BaseController;
import io.dataround.link.config.MenuProperties;
import lombok.extern.slf4j.Slf4j;

/**
 * Menu controller
 * 
 * @author yuehan124@gmail.com
 * @since 2025/02/23
 */
@Slf4j
@RestController
@RequestMapping("/api/menu")
public class MenuController extends BaseController {

    @Autowired
    private MenuProperties menuProperties;

    @GetMapping("/items")
    public Result<MenuProperties> getMenuItems() {
        return Result.success(menuProperties);
    }
}
