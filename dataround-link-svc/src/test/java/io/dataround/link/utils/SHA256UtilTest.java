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

package io.dataround.link.utils;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for SHA256Util.
 * Tests SHA-256 hashing functionality with salt.
 *
 * @author yuehan124@gmail.com
 * @date 2026-04-25
 */
@Slf4j
public class SHA256UtilTest {

    @Test
    public void testGetSHA256WithNormalInput() {
        String input = "dataround.io";
        String hash = SHA256Util.getSHA256(input);
        
        assertNotNull(hash);
        log.info("SHA-256 hash of '{}': {}", input, hash);
    }
}
