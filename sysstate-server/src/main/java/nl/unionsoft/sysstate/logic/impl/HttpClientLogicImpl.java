package nl.unionsoft.sysstate.logic.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.common.logic.HttpClientLogic;
import nl.unionsoft.sysstate.factorybeans.HttpClientFactoryBean;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service("httpClientLogic")
public class HttpClientLogicImpl implements HttpClientLogic, InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClientLogicImpl.class);

    private Map<String, HttpClient> httpClients;

    @Inject
    @Named("properties")
    private Properties properties;

    public HttpClientLogicImpl() {
        httpClients = new HashMap<String, HttpClient>();
    }

    public void closeIdleConnections() {
        for (Entry<String, HttpClient> entry : httpClients.entrySet()) {
            LOG.info("Closing idle httpClient Connections for client '{}'", entry.getKey());
            HttpClient httpClient = entry.getValue();
            ClientConnectionManager clientConnectionManager = httpClient.getConnectionManager();
            clientConnectionManager.closeExpiredConnections();
            clientConnectionManager.closeIdleConnections(120, TimeUnit.SECONDS);

        }
    }

    public void afterPropertiesSet() throws Exception {
        for (String propertyName : properties.stringPropertyNames()) {
            if (StringUtils.startsWith(propertyName, "httpClient") && StringUtils.countMatches(propertyName, ".") == 1) {
                String id = StringUtils.split(propertyName, '.')[1];
                int connectionTimeoutMillis = Integer.valueOf(properties.getProperty("httpClient." + id + ".connectionTimeoutMillis", "45000"));
                int socketTimeoutMillis = Integer.valueOf(properties.getProperty("httpClient." + id + ".socketTimeoutMillis", "45000"));
                HttpClientFactoryBean httpClientFactoryBean = new HttpClientFactoryBean(connectionTimeoutMillis, socketTimeoutMillis);
                
                HttpHost proxy = new HttpHost("127.0.0.1", 8080, "http");
                httpClients.put(id, httpClientFactoryBean.getObject());
                
                
            }
        }
    }

    public HttpClient getHttpClient(String id) {
        return httpClients.get(id);
    }

}
