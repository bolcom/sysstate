@Grapes(
@Grab(group='org.codehaus.groovy', module='groovy-json', version='2.4.0-rc-1')
)


import java.util.regex.Matcher;
import nl.unionsoft.sysstate.common.dto.*
import nl.unionsoft.sysstate.common.enums.StateType
import org.springframework.context.ApplicationContext
import groovy.json.*

import nl.unionsoft.sysstate.common.logic.InstanceLinkLogic
import nl.unionsoft.sysstate.common.logic.InstanceLogic
import nl.unionsoft.sysstate.common.logic.EnvironmentLogic
import nl.unionsoft.sysstate.common.logic.ProjectLogic
import org.slf4j.Logger
import org.slf4j.LoggerFactory


import groovy.text.SimpleTemplateEngine


/**
 * Example binding properties
 * server=http://marathon.dev.mycompany.com
 * applicationLabel=application
 * environmentLabel=environment
 * tags=marathon
 * urlConstructTemplate=http://${projectName.toLowerCase()}.${environmentName.toLowerCase()}.cd.dev.mycompany.com/internal/selfdiagnose.html
 * 
 */


StateDto state = binding.getVariable("state")
state.setState(StateType.UNSTABLE)

def properties = binding.getVariable("properties")
InstanceDto instance = binding.getVariable("instance")

def applicationLabel = properties["applicationLabel"] ? properties["applicationLabel"] : 'application'
def environmentLabel = properties["environmentLabel"] ? properties["environmentLabel"] : 'environment'

ApplicationContext applicationContext = binding.getVariable("applicationContext")

def marathonInstanceSync = new MarathonInstanceSync(
        serverUrl : properties["server"].toString().trim(),
        urlConstructTemplate : properties["urlConstructTemplate"], 
        parentInstance : binding.getVariable("instance"),
        tags : properties["tags"] ? properties['tags'] : 'marathon',
        projectLogic : applicationContext.getBean(ProjectLogic.class),
        environmentLogic: applicationContext.getBean(EnvironmentLogic.class),
        instanceLogic : applicationContext.getBean(InstanceLogic.class),
        instanceLinkLogic : applicationContext.getBean(InstanceLinkLogic.class))

marathonInstanceSync.sync();

class MarathonInstanceSync{

    Logger log = LoggerFactory.getLogger(MarathonInstanceSync.class);

    def serverUrl;
    def urlConstructTemplate;
    def tags;
    
    InstanceDto parentInstance;

    ProjectLogic projectLogic;
    EnvironmentLogic environmentLogic;
    InstanceLogic instanceLogic;
    InstanceLinkLogic instanceLinkLogic;
    
    def sync() {
        def result = new JsonSlurper().parseText(new URL("${serverUrl}/v2/apps/").text)
        
        def validInstanceIds = []
        result['apps'].each { app ->
            def environmentName = app['labels']['environment']?.toString()?.toUpperCase();
            def applicationName = app['labels']['application']?.toString()?.toUpperCase();
            if (environmentName && applicationName){
                log.info("Found app [${app['id']} with environment [${environmentName}] and application [${applicationName}]")
                createProjectIfNotExists(applicationName);
                createEnvironmentIfNotExists(environmentName)
                def component = app['id'].toString().tokenize('/')[-1]
                log.info("Searching for instance with component [${component}] for applicationName [${applicationName}] and environmentName [${environmentName}]")
                def projectEnvironmentInstances = instanceLogic.getInstancesForProjectAndEnvironment(applicationName, environmentName)
                def componentInstances = projectEnvironmentInstances.findAll{InstanceDto i -> i.tags.contains(component)} 
                if (componentInstances){
                    log.info("Instance with component [${component}], applicationName [${applicationName}] and environmentName [${environmentName}] is already configured.")
                    validInstanceIds += componentInstances.collect{InstanceDto i -> i.id}
                } else {
                    log.info("No instances found for application [${applicationName}], environment [${environmentName}] and component [${component}]! Creating one...")
                    validInstanceIds << createInstance(applicationName, environmentName, component)
                }
            }
        }
        
        deleteNoLongerValidInstances(validInstanceIds)
        deleteEnvironmentsWithtoutInstances()
        
        
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
    
    def deleteNoLongerValidInstances(def validInstanceIds) {
        log.info("Deleting linked instances that are not in the validInstanceIds list [${validInstanceIds}]")
        parentInstance.getOutgoingInstanceLinks().findAll{ it.name == 'child'}.each { InstanceLinkDto instanceLink ->
            def instanceId = instanceLink.instanceToId
            if (!validInstanceIds.contains(instanceId)){
                log.info("Deleting instance with id [${instanceId}] sinze it is no longer used.")
                instanceLogic.delete(instanceId)
            }
        }
    }

    long createInstance(String applicationName, String environmentName, String component) {
        ProjectDto project = projectLogic.getProjectByName(applicationName);
        EnvironmentDto environment = environmentLogic.getEnvironmentByName(environmentName);
        InstanceDto childInstance = instanceLogic.generateInstanceDto("selfDiagnoseStateResolver", project.id, environment.id)
        childInstance.name = component
        SimpleTemplateEngine engine = new SimpleTemplateEngine()
        def templateBinding = ["projectName":component, "environmentName":environmentName]
        def template = engine.createTemplate(urlConstructTemplate).make(templateBinding)
        childInstance.homepageUrl=template.toString()
        childInstance.configuration = ['url':template.toString(),'pattern':'Maven POM properties']
        childInstance.tags = [tags, component].join(" ");
        instanceLogic.createOrUpdateInstance(childInstance)
        instanceLinkLogic.link(parentInstance.id, childInstance.id, "child")
        return childInstance.id
    }

    def createProjectIfNotExists(String projectName) {
        if (!projectLogic.getProjectByName(projectName)){
            log.info("There's no project defined for projectName [${projectName}], creating it...")
            def project = new ProjectDto()
            project.name = projectName
            projectLogic.createOrUpdateProject(project)
        }
    }

    def createEnvironmentIfNotExists(String environmentName) {
        if (!environmentLogic.getEnvironmentByName(environmentName)){
            log.info("There's no environment defined for environmentName [${environmentName}], creating it...")
            def environment = new EnvironmentDto()
            environment.name = environmentName
            environmentLogic.createOrUpdate(environment)
        }
    }
}

state.setState(StateType.STABLE)


