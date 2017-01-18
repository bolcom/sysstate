package nl.unionsoft.sysstate.plugins.groovy.consul

import groovy.json.JsonSlurper

import java.util.regex.Matcher;

import javax.inject.Inject;
import javax.inject.Named

import nl.unionsoft.sysstate.common.dto.EnvironmentDto
import nl.unionsoft.sysstate.common.dto.InstanceDto
import nl.unionsoft.sysstate.common.dto.ProjectDto
import nl.unionsoft.sysstate.common.dto.ProjectEnvironmentDto;
import nl.unionsoft.sysstate.common.extending.InstanceStateResolver
import nl.unionsoft.sysstate.common.logic.EnvironmentLogic
import nl.unionsoft.sysstate.common.logic.InstanceLinkLogic
import nl.unionsoft.sysstate.common.logic.InstanceLogic
import nl.unionsoft.sysstate.common.logic.ProjectLogic
import nl.unionsoft.sysstate.common.logic.ResourceLogic
import nl.unionsoft.sysstate.plugins.http.HttpConstants
import nl.unionsoft.sysstate.sysstate_1_0.ProjectEnvironment;

import org.apache.commons.lang.StringUtils
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.StatusLine
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.util.EntityUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class AbstractConsulPatternInstanceResolver extends InstanceStateResolver {

    ResourceLogic resourceLogic

    @Inject
    public AbstractConsulPatternInstanceResolver(InstanceLogic instanceLogic, InstanceLinkLogic instanceLinkLogic,ResourceLogic resourceLogic) {
        super(instanceLinkLogic, instanceLogic)
        this.resourceLogic = resourceLogic;
    }


    private static final Logger log = LoggerFactory.getLogger(AbstractConsulPatternInstanceResolver.class);

    public List<InstanceDto> generateInstances(InstanceDto parent) {

        def configuration = parent.getConfiguration()
        def services = getServices(configuration)
        def validInstanceIds = []

        def servicesPattern = configuration["servicesPattern"]
        assert servicesPattern, "no servicesPattern defined"

        def instances = [];
        services.each { serviceName,tags ->
            Matcher serviceNameMatcher = serviceName =~ servicesPattern
            if (!serviceNameMatcher.matches()){
                log.debug("ServiceName [${serviceName}] does not match servicesPattern [${servicesPattern}], skipping...")
                return
            }

            log.debug("ServiceName [${serviceName}] matches servicesPattern [${servicesPattern}], processing...")
            def environmentName = serviceNameMatcher.group('environment').toString().toUpperCase();
            def applicationName = serviceNameMatcher.group('application').toString().toUpperCase();
            log.debug("For serviceName the environment is [${environmentName}] and the application is [${applicationName}]")

            def project = new ProjectDto(name:applicationName);
            def environment = new EnvironmentDto(name:environmentName)
            instances << createInstance(serviceName, project, environment, parent)
        }
        return instances;
    }

    def createInstance(String reference, ProjectDto project, EnvironmentDto environment, InstanceDto parent){
        ProjectEnvironmentDto projectEnvironment = new ProjectEnvironmentDto(project: project, environment: environment);
        InstanceDto instance = new InstanceDto(
                name: reference,
                reference: reference,
                projectEnvironment : projectEnvironment,
                enabled:true);
        configure(instance, parent);
        return instance;
    }

    abstract void configure(InstanceDto instance, InstanceDto parent)

    def getServices(Map<String, String> configuration){
        HttpClient httpClient = resourceLogic.getResourceInstance(HttpConstants.RESOURCE_MANAGER_NAME, StringUtils.defaultIfEmpty(configuration.get(HttpConstants.HTTP_CLIENT_ID), HttpConstants.DEFAULT_RESOURCE));
        HttpEntity httpEntity = null;
        try {
            def serverUrl = configuration.get("serverUrl") ? configuration.get("serverUrl") : 'http://localhost:8500'
            def dataCenter = configuration.get("dataCenter") ? configuration.get("dataCenter") : ''
            final HttpGet httpGet = new HttpGet("${serverUrl.trim()}/v1/catalog/services?dc=${dataCenter}");
            final HttpResponse httpResponse = httpClient.execute(httpGet);
            final StatusLine statusLine = httpResponse.getStatusLine();
            httpEntity = httpResponse.getEntity();

            final int statusCode = statusLine.getStatusCode();
            assert statusCode == 200, "Unable to perform request, got statusCode [${statusCode}] instead of 200"
            return new JsonSlurper().parse(httpEntity.getContent());
        } finally {
            EntityUtils.consume(httpEntity);
        }
    }


    @Override
    public String generateHomePageUrl(InstanceDto instance) {
        return instance.getConfiguration().get("serverUrl");
    }
}
