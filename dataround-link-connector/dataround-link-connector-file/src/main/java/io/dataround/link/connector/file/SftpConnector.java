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

import com.jcraft.jsch.*;

import io.dataround.link.common.utils.ConnectorNameConstants;
import io.dataround.link.connector.AbstractFileConnector;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

/**
 * SFTP connector
 * Provide SFTP server connection, authentication, and file operation functions.
 *
 * @author yuehan124@gmail.com
 * @date 2025-07-26
 */
@Slf4j
public class SftpConnector extends AbstractFileConnector {

    private Session session;
    private ChannelSftp sftpChannel;
    private boolean connected = false;

    // SFTP connection parameters
    private String host;
    private Integer port;
    private String user;
    private String password;
    private String privateKeyPath;
    private String privateKeyPassphrase;
    private boolean strictHostKeyChecking;
    private Integer timeout;
    private String encoding;


    @Override
    public String getName() {
        return ConnectorNameConstants.SFTP;
    }

    @Override
    public void doInitialize() {
        // Initialize SFTP connector parameter
        Map<String, String> config = getParam().getConfig();
        this.host = getParam().getHost();
        this.port = getParam().getPort();
        this.user = getParam().getUser();
        this.password = getParam().getPassword();
        this.privateKeyPath = config.get("privateKeyPath");
        this.privateKeyPassphrase = config.get("privateKeyPassphrase");
        this.strictHostKeyChecking = Boolean.parseBoolean(config.getOrDefault("strictHostKeyChecking", "false"));
        this.encoding = config.getOrDefault("encoding", "UTF-8");
        // Handle potential null values for timeout, config map contains null values
        String strTimeout = config.get("timeout");
        this.timeout = strTimeout == null ? 30000 : Integer.parseInt(strTimeout);
    }

    @Override
    public boolean testConnectivity() {
        try {
            connect();
            boolean isValid = session != null && session.isConnected() &&
                    sftpChannel != null && sftpChannel.isConnected();
            disconnect();
            return isValid;
        } catch (Exception e) {
            log.error("SFTP connector test connection failed", e);
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
            getFilesRecursive(dir, filePattern, recursive, fileList);
            // return relative path
            if (dir != null && !dir.trim().isEmpty()) {
                sftpChannel.cd(dir);
            }
            int length = sftpChannel.pwd().length() + 1;
            for (int i = 0; i < fileList.size(); i++) {
                fileList.set(i, fileList.get(i).substring(length));
            }
            log.info("SFTP connector get file list, dir: {}, file count: {}, pattern: {}, recursive: {}",
                    dir, fileList.size(), filePattern, recursive);
        } catch (Exception e) {
            log.error("SFTP connector get file list failed", e);
            throw new RuntimeException("SFTP connector get file list failed: " + e.getMessage(), e);
        } finally {
            disconnect();
        }
        return fileList;
    }

    @Override
    public InputStream readFile(String filePath) {
        try {
            connect();
            return sftpChannel.get(filePath);
        } catch (Exception e) {
            log.error("SFTP connector read file failed", e);
            throw new RuntimeException("SFTP connector read file failed: " + e.getMessage(), e);
        }  
    }

    @Override
    public OutputStream writeFile(String targetPath) {
        try {
            connect();
            return sftpChannel.put(targetPath, ChannelSftp.APPEND);
        } catch (Exception e) {
            log.error("SFTP connector write file failed", e);
            throw new RuntimeException("SFTP connector write file failed: " + e.getMessage(), e);
        } 
    }

    @Override
    public void close() throws Exception {
        disconnect();
    }

    private void getFilesRecursive(String dir, String filePattern, boolean recursive, List<String> fileList)
            throws SftpException {
        String originalDir = sftpChannel.pwd();
        try {
            if (dir != null && !dir.trim().isEmpty()) {
                try {
                    sftpChannel.cd(dir);
                } catch (SftpException e) {
                    log.warn("SFTP connector change directory failed, dir: {}", dir);
                    return;
                }
            }
            @SuppressWarnings("unchecked")
            Vector<ChannelSftp.LsEntry> files = sftpChannel.ls(".");
            IOFileFilter fileFilter = null;
            if (filePattern != null && !filePattern.trim().isEmpty()) {
                fileFilter = WildcardFileFilter.builder().setWildcards(filePattern).get();
            }
            String currentWorkingDir = sftpChannel.pwd();
            for (ChannelSftp.LsEntry entry : files) {
                String fileName = entry.getFilename();
                if (".".equals(fileName) || "..".equals(fileName)) {
                    continue;
                }
                SftpATTRS attrs = entry.getAttrs();
                String fullPath = currentWorkingDir.endsWith("/") ? currentWorkingDir + fileName : currentWorkingDir + "/" + fileName;
                if (attrs.isReg()) {
                    if (fileFilter == null || fileFilter.accept(null, fileName)) {
                        fileList.add(fullPath);
                    }
                } else if (attrs.isDir() && recursive) {
                    getFilesRecursive(fullPath, filePattern, recursive, fileList);
                }
            }
        } finally {
            if (originalDir != null) {
                try {
                    sftpChannel.cd(originalDir);
                } catch (SftpException e) {
                    log.warn("Failed to restore original directory: {}", originalDir, e);
                }
            }
        }
    }

    private void connect() throws JSchException, SftpException {
        if (connected && session != null && session.isConnected() &&
                sftpChannel != null && sftpChannel.isConnected()) {
            return;
        }
        JSch jsch = new JSch();
        if (privateKeyPath != null && !privateKeyPath.trim().isEmpty()) {
            if (privateKeyPassphrase != null && !privateKeyPassphrase.trim().isEmpty()) {
                jsch.addIdentity(privateKeyPath, privateKeyPassphrase);
            } else {
                jsch.addIdentity(privateKeyPath);
            }
        }
        session = jsch.getSession(user, host, port);
        if (password != null && !password.trim().isEmpty()) {
            session.setPassword(password);
        }
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", strictHostKeyChecking ? "yes" : "no");
        if (encoding != null && !encoding.trim().isEmpty()) {
            config.put("file.encoding", encoding);
        }
        session.setConfig(config);
        if (timeout != null) {
            session.setTimeout(timeout);
        }
        session.connect();
        Channel channel = session.openChannel("sftp");
        channel.connect();
        sftpChannel = (ChannelSftp) channel;
        connected = true;
        log.debug("SFTP server connection established, host: {}, port: {}", host, port);
    }

    private void disconnect() {
        if (sftpChannel != null && sftpChannel.isConnected()) {
            sftpChannel.disconnect();
            log.debug("SFTP channel disconnected");
        }
        if (session != null && session.isConnected()) {
            session.disconnect();
            connected = false;
            log.debug("SFTP session disconnected");
        }
    }

}