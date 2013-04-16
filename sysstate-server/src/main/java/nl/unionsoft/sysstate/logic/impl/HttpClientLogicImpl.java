package nl.unionsoft.sysstate.logic.impl;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.logic.HttpClientLogic;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service("httpClientLogic")
public class HttpClientLogicImpl implements HttpClientLogic, InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClientLogicImpl.class);

    @Inject
    @Named("httpClient")
    private HttpClient httpClient;

    private ClientConnectionManager clientConnectionManager;

    public void closeIdleConnections() {
        LOG.info("Closing idle httpClient Connections");
        clientConnectionManager.closeExpiredConnections();
        clientConnectionManager.closeIdleConnections(120, TimeUnit.SECONDS);
    }

    public void afterPropertiesSet() throws Exception {
        clientConnectionManager = httpClient.getConnectionManager();
    }

}
