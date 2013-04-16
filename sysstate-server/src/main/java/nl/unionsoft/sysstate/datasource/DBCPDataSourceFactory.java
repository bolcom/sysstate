package nl.unionsoft.sysstate.datasource;

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

public class DBCPDataSourceFactory implements FactoryBean<DataSource> {

    private static final Logger LOG = LoggerFactory.getLogger(DBCPDataSourceFactory.class);

    private DataSource dataSource;

    private String driver;
    private String connectURI;
    private String userName;
    private String password;
    private int minIdle;
    private int maxActive;

    public DBCPDataSourceFactory () {
        minIdle = 10;
        maxActive = 20;

    }

    public DataSource getObject() throws Exception {
        if (dataSource == null) {
            try {
                LOG.info("Loading driver: {}", driver);
                Class.forName(driver);
                dataSource = createDataSource();
            } catch(final ClassNotFoundException e) {
                LOG.error("Error loading driver, caught ClassNotFoundException", e);
            }
        }
        return dataSource;
    }

    public DataSource createDataSource() {
        LOG.info("Creating new datasource for:\n\tURI:{}\n\tUserName:{}", connectURI, userName);
        final ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(connectURI, userName, password);
        final GenericObjectPool connectionPool = new GenericObjectPool(null);
        connectionPool.setMinIdle(minIdle);
        connectionPool.setMaxActive(maxActive);
        new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false, true);
        return new PoolingDataSource(connectionPool);
    }

    public Class<?> getObjectType() {
        return DataSource.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getConnectURI() {
        return connectURI;
    }

    public void setConnectURI(String connectURI) {
        this.connectURI = connectURI;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

}
