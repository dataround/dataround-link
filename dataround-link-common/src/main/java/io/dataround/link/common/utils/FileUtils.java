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

package io.dataround.link.common.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.extern.slf4j.Slf4j;

/**
 * File utility class
 * 
 * @author yuehan124@gmail.com
 * @date 2025-11-05
 */
@Slf4j
public class FileUtils {
    /**
     * Create a temporary file with the given content
     */
    public static Path createTempFile(String prefix, String suffix, String content) {
        try {
            // Create temporary directory if it doesn't exist
            Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "dataround");
            if (!Files.exists(tempDir)) {
                Files.createDirectories(tempDir);
            }
            // Create temporary file
            Path tempFile = Files.createTempFile(tempDir, prefix + "-", suffix);
            // Write content to file
            org.apache.commons.io.FileUtils.writeStringToFile(tempFile.toFile(), content, "UTF-8");
            // Delete file on JVM exit
            tempFile.toFile().deleteOnExit();
            return tempFile;
        } catch (IOException e) {
            log.error("Failed to create temporary file", e);
            throw new RuntimeException(e);
        }
    }
}
