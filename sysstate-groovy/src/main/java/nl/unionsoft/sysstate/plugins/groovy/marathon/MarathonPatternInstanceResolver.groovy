package nl.unionsoft.sysstate.plugins.groovy.marathon


import groovy.json.JsonSlurper

import javax.inject.Inject
import javax.inject.Named


import nl.unionsoft.sysstate.common.annotations.ParameterDefinition
import nl.unionsoft.sysstate.common.dto.EnvironmentDto
import nl.unionsoft.sysstate.common.dto.InstanceDto
import nl.unionsoft.sysstate.common.dto.InstanceLinkDto
import nl.unionsoft.sysstate.common.dto.ProjectDto
import nl.unionsoft.sysstate.common.dto.StateDto
import nl.unionsoft.sysstate.common.enums.StateType
import nl.unionsoft.sysstate.common.extending.AnnotationsPluginDescriptor
import nl.unionsoft.sysstate.common.extending.DescriptedStateResolver
import nl.unionsoft.sysstate.common.logic.EnvironmentLogic
import nl.unionsoft.sysstate.common.logic.InstanceLinkLogic
import nl.unionsoft.sysstate.common.logic.InstanceLogic
import nl.unionsoft.sysstate.common.logic.ListOfValueLogic;
import nl.unionsoft.sysstate.common.logic.ProjectLogic

import org.apache.commons.lang.text.StrSubstitutor
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Named("marathonPatternInstanceResolver")
class MarathonPatternInstanceResolver extends DescriptedStateResolver<MarathonPatternInstanceResolverDescriptor>{

    Logger log = LoggerFactory.getLogger(MarathonPatternInstanceResolver.class);

    @Inject
    private InstanceLogic instanceLogic

    @Inject
    private ProjectLogic projectLogic

    @Inject
    private EnvironmentLogic environmentLogic

    @Inject
    private InstanceLinkLogic instanceLinkLogic

    @Inject
    private ListOfValueLogic listOfValueLogic

    @Override
    public void setState(InstanceDto parent, StateDto state, MarathonPatternInstanceResolverDescriptor descriptor) {

        def serverUrl = descriptor.serverUrl.toString().trim()

        def url = new URL("${serverUrl}/v2/apps/")
        def text = url.getText(["connectTimeout" : descriptor.connectTimeout, "readTimeout":descriptor.readTimeout], "UTF-8")
        def result = new JsonSlurper().parseText(text)

        def validInstanceIds = []
        result['apps'].each { app ->

            def appId = app['id']
            def idMatcher = appId =~ descriptor.idPattern
            if (!idMatcher.matches()){
                return
            }

            StrSubstitutor strsub = new StrSubstitutor(["environmentName":idMatcher[0][descriptor.environmentIndex].toString().toUpperCase()])
            def environmentName = strsub.replace(descriptor.environmentTemplate)
            def applicationName = idMatcher[0][descriptor.applicationIndex].toString().toUpperCase();
            if (environmentName && applicationName){
                log.info("Found app [${app['id']} with environment [${environmentName}] and application [${applicationName}]")
                def project = findOrCreateProject(applicationName);
                def environment = findOrCreateEnvironment(environmentName)
                def component = app['id'].toString().tokenize('/')[-1]
                log.info("Searching for instance with component [${component}] for applicationName [${applicationName}] and environmentName [${environmentName}]")
                def projectEnvironmentInstances = instanceLogic.getInstancesForProjectAndEnvironment(applicationName, environmentName)
                def componentInstances = projectEnvironmentInstances.findAll{InstanceDto i -> i.tags.contains(component)}
                if (componentInstances){
                    log.info("Instance with component [${component}], applicationName [${applicationName}] and environmentName [${environmentName}] is already configured.")
                    validInstanceIds += componentInstances.collect{InstanceDto i -> i.id}
                } else {
                    log.info("No instances found for application [${applicationName}], environment [${environmentName}] and component [${component}]! Creating one...")
                    def childInstanceId = createInstance(component, serverUrl, ['applicationPath':app['id'],'ignoreHealthStatus':'false','serverUrl':serverUrl], descriptor.tags, project, environment)
                    instanceLinkLogic.link(parent.id,childInstanceId, "child")
                    validInstanceIds << childInstanceId
                }
            }
        }

        deleteNoLongerValidInstances(validInstanceIds, parent)
        deleteEnvironmentsWithtoutInstances()
        state.setState(StateType.STABLE)
    }

