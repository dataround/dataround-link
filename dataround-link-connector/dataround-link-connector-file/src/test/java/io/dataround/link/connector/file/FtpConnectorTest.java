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
 * FtpConnector unit test
 * 
 * @author test
 * @date 2025-07-26
 */
public class FtpConnectorTest {

    private FtpConnector ftpConnector;
    private Param param;

    @Before
    public void setUp() {
        ftpConnector = new FtpConnector();
        // set test properties
        param = new Param();
        param.setHost("ftp.sra.ebi.ac.uk");
        param.setPort(21);
        param.setUser("testuser");
        param.setPassword("testpass");
        param.setConfig(new HashMap<>());
        param.getConfig().put("passiveMode", "true");
        param.getConfig().put("binaryMode", "true");
        param.getConfig().put("timeout", "30000");
        param.getConfig().put("encoding", "UTF-8");
        ftpConnector.initialize(param);
    }

    @After
    public void tearDown() {
        ftpConnector = null;
        param = null;
    }

    @Test
    public void testGetName() {
        assertEquals("FTP", ftpConnector.getName());
    }

    @Test
    public void testDoInitialize() {
        ftpConnector.doInitialize();
    }

    @Test
    public void testTestConnectivitySuccess() {
        ftpConnector.doInitialize();
        boolean result = ftpConnector.testConnectivity();
        assertTrue(result);
    }

    @Test
    public void testGetFilesWithWildcard() {
        ftpConnector.doInitialize();
        List<String> files = ftpConnector.getFiles("/", "*.txt");
        for (String file : files) {
            System.out.println(file);
        }
        assertNotNull(files);
        assertTrue(files.size() > 0);
    }

    @Test
    public void testGetFilesWithoutWildcard() {
        ftpConnector.doInitialize();
        List<String> files = ftpConnector.getFiles("/");
        for (String file : files) {
            System.out.println(file);
        }
        assertNotNull(files);
    }

    @Test
    public void testGetFilesRecursive() {
        ftpConnector.doInitialize();
        // Test recursive search - this will search subdirectories
        List<String> files = ftpConnector.getFiles("/pub/ensembl/release-30/anopheles-30.2e/data/fasta", "*", true);
        assertNotNull(files);
        System.out.println("Recursive files found: " + files.size());
        for (String file : files) {
            System.out.println("Recursive: " + file);
        }
    }

    @Test
    public void testGetFilesNonRecursive() {
        ftpConnector.doInitialize();
        // Test non-recursive search - this will only search current directory
        List<String> files = ftpConnector.getFiles("/pub/ensembl/release-30/anopheles-30.2e/data/fasta", "*", false);
        assertNotNull(files);
        System.out.println("Non-recursive files found: " + files.size());
        for (String file : files) {
            System.out.println("Non-recursive: " + file);
            // Non-recursive files should not contain path separators
            assertFalse("File should not contain path separator for non-recursive search",
                    file.contains("/"));
        }
    }

    @Test
    public void testGetFilesWithPatternRecursive() {
        ftpConnector.doInitialize();
        // Test recursive search with file pattern
        List<String> files = ftpConnector.getFiles("/vol1", "*.gz", true);
        assertNotNull(files);
        System.out.println("Recursive .gz files found: " + files.size());
        for (String file : files) {
            System.out.println("Recursive .gz: " + file);
            assertTrue("File should end with .gz", file.endsWith(".gz"));
        }
    }
}