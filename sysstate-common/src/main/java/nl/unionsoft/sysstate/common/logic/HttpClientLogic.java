package nl.unionsoft.sysstate.common.logic;

import org.apache.http.client.HttpClient;

public interface HttpClientLogic {

    public HttpClient getHttpClient(String id);
    
    public void closeIdleConnections();
}
