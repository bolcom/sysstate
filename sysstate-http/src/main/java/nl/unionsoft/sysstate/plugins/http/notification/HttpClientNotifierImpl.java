package nl.unionsoft.sysstate.plugins.http.notification;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.common.extending.Notification;
import nl.unionsoft.sysstate.common.extending.Notifier;
import nl.unionsoft.sysstate.common.logic.HttpClientLogic;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HttpClientNotifierImpl implements  Notifier{

    @Inject
    @Named("httpClientLogic")
    private HttpClientLogic httpClientLogic;

    private static final Logger LOG = LoggerFactory.getLogger(HttpClientNotifierImpl.class);

    public void notify(Notification notification, Map<String, String> properties) {
        HttpClient httpClient = httpClientLogic.getHttpClient(StringUtils.defaultIfEmpty(properties.get("httpClientId"), "default"));
        notify(notification, properties, httpClient);
    }
    
    protected abstract void notify(Notification notification, Map<String, String> properties, HttpClient httpClient);

    

    protected void handleRequest(HttpRequestBase httpRequestBase, HttpClient httpClient) {
        final Long startTime = System.currentTimeMillis();
        try {
            LOG.info("Executing httpRequest...");
            final HttpResponse httpResponse = httpClient.execute(httpRequestBase);
            final long responseTime = System.currentTimeMillis() - startTime;
            LOG.info("HttpRequest complete, execution took {} ms", responseTime);

            HttpEntity httpEntity = null;
            try {
                final StatusLine statusLine = httpResponse.getStatusLine();
                httpEntity = httpResponse.getEntity();
                final int statusCode = statusLine.getStatusCode();
                LOG.info("HttpRequest returned with statusCode " + statusCode);
            } finally {
                EntityUtils.consume(httpEntity);
            }

        } catch (final Exception e) {
            LOG.warn("Caught Exception while performing request: {}", e.getMessage(), e);
        }

    }
    
    
    
    public HttpClientLogic getHttpClientLogic() {
        return httpClientLogic;
    }

    public void setHttpClientLogic(HttpClientLogic httpClientLogic) {
        this.httpClientLogic = httpClientLogic;
    }

    protected void addEntity(HttpEntityEnclosingRequestBase requestBase, String body){
        
        if (StringUtils.isNotEmpty(body)) {
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(body.getBytes("UTF-8"));
                BasicHttpEntity basicHttpEntity = new BasicHttpEntity();
                basicHttpEntity.setContent(bais);
                requestBase.setEntity(basicHttpEntity);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    
}
