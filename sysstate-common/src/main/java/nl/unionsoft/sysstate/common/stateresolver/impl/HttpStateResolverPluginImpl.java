package nl.unionsoft.sysstate.common.stateresolver.impl;

import java.io.IOException;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;

import net.xeoh.plugins.base.annotations.Capabilities;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import nl.unionsoft.common.util.PropertiesUtil;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.common.plugins.StateResolverPlugin;
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

@PluginImplementation
public class HttpStateResolverPluginImpl implements StateResolverPlugin {

    public static final String URL = "url";

    private static final Logger LOG = LoggerFactory.getLogger(HttpStateResolverPluginImpl.class);

    @Inject
    @Named("httpClient")
    private HttpClient httpClient;

    public void setState(InstanceDto instance, StateDto state) {
        state.setState(StateType.STABLE);
        LOG.info("Preparing httpRequest...");
        final Properties configuration = getPropsFromConfiguration(instance.getConfiguration());

        final String uri = processUri(configuration.getProperty(URL));
        if (StringUtils.isEmpty(uri)) {
            throw new IllegalStateException("URL is empty!");
        }
        final HttpGet httpGet = new HttpGet(uri);
        httpGet.addHeader("Connection", "close");
        String userAgent = configuration.getProperty("userAgent");
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
            handleHttpResponse(state, configuration, httpResponse);

        } catch(final Exception e) {
            LOG.warn("Caught Exception while performing request: {}", e.getMessage(), e);
            handleStateForException(state, e, startTime);
        } finally {

        }
    }

    public String processUri(String uri) {
        return uri;
    }

    private Properties getPropsFromConfiguration(String configuration) {
        Properties properties = new Properties();
        if (StringUtils.isNotBlank(configuration)) {
            boolean isProperties = false;

            for (final String row : StringUtils.split(configuration, '\n')) {
                if (StringUtils.startsWith(row, "url=")) {
                    isProperties = true;
                    break;
                }
            }
            if (isProperties) {

                properties = PropertiesUtil.stringToProperties(configuration);
            } else {
                properties = new Properties();
                properties.setProperty(URL, configuration);
            }

        }

        return properties;
    }

    private void handleHttpResponse(final StateDto state, Properties configuration, final HttpResponse httpResponse) throws IOException {
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

    public void handleEntity(final HttpEntity httpEntity, Properties configuration, final StateDto state) throws IOException {

    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Capabilities
    public String[] capabilities() {
        return new String[] { "httpStateResolver" };
    }

    public String generateHomePageUrl(InstanceDto instance) {
        final Properties configuration = getPropsFromConfiguration(instance.getConfiguration());
        String result = null;
        if (configuration != null) {
            String homePageUrl = processUri(configuration.getProperty(URL));
            result = StringUtils.substringBefore(homePageUrl, "//") + "//" + StringUtils.substringBetween(homePageUrl, "//", "/");
        }
        return result;
    }
}
