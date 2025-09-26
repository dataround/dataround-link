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

 package io.dataround.link.config;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * Message utils
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
@Component
public class MessageUtils {

    private static MessageSource messageSource;

    public MessageUtils(MessageSource messageSource) {
        MessageUtils.messageSource = messageSource;
    }

    /**
     * Get internationalized message
     *
     * @param code Message code
     * @return Internationalized message
     */
    public static String getMessage(String code) {
        return getMessage(code, null);
    }

    /**
     * Get internationalized message
     *
     * @param code Message code
     * @param args Parameters
     * @return Internationalized message
     */
    public static String getMessage(String code, Object[] args) {
        return messageSource.getMessage(code, args, code, LocaleContextHolder.getLocale());
    }
}