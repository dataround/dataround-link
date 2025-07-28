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

package io.dataround.link.connector;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import io.dataround.link.common.connector.Param;

/**
 * Connector interface
 *
 * @author yuehan124@gmail.com
 * @date 2025-06-09
 */
public interface FileConnector extends Connector {

    /**
     * Initialize connector with properties
     */
    void initialize(Param param);

    /**
     * Get files
     */
    List<String> getFiles(String dir);

    /**
     * Get files with pattern
     */
    List<String> getFiles(String dir, String filePattern);

    /**
     * Get files with pattern and recursive
     */
    List<String> getFiles(String dir, String filePattern, boolean recursive);

    /**
     * Read file
     */
    InputStream readFile(String filePath);

    /**
     * Write file
     */
    OutputStream writeFile(String targetPath);

}
