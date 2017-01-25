package nl.unionsoft.sysstate.plugins.groovy.marathon

import groovy.json.JsonSlurper
import nl.unionsoft.sysstate.common.dto.EnvironmentDto
import nl.unionsoft.sysstate.common.dto.InstanceDto
import nl.unionsoft.sysstate.common.dto.ProjectDto
import nl.unionsoft.sysstate.common.dto.ProjectEnvironmentDto
import nl.unionsoft.sysstate.common.extending.InstanceStateResolver
import nl.unionsoft.sysstate.common.logic.RelationalInstanceLogic
import nl.unionsoft.sysstate.common.logic.ResourceLogic
import nl.unionsoft.sysstate.common.logic.TemplateLogic
import nl.unionsoft.sysstate.plugins.groovy.http.JsonSlurperHttpGetCallback;
import nl.unionsoft.sysstate.plugins.http.HttpClientCallback;
import nl.unionsoft.sysstate.plugins.http.HttpClientLovResolver;
import nl.unionsoft.sysstate.plugins.http.HttpConstants
import nl.unionsoft.sysstate.plugins.http.HttpGetBuilder;

import org.apache.commons.lang.StringUtils
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.StatusLine
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.auth.BasicScheme
import org.apache.http.util.EntityUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class AbstractMarathonPatternInstanceResolver extends InstanceStateResolver{

    private final static Logger logger = LoggerFactory.getLogger(AbstractMarathonPatternInstanceResolver.class);

    private final ResourceLogic resourceLogic
    private final TemplateLogic templateLogic;

    public AbstractMarathonPatternInstanceResolver(RelationalInstanceLogic relationalInstanceLogic,ResourceLogic resourceLogic, TemplateLogic templateLogic) {
        super(relationalInstanceLogic)
        this.resourceLogic = resourceLogic;
        this.templateLogic = templateLogic;
    }

    @Override
    public List<InstanceDto> generateInstances(InstanceDto parent) {
        def properties = parent.getConfiguration()

        def idPattern = properties["idPattern"]
        assert idPattern, "no idPattern defined"

        def apps = getApps(properties)
        def instances = [];
        apps['apps'].each { app ->
            def appId = app['id']
            def idMatcher = appId =~ idPattern
            if (!idMatcher.matches()){
                return
            }

            logger.debug("Application ID [${appId}] matches idPattern [${idPattern}], processing...")
            def environmentName = getEnvironment(idMatcher.group('environment').toString().toUpperCase(), parent);
            def applicationName = idMatcher.group('application').toString().toUpperCase();


            def project = new ProjectDto(name:applicationName);
            def environment = new EnvironmentDto(name:environmentName)
            instances << createInstance(app, project, environment, parent)
        }
        return instances
    }

    protected String getEnvironment(def environmentName, def InstanceDto parent) {
        def environmentTemplate = parent.configuration['environmentTemplate']
        assert environmentTemplate, "No environmentTemplate defined"
        StringWriter stringWriter = new StringWriter();
        templateLogic.writeTemplate(environmentTemplate, ['environmentName':environmentName], stringWriter)
        return stringWriter.toString()
    }

    def createInstance(def app, ProjectDto project, EnvironmentDto environment, InstanceDto parent){
        ProjectEnvironmentDto projectEnvironment = new ProjectEnvironmentDto(project: project, environment: environment);
        InstanceDto instance = new InstanceDto(
                name: app['id'],
                reference: app['id'],
                projectEnvironment : projectEnvironment,
                enabled:true);
        configure(app, instance, parent);
        return instance;
    }

    abstract void configure(def app, InstanceDto instance, InstanceDto parent)

    def getApps(Map<String, String> configuration){
        HttpClient httpClient = resourceLogic.getResourceInstance(HttpConstants.RESOURCE_MANAGER_NAME, StringUtils.defaultIfEmpty(configuration.get(HttpConstants.HTTP_CLIENT_ID), HttpConstants.DEFAULT_RESOURCE));
        def serverUrl = configuration.get("serverUrl")
        assert serverUrl,"ServerUrl is missing"
        return new HttpGetBuilder(httpClient, "${serverUrl.trim()}/v2/apps/")
        .withBasicAuthentication( configuration.get("userName"), configuration.get("password"))
        .execute(new JsonSlurperHttpGetCallback());

    }

    @Override
    public String generateHomePageUrl(InstanceDto instance) {
        return instance.getConfiguration().get("serverUrl");
    }
}
