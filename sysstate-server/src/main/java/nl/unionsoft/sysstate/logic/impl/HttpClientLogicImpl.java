package nl.unionsoft.sysstate.logic.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.common.logic.HttpClientLogic;
import nl.unionsoft.sysstate.common.util.PropertyGroupUtil;
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
            LOG.debug("Closing idle httpClient Connections for client '{}'", entry.getKey());
            HttpClient httpClient = entry.getValue();
            ClientConnectionManager clientConnectionManager = httpClient.getConnectionManager();
            clientConnectionManager.closeExpiredConnections();
            clientConnectionManager.closeIdleConnections(120, TimeUnit.SECONDS);

        }
    }

    public void afterPropertiesSet() throws Exception {
        Map<String, Properties> httpClientGroupProps = PropertyGroupUtil.getGroupProperties(properties, "httpClient");
        for (Entry<String, Properties> entry : httpClientGroupProps.entrySet()) {
            String id = entry.getKey();
            LOG.info("Configuring HttpClient for id '{}'", id);
            Properties groupProps = entry.getValue();
            int connectionTimeoutMillis = Integer.valueOf(groupProps.getProperty("connectionTimeoutMillis", "45000"));
            int socketTimeoutMillis = Integer.valueOf(groupProps.getProperty("socketTimeoutMillis", "45000"));
            int proxyPort = Integer.valueOf(groupProps.getProperty("proxyPort", "0"));
            String proxyHost = groupProps.getProperty("proxyHost");
            LOG.info("HttpClient settings are: connectionTimeoutMillis={},socketTimeoutMillis={}, proxyPort={}, proxyHost={}", new Object[] {
                    connectionTimeoutMillis, socketTimeoutMillis, proxyPort, proxyHost });
            HttpClientFactoryBean httpClientFactoryBean = new HttpClientFactoryBean(connectionTimeoutMillis,
                    socketTimeoutMillis);
            httpClientFactoryBean.setProxyHost(proxyHost);
            httpClientFactoryBean.setProxyPort(proxyPort);
            httpClients.put(id, httpClientFactoryBean.getObject());
        }
    }

    public HttpClient getHttpClient(String id) {
        HttpClient httpClient = httpClients.get(id);
        if (httpClient == null) {
            throw new IllegalArgumentException("No httpClient could be found for id [" + id + "]");
        }
        return httpClient;
    }

    public Set<String> getHttpClientIds() {
        return httpClients.keySet();
    }

}
