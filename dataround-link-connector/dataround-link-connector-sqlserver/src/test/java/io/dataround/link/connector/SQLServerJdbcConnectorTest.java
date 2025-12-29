package io.dataround.link.connector;

import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import io.dataround.link.common.connector.Param;

public class SQLServerJdbcConnectorTest {
    
    private Param param;

    @Before
    public void setUp() {
        Param param = new Param();
        param.setUser("sa");
        param.setPassword("dataround.io");
        param.setConfig(new HashMap<>());
        param.getConfig().put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        param.getConfig().put("connectionTimeout", "30000");
        param.getConfig().put("url", "jdbc:sqlserver://192.168.10.231:1433;databaseName=tpcds_tst;encrypt=false;trustServerCertificate=true;loginTimeout=30;socketTimeout=30000");
        this.param = param;
    }

    @Test
    public void testDoGetDatabases() {
        try (SQLServerJdbcConnector connector = new SQLServerJdbcConnector()) {
            connector.initialize(param);
            List<String> databases = connector.doGetDatabases();
            System.out.println(String.join(", ", databases));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
