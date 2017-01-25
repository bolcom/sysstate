package nl.unionsoft.sysstate.plugins.http;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;

public interface HttpClientCallback<T> {

    T consume(HttpEntity httpEntity, StatusLine statusLine);

}