    long createInstance(def component, def serverUrl, def configuration, def tags, ProjectDto project, EnvironmentDto environment) {
        InstanceDto childInstance = instanceLogic.generateInstanceDto("marathonAppStateResolver", project.id, environment.id)
        childInstance.name = component
        childInstance.homepageUrl= serverUrl
        childInstance.configuration = configuration
        childInstance.tags = [tags, component].join(" ");
        instanceLogic.createOrUpdateInstance(childInstance)
        return childInstance.id
    }



    def deleteEnvironmentsWithtoutInstances(){
        log.info("Deleting Environments without instances...")
        environmentLogic.getEnvironments().each{ environment ->
            if (!instanceLogic.getInstancesForEnvironment(environment.id)){
                log.info("Deleting environment with id [${environment.id}] since it is no longer used.")
                environmentLogic.delete(environment.id)
            }
        }
    }

    def deleteNoLongerValidInstances(List validInstanceIds, InstanceDto parentInstance) {
        log.info("Deleting linked instances that are not in the validInstanceIds list [${validInstanceIds}]")
        parentInstance.getOutgoingInstanceLinks().findAll{ it.name == 'child'}.each { InstanceLinkDto instanceLink ->
            def instanceId = instanceLink.instanceToId
            if (!validInstanceIds.contains(instanceId)){
                log.info("Deleting instance with id [${instanceId}] sinze it is no longer used.")
                instanceLogic.delete(instanceId)
            }
        }
    }



    def findOrCreateProject(String projectName) {

        def project = projectLogic.getProjectByName(projectName)
        if (!project){
            log.info("There's no project defined for projectName [${projectName}], creating it...")
            project = new ProjectDto()
            project.name = projectName
            projectLogic.createOrUpdateProject(project)
        }
        return project;
    }

    def findOrCreateEnvironment(String environmentName) {
        def environment =environmentLogic.getEnvironmentByName(environmentName)
        if (!environment){
            log.info("There's no environment defined for environmentName [${environmentName}], creating it...")
            environment = new EnvironmentDto()
            environment.name = environmentName
            environmentLogic.createOrUpdate(environment)
        }
        return environment
    }


    @Override
    public String generateHomePageUrl(InstanceDto instance) {
        def properties = instance.getConfiguration()
        return properties["serverUrl"].toString().trim();
    }


    @Override
    public MarathonPatternInstanceResolverDescriptor createPluginDescriptor() {
        return new MarathonPatternInstanceResolverDescriptor(listOfValueLogic);
    }


    public static class MarathonPatternInstanceResolverDescriptor extends AnnotationsPluginDescriptor{

        
        public MarathonPatternInstanceResolverDescriptor(ListOfValueLogic listOfValueLogic){
            super(listOfValueLogic);
        }
        
        @ParameterDefinition(title = "ID Grouping Pattern",
        description = "The pattern used find groups for application and environment",
        defaultValue = '/([a-z]*-[0-9]*|[a-z]*)(?:.*-|/)([a-z]*)(?:/|$)(.*)')
        String idPattern;

        @ParameterDefinition(title = "Environment Index",
        description = "The index to use from the pattern to define the environment",
        defaultValue = '1')
        Integer environmentIndex;

        @ParameterDefinition(title= "Application Index",
        description = "The index to use from the pattern to define the application",
        defaultValue = '2')
        Integer applicationIndex;

        @ParameterDefinition(title = "Connection Timeout",
        description = "The time to wait before the connection will time out",
        defaultValue = '5000')
        Long connectTimeout;

        @ParameterDefinition(title ="Read Timeout",
        description = "The time to wait before the read will time out",
        defaultValue = '5000')
        Long readTimeout;

        @ParameterDefinition(title = "Environment Template",
        description = "The template used the parse the environmentName.",
        defaultValue = 'use-${environmentName}')
        String environmentTemplate;

        @ParameterDefinition(title="ServerUrl",
        description = "The location of your Marathon Server",
        defaultValue = 'http://marathon.myorganization.com')
        String serverUrl;

        @ParameterDefinition(title="Tags",
        description = "These tags will be set on each found instance so that you can filter on them later on",
        defaultValue = 'marathon')
        String tags;
    }
}




