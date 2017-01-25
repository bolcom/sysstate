package nl.unionsoft.sysstate.plugins.groovy.http

import groovy.json.JsonSlurper
import nl.unionsoft.sysstate.plugins.http.HttpClientCallback

import org.apache.http.HttpEntity
import org.apache.http.StatusLine

class JsonSlurperHttpGetCallback implements HttpClientCallback<Object> {

    @Override
    public Object consume(HttpEntity httpEntity, StatusLine statusLine) {
        final int statusCode = statusLine.getStatusCode();
        assert statusCode >= 200 && statusCode < 300 , "Unable to perform request, got statusCode [${statusCode}] instead of 200-299"
        return new JsonSlurper().parse(httpEntity.getContent());
    }
}
