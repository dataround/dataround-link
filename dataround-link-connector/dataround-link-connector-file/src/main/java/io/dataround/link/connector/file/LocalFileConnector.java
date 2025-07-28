/**
 * Copyright (c) 2025, yuehan124@gmail.com
 * All rights reserved.
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

package io.dataround.link.connector.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import io.dataround.link.connector.AbstractFileConnector;

/**
 * Local file connector
 * Provide local file connection and file operation functions.
 *
 * @author yuehan124@gmail.com
 * @date 2025-07-28
 */
public class LocalFileConnector extends AbstractFileConnector {

    @Override
    public String getName() {
        return "LocalFile";
    }

    @Override
    public void doInitialize() {
    }

    @Override
    public boolean testConnectivity() {
        return true;
    }

    @Override
    public List<String> getFiles(String dir) {
        return getFiles(dir, null);
    }

    @Override
    public List<String> getFiles(String dir, String filePattern) {
        return getFiles(dir, filePattern, false);
    }

    @Override
    public List<String> getFiles(String dir, String filePattern, boolean recursive) {
        try {
            File directory = new File(dir);
            if (!directory.exists() || !directory.isDirectory()) {
                throw new IllegalArgumentException("Directory does not exist or is not a directory: " + dir);
            }
            Collection<File> files;
            if (filePattern != null && !filePattern.isEmpty()) {
                // use wildcard filter to match file pattern
                IOFileFilter fileFilter = WildcardFileFilter.builder().setWildcards(filePattern).get();
                IOFileFilter dirFilter = recursive ? TrueFileFilter.INSTANCE : null;
                files = FileUtils.listFiles(directory, fileFilter, dirFilter);
            } else {
                // get all files
                files = FileUtils.listFiles(directory, null, recursive);
            }
            return files.stream().map(File::getAbsolutePath).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to list files in directory: " + dir, e);
        }
    }

    @Override
    public InputStream readFile(String filePath) {
        try {
            return new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found: " + filePath, e);
        }
    }

    @Override
    public OutputStream writeFile(String targetPath) {
        try {
            return new FileOutputStream(targetPath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found: " + targetPath, e);
        }
    }

    @Override
    public void close() throws Exception {
        // do nothing
    }

}
