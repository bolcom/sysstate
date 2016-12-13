package nl.unionsoft.sysstate.plugins.groovy.consul

import groovy.json.JsonSlurper

import javax.inject.Inject;
import javax.inject.Named

import nl.unionsoft.sysstate.common.dto.EnvironmentDto
import nl.unionsoft.sysstate.common.dto.InstanceDto
import nl.unionsoft.sysstate.common.dto.ProjectDto
import nl.unionsoft.sysstate.common.extending.InstanceStateResolver
import nl.unionsoft.sysstate.common.logic.EnvironmentLogic
import nl.unionsoft.sysstate.common.logic.InstanceLinkLogic
import nl.unionsoft.sysstate.common.logic.InstanceLogic
import nl.unionsoft.sysstate.common.logic.ProjectLogic
import nl.unionsoft.sysstate.common.logic.ResourceLogic
import nl.unionsoft.sysstate.plugins.http.HttpConstants

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
    ProjectLogic projectLogic

    @Inject
    public AbstractConsulPatternInstanceResolver(ResourceLogic resourceLogic, ProjectLogic projectLogic, EnvironmentLogic environmentLogic, InstanceLogic instanceLogic, InstanceLinkLogic instanceLinkLogic) {
        super(instanceLinkLogic, instanceLogic, environmentLogic)
        this.resourceLogic = resourceLogic;
        this.projectLogic = projectLogic;
    }


    private static final Logger log = LoggerFactory.getLogger(AbstractConsulPatternInstanceResolver.class);

    public List<InstanceDto> createOrUpdateInstances(InstanceDto parent, List<InstanceDto> childInstances) {
        
        def configuration = parent.getConfiguration()
        def services = getServices(configuration)
        def validInstanceIds = []

        def environmentIndex = (configuration["environmentIndex"] ? configuration["environmentIndex"] : '1') as int
        def applicationIndex = (configuration["applicationIndex"] ? configuration["applicationIndex"] : '2') as int
        def servicesPattern = configuration["servicesPattern"] ? configuration["servicesPattern"] : '([a-z].*)-([a-z].*)-.*'

        def instances = [];
        services.each { serviceName,tags ->
            def serviceNameMatcher = serviceName =~ servicesPattern
            if (!serviceNameMatcher.matches()){
                log.debug("ServiceName [${serviceName}] does not match servicesPattern [${servicesPattern}], skipping...")
                return
            }

            log.debug("ServiceName [${serviceName}] matches servicesPattern [${servicesPattern}], processing...")
            def environmentName = serviceNameMatcher[0][environmentIndex].toString().toUpperCase();
            def applicationName = serviceNameMatcher[0][applicationIndex].toString().toUpperCase();
            log.debug("For serviceName the environment is [${environmentName}] and the application is [${applicationName}]")

            def project = projectLogic.findOrCreateProject(applicationName);
            def environment = environmentLogic.findOrCreateEnvironment(environmentName)
            instances << createOrUpdateInstance(serviceName, project, environment, childInstances, parent)
        }
        return instances;
    }



    def createOrUpdateInstance(String reference, ProjectDto project, EnvironmentDto environment, List<InstanceDto> childInstances, InstanceDto parent){
        InstanceDto instance = childInstances.find{ InstanceDto child -> child.reference == reference}
        if (!instance){
            log.debug("No child has been found for reference [${reference}], creating new...")
            instance = instanceLogic.generateInstanceDto(getType(), project.id, environment.id)
            instance.name = reference
            instance.reference = reference
        }
        instance.refreshTimeout = Long.valueOf(parent.configuration["child_refreshTimeout"] ? parent.configuration['child_refreshTimeout'] : '60000')
        configure(instance, project, environment, parent);
        instanceLogic.createOrUpdateInstance(instance);
        return instance;
    }


    abstract String getType();

    abstract void configure(InstanceDto instance, ProjectDto project, EnvironmentDto environment, InstanceDto parent)

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
