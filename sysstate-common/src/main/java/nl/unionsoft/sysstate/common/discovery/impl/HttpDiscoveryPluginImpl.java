package nl.unionsoft.sysstate.common.discovery.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.common.dto.EnvironmentDto;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.ProjectDto;
import nl.unionsoft.sysstate.common.dto.ProjectEnvironmentDto;
import nl.unionsoft.sysstate.common.logic.EnvironmentLogic;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.common.logic.ProjectLogic;
import nl.unionsoft.sysstate.common.plugins.DiscoveryPlugin;
import nl.unionsoft.sysstate.common.queue.AddDiscoveredInstancesWorker;
import nl.unionsoft.sysstate.common.queue.ReferenceRunnable;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HttpDiscoveryPluginImpl implements DiscoveryPlugin {

    public static final String URL = "url";

    private static final Logger LOG = LoggerFactory.getLogger(HttpDiscoveryPluginImpl.class);

    @Inject
    @Named("environmentLogic")
    private EnvironmentLogic environmentLogic;

    @Inject
    @Named("projectLogic")
    private ProjectLogic projectLogic;

    @Inject
    @Named("instanceLogic")
    private InstanceLogic instanceLogic;

    @Inject
    @Named("httpClient")
    private HttpClient httpClient;

    public Collection<? extends ReferenceRunnable> discover(Properties properties) {
        LOG.info("Preparing httpRequest...");
        final String uri = processUri(properties.getProperty(URL));
        if (StringUtils.isEmpty(uri)) {
            throw new IllegalStateException("URL is empty!");
        }
        final HttpGet httpGet = new HttpGet(uri);
        httpGet.addHeader("Connection", "close");
        final Long startTime = System.currentTimeMillis();
        final List<AddDiscoveredInstancesWorker> results = new ArrayList<AddDiscoveredInstancesWorker>();
        try {
            LOG.info("Executing httpRequest...");
            final HttpResponse httpResponse = httpClient.execute(httpGet);
            final long responseTime = System.currentTimeMillis() - startTime;
            LOG.info("HttpRequest complete, execution took {} ms", responseTime);
            final Collection<? extends InstanceDto> instances = removeDupes(handleHttpResponse(properties, httpResponse));
            if (instances != null && !instances.isEmpty()) {
                AddDiscoveredInstancesWorker addDiscoveredInstancesWorker = new AddDiscoveredInstancesWorker();
                addDiscoveredInstancesWorker.setResults(instances);
                results.add(addDiscoveredInstancesWorker);
            }
        } catch(final Exception e) {
            LOG.warn("Caught Exception while performing request: {}", e.getMessage(), e);
        } finally {

        }
        return results;
    }

    private Collection<? extends InstanceDto> removeDupes(Collection<? extends InstanceDto> discoveredInstances) {
        final List<InstanceDto> currentInstances = instanceLogic.getInstances();
        final Collection<InstanceDto> results = new ArrayList<InstanceDto>();
        if (discoveredInstances != null) {
            for (final InstanceDto discoveredInstance : discoveredInstances) {

                if (currentInstances.contains(discoveredInstance)) {
                    LOG.info("Duplicate instance {}, skipping...", discoveredInstance);
                } else {
                    matchProjectAndEnvironment(discoveredInstance);
                    results.add(discoveredInstance);
                }
            }
        }
        return results;
    }

    private void matchProjectAndEnvironment(InstanceDto discoveredInstance) {
        final ProjectEnvironmentDto projectEnvironment = discoveredInstance.getProjectEnvironment();
        if (projectEnvironment != null) {
            final ProjectDto project = projectEnvironment.getProject();
            if (project != null && StringUtils.isNotEmpty(project.getName())) {
                final ProjectDto foundProject = projectLogic.findProject(project.getName());
                if (foundProject != null) {
                    project.setId(foundProject.getId());
                }
            }
            final EnvironmentDto environment = projectEnvironment.getEnvironment();
            if (environment != null && StringUtils.isNotEmpty(environment.getName())) {
                final EnvironmentDto foundEnvironment = environmentLogic.findEnvironment(environment.getName());
                if (foundEnvironment != null) {
                    environment.setId(foundEnvironment.getId());
                }
            }
        }
    }

    private Collection<? extends InstanceDto> handleHttpResponse(Properties configuration, final HttpResponse httpResponse) throws IOException {
        Collection<? extends InstanceDto> results = null;
        HttpEntity httpEntity = null;
        try {
            final StatusLine statusLine = httpResponse.getStatusLine();

            httpEntity = httpResponse.getEntity();
            final int statusCode = statusLine.getStatusCode();
            if (statusCode >= 300) {
                LOG.error("Invalid statuscode for request, statusCode: {}", statusCode);
            } else {
                results = handleEntity(httpEntity, configuration);
            }
        } finally {
            EntityUtils.consume(httpEntity);
        }
        return results;
    }

    public abstract Collection<? extends InstanceDto> handleEntity(final HttpEntity httpEntity, Properties configuration) throws IOException;

    public String processUri(String uri) {
        return uri;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void updatePropertiesTemplate(Properties properties) {
        properties.setProperty(URL, "");
    }
}
