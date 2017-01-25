package nl.unionsoft.sysstate.plugins.http;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpGetBuilder {

    private final static Logger logger = LoggerFactory.getLogger(HttpGetBuilder.class);
    private HttpClient httpClient;
    private final HttpGet httpGet;

    private HttpGetBuilder(HttpClient httpClient, String url) {
        this.httpClient = httpClient;
        this.httpGet = new HttpGet(url);
    }

    public HttpGetBuilder create(HttpClient httpClient, String url) {
        return new HttpGetBuilder(httpClient, url);
    }

    public HttpGetBuilder withBasicAuthentication(String userName, String password) {
        if (StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password)) {
            try {
                UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(userName, password);
                httpGet.addHeader(new BasicScheme().authenticate(credentials, httpGet));
            } catch (AuthenticationException e) {
                throw new IllegalStateException("Unable to apply authentication", e);
            }
        }
       
        return this;
    }

    public <T> T execute(HttpClientCallback<T> httpClientCallback) throws ClientProtocolException, IOException {
        HttpEntity httpEntity = null;
        try {

            HttpResponse httpResponse = httpClient.execute(httpGet);
            StatusLine statusLine = httpResponse.getStatusLine();
            httpEntity = httpResponse.getEntity();
            return httpClientCallback.consume(httpEntity, statusLine);
        } finally {
            try {
                EntityUtils.consume(httpEntity);
            } catch (IOException e) {
                logger.warn("Caught IOException while consuming entity", e);
            }
        }
    }
}
