package nl.unionsoft.sysstate.plugins.groovy.consul

import groovy.json.JsonSlurper

import java.util.regex.Matcher

import javax.inject.Inject

import nl.unionsoft.sysstate.common.dto.EnvironmentDto
import nl.unionsoft.sysstate.common.dto.InstanceDto
import nl.unionsoft.sysstate.common.dto.ProjectDto
import nl.unionsoft.sysstate.common.dto.ProjectEnvironmentDto
import nl.unionsoft.sysstate.common.extending.InstanceStateResolver
import nl.unionsoft.sysstate.common.logic.RelationalInstanceLogic
import nl.unionsoft.sysstate.common.logic.ResourceLogic
import nl.unionsoft.sysstate.plugins.groovy.http.JsonSlurperHttpGetCallback
import nl.unionsoft.sysstate.plugins.http.HttpConstants
import nl.unionsoft.sysstate.plugins.http.HttpGetBuilder

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
    public AbstractConsulPatternInstanceResolver(RelationalInstanceLogic relationalInstanceLogic,ResourceLogic resourceLogic) {
        super(relationalInstanceLogic)
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
        def serverUrl = configuration.get("serverUrl") ? configuration.get("serverUrl") : 'http://localhost:8500'
        def dataCenter = configuration.get("dataCenter") ? configuration.get("dataCenter") : ''

        return  new HttpGetBuilder(httpClient, "${serverUrl.trim()}/v1/catalog/services?dc=${dataCenter}")
                .withBasicAuthentication( configuration.get("userName"), configuration.get("password"))
                .execute(new JsonSlurperHttpGetCallback());
    }


    @Override
    public String generateHomePageUrl(InstanceDto instance) {
        return instance.getConfiguration().get("serverUrl");
    }
}
