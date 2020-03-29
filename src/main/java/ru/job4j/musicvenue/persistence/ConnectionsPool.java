package ru.job4j.musicvenue.persistence;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.Properties;

public class ConnectionsPool {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectionsPool.class);

    private static final ConnectionsPool INSTANCE = new ConnectionsPool();
    private static ComboPooledDataSource dataSource;

    static {
        Properties prs = new Properties();
        dataSource = new ComboPooledDataSource();

        try {
            prs.load(ConnectionsPool.class.getClassLoader().getResourceAsStream("db.properties"));

            dataSource.setDriverClass(prs.getProperty("jdbc.driver"));
            dataSource.setJdbcUrl(prs.getProperty("jdbc.url"));
            dataSource.setUser(prs.getProperty("jdbc.username"));
            dataSource.setPassword(prs.getProperty("jdbc.password"));
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private ConnectionsPool() { }

    public static ConnectionsPool getInstance() {
        return INSTANCE;
    }

    public Connection getConnection() throws PersistException {
        Connection result = null;
        try {
            result = dataSource.getConnection();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new PersistException("Can't obtain connection from the pool.", e);
        }
        return result;
    }
}
