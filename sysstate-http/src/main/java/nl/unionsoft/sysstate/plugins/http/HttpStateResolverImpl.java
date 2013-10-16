package nl.unionsoft.sysstate.plugins.http;

import java.io.IOException;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.common.util.PropertiesUtil;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.common.extending.ConfigurationHolder;
import nl.unionsoft.sysstate.common.extending.ConfiguredBy;
import nl.unionsoft.sysstate.common.extending.StateResolver;
import nl.unionsoft.sysstate.common.util.StateUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("httpStateResolver")
@ConfiguredBy(instanceConfig = HttpStateResolverConfig.class)
public class HttpStateResolverImpl<ICT extends HttpStateResolverConfig> implements StateResolver<HttpStateResolverConfig> {

    public static final String URL = "url";

    private static final Logger LOG = LoggerFactory.getLogger(HttpStateResolverImpl.class);

    @Inject
    @Named("httpClient")
    private HttpClient httpClient;

    public void setState(final InstanceDto<HttpStateResolverConfig> instance, final StateDto state, final ConfigurationHolder configurationHolder) {
        state.setState(StateType.STABLE);
        LOG.info("Preparing httpRequest...");
        HttpStateResolverConfig httpStateResolverConfig = instance.getInstanceConfiguration();

        final String uri = processUri(httpStateResolverConfig.getUrl());
        if (StringUtils.isEmpty(uri)) {
            throw new IllegalStateException("URL is empty!");
        }
        final HttpGet httpGet = new HttpGet(uri);
        httpGet.addHeader("Connection", "close");

        String userAgent = httpStateResolverConfig.getUserAgent();
        if (StringUtils.isNotEmpty(userAgent)) {
            httpGet.addHeader("User-Agent", userAgent);
        }

        final Long startTime = System.currentTimeMillis();
        try {
            LOG.info("Executing httpRequest...");
            final HttpResponse httpResponse = httpClient.execute(httpGet);
            final long responseTime = System.currentTimeMillis() - startTime;
            LOG.info("HttpRequest complete, execution took {} ms", responseTime);
            state.setResponseTime(responseTime);
            //FIXME
            handleHttpResponse(state, (ICT) httpStateResolverConfig, httpResponse);

        } catch (final Exception e) {
            LOG.warn("Caught Exception while performing request: {}", e.getMessage(), e);
            handleStateForException(state, e, startTime);
        }
    }

    public String processUri(final String uri) {
        return uri;
    }

   
    private void handleHttpResponse(final StateDto state, final ICT configuration, final HttpResponse httpResponse) throws IOException {
        HttpEntity httpEntity = null;
        try {
            final StatusLine statusLine = httpResponse.getStatusLine();
            state.setDescription("Status " + statusLine.getStatusCode());
            httpEntity = httpResponse.getEntity();
            final int statusCode = statusLine.getStatusCode();
            if (statusCode >= 300) {
                if (statusCode >= 400) {
                    state.setState(StateType.ERROR);
                } else {
                    state.setState(StateType.UNSTABLE);
                }
                state.setMessage(EntityUtils.toString(httpEntity));
            } else {
                handleEntity(httpEntity, configuration, state);
            }
        } finally {
            EntityUtils.consume(httpEntity);
        }
    }

    private void handleStateForException(final StateDto state, final Exception exception, final Long startTime) {
        state.setState(StateType.ERROR);
        state.setDescription(exception.getMessage());
        state.setResponseTime(System.currentTimeMillis() - startTime);
        state.setMessage(StateUtil.exceptionAsMessage(exception));
    }

    public void handleEntity(final HttpEntity httpEntity, final ICT configuration, final StateDto state) throws IOException {

    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(final HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public String generateHomePageUrl(final InstanceDto<HttpStateResolverConfig> instance) {
        HttpStateResolverConfig httpStateResolverConfig = instance.getInstanceConfiguration();
        String homePageUrl = processUri(httpStateResolverConfig.getUrl());
        return StringUtils.substringBefore(homePageUrl, "//") + "//" + StringUtils.substringBetween(homePageUrl, "//", "/");
    }

}
