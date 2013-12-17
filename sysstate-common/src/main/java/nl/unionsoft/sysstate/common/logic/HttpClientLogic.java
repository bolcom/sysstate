package nl.unionsoft.sysstate.common.logic;

import java.util.Set;

import org.apache.http.client.HttpClient;

public interface HttpClientLogic {

    public HttpClient getHttpClient(String id);
 
    public Set<String> getHttpClientIds();
    public void closeIdleConnections();
}
