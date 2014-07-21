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
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestNotifierImpl extends HttpClientNotifierImpl {

    @Override
    protected void notify(Notification notification, Map<String, String> properties, HttpClient httpClient) {
        final String uri = processUri(properties.get("url"));
        if (StringUtils.isEmpty(uri)) {
            throw new IllegalStateException("URL is empty!");
        }

        String method = properties.get("method");
        HttpRequestBase httpRequest = null;
        if (StringUtils.equals("get", method)) {
            final HttpGet httpGet = new HttpGet(uri);
            httpRequest = httpGet;
        } else if (StringUtils.equals("delete", method)) {
            final HttpDelete httpDelete = new HttpDelete(uri);
            httpRequest = httpDelete;
        } else if (StringUtils.equals("post", method)) {
            final HttpPost httpPost = new HttpPost(uri);
            addEntity(httpPost, notification, properties);
            httpRequest = httpPost;
        } else if (StringUtils.equals("put", method)) {
            final HttpPut httpPut = new HttpPut(uri);
            addEntity(httpPut, notification, properties);
            httpRequest = httpPut;
        }
        addHeaders(httpRequest, notification, properties);
        handleRequest(httpRequest, httpClient);
    }

    protected void addHeaders(HttpRequestBase httpRequestBase, Notification notification, Map<String, String> properties) {
        httpRequestBase.addHeader("Connection", "close");
    }

    protected void addEntity(HttpEntityEnclosingRequestBase requestBase, Notification notification, Map<String, String> properties) {
        
        String body = properties.get("body");
        addEntity(requestBase, body);
    }

    protected String processUri(final String uri) {
        return uri;
    }

}
