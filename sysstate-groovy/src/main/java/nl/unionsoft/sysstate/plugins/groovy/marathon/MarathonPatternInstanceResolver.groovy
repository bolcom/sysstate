package nl.unionsoft.sysstate.plugins.groovy.marathon


import groovy.json.JsonSlurper

import javax.inject.Inject
import javax.inject.Named

import nl.unionsoft.sysstate.common.dto.EnvironmentDto
import nl.unionsoft.sysstate.common.dto.InstanceDto
import nl.unionsoft.sysstate.common.dto.InstanceLinkDto
import nl.unionsoft.sysstate.common.dto.InstanceLinkDto.Direction;
import nl.unionsoft.sysstate.common.dto.ProjectDto
import nl.unionsoft.sysstate.common.dto.StateDto
import nl.unionsoft.sysstate.common.enums.StateType
import nl.unionsoft.sysstate.common.extending.StateResolver
import nl.unionsoft.sysstate.common.logic.EnvironmentLogic
import nl.unionsoft.sysstate.common.logic.InstanceLinkLogic
import nl.unionsoft.sysstate.common.logic.InstanceLogic
import nl.unionsoft.sysstate.common.logic.ProjectLogic

import org.apache.commons.lang.text.StrSubstitutor;
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.sun.org.apache.xpath.internal.FoundIndex;

@Named("marathonPatternInstanceResolver")
class MarathonPatternInstanceResolver implements StateResolver{

	Logger log = LoggerFactory.getLogger(MarathonPatternInstanceResolver.class);

	@Inject
	private InstanceLogic instanceLogic

	@Inject
	private ProjectLogic projectLogic

	@Inject
	private EnvironmentLogic environmentLogic

	@Inject
	private InstanceLinkLogic instanceLinkLogic

	@Override
	public void setState(InstanceDto parent, StateDto state) {


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

		def validInstanceIds = []

		def childInstances = instanceLinkLogic.getInstanceLinks(parent.id)
				.findAll{ il -> il.direction == Direction.OUTGOING && il.name == "child"}
				.collect{ InstanceLinkDto instanceLink -> instanceLogic.getInstance(instanceLink.instanceToId) }
				.findAll { Optional<InstanceDto> optInstance -> optInstance.isPresent()}
				.collect{ Optional<InstanceDto> optInstance -> optInstance.get()}

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
				def project = findOrCreateProject(applicationName);
				def environment = findOrCreateEnvironment(environmentName)
				def reference = app['id']
				def component = app['id'].toString().tokenize('/')[-1]
				log.debug("Searching for child instance with reference [${reference}]")
				InstanceDto foundChild = childInstances.find{ InstanceDto child -> child.reference == reference}
				if (foundChild){
					log.debug("Child [${foundChild}] has been found for reference [${reference}]")
					validInstanceIds += foundChild.id
				} else {
					log.debug("No child [${foundChild}] has been found for reference [${reference}], creating new...")
					def childInstanceId = createInstance(reference, component, serverUrl, ['applicationPath':app['id'],'ignoreHealthStatus':'false','serverUrl':serverUrl], tags, project, environment)
					instanceLinkLogic.link(parent.id,childInstanceId, "child")
					validInstanceIds << childInstanceId
				}
			}
		}

		deleteNoLongerValidInstances(validInstanceIds, childInstances)
		deleteEnvironmentsWithtoutInstances()
		state.setState(StateType.STABLE)
	}

	long createInstance(def reference, def component, def serverUrl, def configuration, def tags, ProjectDto project, EnvironmentDto environment) {
		InstanceDto childInstance = instanceLogic.generateInstanceDto("marathonAppStateResolver", project.id, environment.id)
		childInstance.name = component
		childInstance.reference = reference
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

	def deleteNoLongerValidInstances(List validInstanceIds, List<InstanceDto> children) {
		log.info("Removing children that are not in the list of validInstanceIds...")
		children.each { InstanceDto i ->
			if (!validInstanceIds.contains(i.id)){
				log.info("Deleting instance with id [${i.id}] sinze it is no longer used.")
				instanceLogic.delete(i.id)
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
}


