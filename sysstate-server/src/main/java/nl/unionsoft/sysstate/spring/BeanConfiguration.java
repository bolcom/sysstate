package nl.unionsoft.sysstate.spring;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(BeanConfiguration.class);

    @Bean
    @Inject
    //@formatter:off
    public DataSource dataSource(ConnectionFactory connectionFactory,
            @Value("${db.driver}") String driver, 
            @Value("${db.minIdle}") int minIdle, @Value("${db.maxIdle}") int maxIdle,
            @Value("${db.maxActive}") int maxActive, @Value("${db.maxWait}") long maxWait,
            @Value("${db.minEvictableIdleTimeMillis}") long minEvictableIdleTimeMillis, @Value("${db.softMinEvictableIdleTimeMillis}") long softMinEvictableIdleTimeMillis,
            @Value("${db.numTestsPerEvictionRun}") int numTestsPerEvictionRun,@Value("${db.timeBetweenEvictionRunsMillis}") long timeBetweenEvictionRunsMillis,
            @Value("${db.testOnBorrow}") boolean testOnBorrow, @Value("${db.testOnReturn}") boolean testOnReturn, @Value("${db.testWhileIdle}") boolean testWhileIdle,
            @Value("${db.validationQuery}") String validationQuery, @Value("${db.validationQueryTimeout}") int validationQueryTimeout
            ) {
        //@formatter:on
        logger.info("Loading driver: {}", driver);
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Unable to load driver, caught ClassNotFoundException", e);
        }

        final GenericObjectPool connectionPool = new GenericObjectPool(null);
        connectionPool.setMinIdle(minIdle);
        connectionPool.setMaxIdle(maxIdle);
        connectionPool.setMaxActive(maxActive);
        connectionPool.setMaxWait(maxWait);

        connectionPool.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        connectionPool.setSoftMinEvictableIdleTimeMillis(softMinEvictableIdleTimeMillis);
        connectionPool.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
        connectionPool.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);

        connectionPool.setTestOnBorrow(testOnBorrow);
        connectionPool.setTestOnReturn(testOnReturn);
        connectionPool.setTestWhileIdle(testWhileIdle);
        connectionPool.setFactory(new PoolableConnectionFactory(connectionFactory, connectionPool, null, validationQuery, validationQueryTimeout, false, true));

        return new PoolingDataSource(connectionPool);
    }

    @Bean
    @Inject
    public ConnectionFactory connectionFactory(
            @Value("${db.connectURI}") String connectURI,
            @Value("${db.userName}") String userName, @Value("${db.password}") String password) {
        logger.info("Creating new ConnectionFactory for connectURI [{}] with userName [{}] and password", connectURI, userName);
        return new DriverManagerConnectionFactory(connectURI, userName, password);
    }

}
