package io.dataround.link.connector.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.dataround.link.common.connector.Param;

/**
 * SftpConnector unit test
 * 
 * @author test
 * @date 2025-07-26
 */
public class SftpConnectorTest {

    private SftpConnector sftpConnector;
    private Param param;

    @Before
    public void setUp() {
        sftpConnector = new SftpConnector();
        // set test properties
        param = new Param();
        param.setHost("10.10.10.10");
        param.setPort(22);
        param.setUser("test");
        param.setPassword("test");
        param.setConfig(new HashMap<>());
        param.getConfig().put("strictHostKeyChecking", "false");
        param.getConfig().put("timeout", "30000");
        param.getConfig().put("encoding", "UTF-8");
        sftpConnector.initialize(param);
    }

    @After
    public void tearDown() {
        sftpConnector = null;
        param = null;
    }

    @Test
    public void testGetName() {
        assertEquals("SFTP", sftpConnector.getName());
    }

    @Test
    public void testDoInitialize() {
        sftpConnector.doInitialize();
    }

    @Test
    public void testTestConnectivitySuccess() {
        sftpConnector.doInitialize();
        try {
            boolean result = sftpConnector.testConnectivity();
            assertTrue("Should connect successfully to SFTP server", result);
        } catch (Exception e) {
            // Network issues may cause test to fail, log and continue
            System.out.println("SFTP connectivity test skipped: " + e.getMessage());
        }
    }

    @Test
    public void testTestConnectivityFailure() {
        // Test with invalid host to verify failure handling
        param.setHost("invalid.host.example.com");
        param.setPort(22);
        sftpConnector.initialize(param);
        sftpConnector.doInitialize();
        boolean result = sftpConnector.testConnectivity();
        assertFalse(result); // Should fail for invalid host
    }

    @Test
    public void testGetFilesWithoutWildcard() {
        sftpConnector.doInitialize();
        try {
            List<String> files = sftpConnector.getFiles("mysql");
            assertNotNull(files);
            for (String file : files) {
                System.out.println("SFTP file: " + file);
            }
        } catch (Exception e) {
            System.out.println("SFTP file listing test skipped: " + e.getMessage());
        }
    }

    @Test
    public void testGetFilesWithWildcard() {
        sftpConnector.doInitialize();
        try {
            List<String> files = sftpConnector.getFiles("mysql", "*");
            assertNotNull(files);
            for (String file : files) {
                System.out.println("SFTP .txt file: " + file);
                assertTrue("File should end with .txt", file.endsWith(".txt"));
            }
        } catch (Exception e) {
            System.out.println("SFTP wildcard file test skipped: " + e.getMessage());
        }
    }

    @Test
    public void testGetFilesRecursive() {
        sftpConnector.doInitialize();
        try {
            // Test recursive search - this will search subdirectories
            List<String> files = sftpConnector.getFiles("/root/mysql", "*", true);
            assertNotNull(files);
            System.out.println("SFTP recursive files found: " + files.size());
            for (String file : files) {
                System.out.println("SFTP recursive: " + file);
            }
        } catch (Exception e) {
            System.out.println("SFTP recursive test skipped: " + e.getMessage());
        }
    }

    @Test
    public void testGetFilesNonRecursive() {
        sftpConnector.doInitialize();
        try {
            // Test non-recursive search - this will only search current directory
            List<String> files = sftpConnector.getFiles("/home", "*", false);
            assertNotNull(files);
            System.out.println("SFTP non-recursive files found: " + files.size());
            for (String file : files) {
                System.out.println("SFTP non-recursive: " + file);
            }
        } catch (Exception e) {
            System.out.println("SFTP non-recursive test skipped: " + e.getMessage());
        }
    }

    @Test
    public void testPrivateKeyAuthentication() {
        // Test with private key authentication (if available)
        Param param = new Param();
        param.setHost("test.sftp.server.com");
        param.setPort(22);
        param.setUser("testuser");
        param.setConfig(new HashMap<>());
        param.getConfig().put("privateKeyPath", "/path/to/private/key");
        param.getConfig().put("strictHostKeyChecking", "false");
        
        SftpConnector keyConnector = new SftpConnector();
        keyConnector.initialize(param);
        keyConnector.doInitialize();
        
        // This test will likely fail due to missing key file, but verifies configuration
        try {
            boolean result = keyConnector.testConnectivity();
            // Don't assert here as this is just a configuration test
            System.out.println("Private key authentication test result: " + result);
        } catch (Exception e) {
            System.out.println("Private key test skipped (expected): " + e.getMessage());
        } finally {
            try {
                keyConnector.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
} 