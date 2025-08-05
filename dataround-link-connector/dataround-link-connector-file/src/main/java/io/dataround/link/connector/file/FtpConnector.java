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

package io.dataround.link.connector.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import io.dataround.link.connector.AbstractFileConnector;
import lombok.extern.slf4j.Slf4j;

/**
 * FTP connector
 * Provide FTP server connection, authentication, and file operation functions.
 *
 * @author yuehan124@gmail.com
 * @date 2025-06-26
 */
@Slf4j
public class FtpConnector extends AbstractFileConnector {

    private FTPClient ftpClient;
    private boolean connected = false;

    // FTP parameter
    private String host;
    private Integer port;
    private String user;
    private String password;
    private Boolean passiveMode;
    private Boolean binaryMode;
    private Integer timeout;
    private String encoding;

    @Override
    public String getName() {
        return "FTP";
    }

    @Override
    public void doInitialize() {
        Map<String, String> config = getParam().getConfig();
        this.host = getParam().getHost();
        this.port = getParam().getPort();
        this.user = getParam().getUser();
        this.password = getParam().getPassword();
        this.passiveMode = Boolean.parseBoolean(config.getOrDefault("passiveMode", "true"));
        this.binaryMode = Boolean.parseBoolean(config.getOrDefault("binaryMode", "true"));
        this.timeout = Integer.parseInt(config.getOrDefault("timeout", "30000"));
        this.encoding = config.getOrDefault("encoding", "UTF-8");
        // Initialize FTP client
        this.ftpClient = new FTPClient();
        // set timeout
        if (timeout != null) {
            ftpClient.setDefaultTimeout(timeout);
            ftpClient.setConnectTimeout(timeout);
        }
        // set encoding
        if (encoding != null) {
            ftpClient.setControlEncoding(encoding);
        }
        // set buffer size
        ftpClient.setBufferSize(8192);
        log.debug("FTP client configuration completed");
    }

    @Override
    public boolean testConnectivity() {
        try {
            connect();
            boolean isValid = ftpClient.isConnected() && ftpClient.isAvailable();
            disconnect();
            return isValid;
        } catch (Exception e) {
            log.error("FTP connector test connection failed", e);
            return false;
        }
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
        List<String> fileList = new ArrayList<>();
        try {
            connect();
            // get files from specified directory
            getFilesRecursive(dir, filePattern, recursive, fileList);
            // return relative path
            if (dir != null && !dir.trim().isEmpty()) {
                ftpClient.changeWorkingDirectory(dir);
            }
            int length = ftpClient.printWorkingDirectory().length() + 1;
            for (int i = 0; i < fileList.size(); i++) {
                fileList.set(i, fileList.get(i).substring(length));
            }
            log.info("FTP connector get file list, dir: {}, file count: {}, pattern: {}, recursive: {}",
                    dir, fileList.size(), filePattern, recursive);
        } catch (IOException e) {
            log.error("FTP connector get file list failed", e);
            throw new RuntimeException("FTP connector get file list failed: " + e.getMessage(), e);
        } finally {
            disconnect();
        }
        return fileList;
    }

    /**
     * Recursively get files from FTP directory
     * 
     * @param dir         target directory to search
     * @param filePattern file pattern (wildcard)
     * @param recursive   whether to search recursively
     * @param fileList    result list to store file names
     * @throws IOException if FTP operation fails
     */
    private void getFilesRecursive(String dir, String filePattern, boolean recursive,
            List<String> fileList) throws IOException {
        // save current working directory
        String originalDir = ftpClient.printWorkingDirectory();
        try {
            // change to target directory
            if (dir != null && !dir.trim().isEmpty()) {
                if (!ftpClient.changeWorkingDirectory(dir)) {
                    log.warn("FTP connector change directory failed, dir: {}", dir);
                    return;
                }
            }
            // get file list in current directory
            FTPFile[] files = ftpClient.listFiles();
            // build file pattern filter
            IOFileFilter fileFilter = null;
            if (filePattern != null && !filePattern.trim().isEmpty()) {
                fileFilter = WildcardFileFilter.builder().setWildcards(filePattern).get();
            }
            String currentWorkingDir = ftpClient.printWorkingDirectory();
            // process files and directories
            for (FTPFile file : files) {
                String fileName = file.getName();
                // skip current and parent directory entries
                if (".".equals(fileName) || "..".equals(fileName)) {
                    continue;
                }
                String fullPath = currentWorkingDir.endsWith("/") ? currentWorkingDir + fileName : currentWorkingDir + "/" + fileName;
                if (file.isFile()) {
                    // check if file matches pattern
                    if (fileFilter == null || fileFilter.accept(null, fileName)) {
                        fileList.add(fullPath);
                    }
                } else if (file.isDirectory() && recursive) {
                    // recursively search subdirectories
                    getFilesRecursive(fullPath, filePattern, recursive, fileList);
                }
            }
        } finally {
            // restore original working directory
            if (originalDir != null) {
                ftpClient.changeWorkingDirectory(originalDir);
            }
        }
    }

    @Override
    public InputStream readFile(String filePath) {
        try {
            connect();
            return ftpClient.retrieveFileStream(filePath);
        } catch (IOException e) {
            log.error("FTP connector read file failed", e);
            throw new RuntimeException("FTP connector read file failed: " + e.getMessage(), e);
        }  
    }

    @Override
    public OutputStream writeFile(String targetPath) {
        try {
            connect();
            return ftpClient.appendFileStream(targetPath);
        } catch (IOException e) {
            log.error("FTP connector write file failed", e);
            throw new RuntimeException("FTP connector write file failed: " + e.getMessage(), e);
        }  
    }

    @Override
    public void close() throws Exception {
        disconnect();
    }

    /**
     * Connect to FTP server
     */
    private void connect() throws IOException {
        if (connected && ftpClient.isConnected()) {
            return;
        }        // connect to server
        ftpClient.connect(host, port);
        // check connection response
        int replyCode = ftpClient.getReplyCode();
        if (!org.apache.commons.net.ftp.FTPReply.isPositiveCompletion(replyCode)) {
            ftpClient.disconnect();
            throw new IOException("FTP server connection failed, response code: " + replyCode);
        }
        // login - support anonymous login
        boolean loginSuccess;
        if (user == null || user.trim().isEmpty() || "anonymous".equalsIgnoreCase(user.trim())) {
            // Anonymous login - use traditional anonymous credentials
            // Password can be any email-like string or even empty for most servers
            String anonymousPassword = (password != null && !password.trim().isEmpty()) ? password
                    : "guest@dataround.io";
            loginSuccess = ftpClient.login("anonymous", anonymousPassword);
            if (!loginSuccess) {
                ftpClient.disconnect();
                throw new IOException("FTP anonymous login failed");
            }
            log.debug("FTP anonymous login successful with password: {}", anonymousPassword);
        } else {
            // Regular login with username and password
            loginSuccess = ftpClient.login(user, password);
            if (!loginSuccess) {
                ftpClient.disconnect();
                throw new IOException("FTP login failed, username or password error, username: " + user);
            }
            log.debug("FTP login successful for user: {}", user);
        }
        // set passive mode
        if (passiveMode) {
            ftpClient.enterLocalPassiveMode();
        }
        // set binary mode
        if (binaryMode) {
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        }
        connected = true;
        log.debug("FTP server connection established, host: {}, port: {}", host, port);
    }

    /**
     * Disconnect from FTP server
     */
    private void disconnect() {
        if (ftpClient != null && ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
                connected = false;
                log.debug("FTP server connection closed");
            } catch (IOException e) {
                log.info("FTP server connection close failed", e);
            }
        }
    }

}
