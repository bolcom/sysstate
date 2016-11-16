package nl.unionsoft.sysstate.plugins.groovy.marathon


import groovy.json.JsonSlurper

import java.util.List;

import javax.inject.Inject
import javax.inject.Named

import nl.unionsoft.sysstate.common.dto.EnvironmentDto
import nl.unionsoft.sysstate.common.dto.InstanceDto
import nl.unionsoft.sysstate.common.dto.ProjectDto
import nl.unionsoft.sysstate.common.dto.StateDto
import nl.unionsoft.sysstate.common.enums.StateType
import nl.unionsoft.sysstate.common.extending.InstanceStateResolver;
import nl.unionsoft.sysstate.common.extending.StateResolver
import nl.unionsoft.sysstate.common.logic.EnvironmentLogic
import nl.unionsoft.sysstate.common.logic.InstanceLinkLogic;
import nl.unionsoft.sysstate.common.logic.InstanceLogic
import nl.unionsoft.sysstate.common.logic.ProjectLogic
import nl.unionsoft.sysstate.common.logic.ResourceLogic;

import org.apache.commons.lang.text.StrSubstitutor;
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.sun.org.apache.xpath.internal.FoundIndex;

@Named("marathonPatternInstanceResolver")
class MarathonPatternInstanceResolver extends InstanceStateResolver{

    Logger log = LoggerFactory.getLogger(MarathonPatternInstanceResolver.class);

    @Inject
    private ProjectLogic projectLogic

    @Inject
    public MarathonPatternInstanceResolver(ProjectLogic projectLogic, EnvironmentLogic environmentLogic, InstanceLogic instanceLogic, InstanceLinkLogic instanceLinkLogic) {
        super(instanceLinkLogic, instanceLogic, environmentLogic)
        this.projectLogic = projectLogic;
    }

    
    public List<InstanceDto> createOrUpdateInstances(InstanceDto parent, List<InstanceDto> childInstances){

        def properties = parent.getConfiguration()

        def idPattern = properties["idPattern"] ? properties["idPattern"] : '/([a-z]*-[0-9]*|[a-z]*)(?:.*-|/)([a-z]*)(?:/|$)(.*)'
        def environmentIndex = (properties["environmentIndex"] ? properties["environmentIndex"] : '1') as int
        def applicationIndex = (properties["applicationIndex"] ? properties["applicationIndex"] : '2') as int
        def connectTimeout = (properties["connectTimeout"] ? properties['connectTimeout'] : '5000') as int
        def readTimeout = (properties["readTimeout"] ? properties['readTimeout'] : '5000') as int

        def environmentTemplate = properties["environmentTemplate"] ? properties['environmentTemplate'] : 'use-${environmentName}'

        def serverUrl = properties["serverUrl"].toString().trim()
        def tags = properties["tags"] ? properties['tags'] : 'marathon'

        def url = new URL("${serverUrl}/v2/apps/")
        def text = url.getText(["connectTimeout" : connectTimeout, "readTimeout":readTimeout], "UTF-8")
        def result = new JsonSlurper().parseText(text)

        def validInstances = []

        result['apps'].each { app ->
            def appId = app['id']
            def idMatcher = appId =~ idPattern
            if (!idMatcher.matches()){
                return
            }

            StrSubstitutor strsub = new StrSubstitutor(["environmentName":idMatcher[0][environmentIndex].toString().toUpperCase()])
            def environmentName = strsub.replace(environmentTemplate)
            def applicationName = idMatcher[0][applicationIndex].toString().toUpperCase();
            if (environmentName && applicationName){
                log.debug("Found app [${app['id']} with environment [${environmentName}] and application [${applicationName}]")
                def project = projectLogic.findOrCreateProject(applicationName);
                def environment = environmentLogic.findOrCreateEnvironment(environmentName)
                def reference = app['id']
                def component = app['id'].toString().tokenize('/')[-1]
                log.debug("Searching for child instance with reference [${reference}]")
                InstanceDto foundChild = childInstances.find{ InstanceDto child -> child.reference == reference}
                if (foundChild){
                    log.debug("Child [${foundChild}] has been found for reference [${reference}]")
                    validInstances << foundChild
                } else {
                    log.debug("No child [${foundChild}] has been found for reference [${reference}], creating new...")
                    def childInstanceId = createInstance(reference, component, serverUrl, ['applicationPath':app['id'],'ignoreHealthStatus':'false','serverUrl':serverUrl], tags, project, environment)
                    validInstances << childInstanceId
                }
            }
        }
        return validInstances
    }

    long createInstance(def reference, def component, def serverUrl, def configuration, def tags, ProjectDto project, EnvironmentDto environment) {
        InstanceDto childInstance = instanceLogic.generateInstanceDto("marathonAppStateResolver", project.id, environment.id)
        childInstance.name = component
        childInstance.reference = reference
        childInstance.homepageUrl= serverUrl
        childInstance.configuration = configuration
        childInstance.tags = [tags, component].join(" ");
        instanceLogic.createOrUpdateInstance(childInstance)
        return childInstance
    }

    @Override
    public String generateHomePageUrl(InstanceDto instance) {
        def properties = instance.getConfiguration()
        return properties["serverUrl"].toString().trim();
    }
}


