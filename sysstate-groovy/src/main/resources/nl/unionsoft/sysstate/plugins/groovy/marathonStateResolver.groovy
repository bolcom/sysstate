@Grab(group='com.github.mohitsoni', module='marathon-client', version='0.4.1')
import java.util.regex.Matcher;
import nl.unionsoft.sysstate.common.dto.*
import nl.unionsoft.sysstate.common.enums.StateType
import mesosphere.marathon.client.Marathon
import mesosphere.marathon.client.MarathonClient
import org.springframework.context.ApplicationContext
import nl.unionsoft.sysstate.common.logic.InstanceLogic
import nl.unionsoft.sysstate.common.logic.EnvironmentLogic
import nl.unionsoft.sysstate.common.logic.ProjectLogic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import groovy.text.SimpleTemplateEngine


/**
 * Example binding properties
 * server=http://marathon.dev.mycompany.com
 * groupMatcherPattern=/([a-z]*-[0-9]*)/(.*)
 * urlConstructTemplate=http://${projectName.toLowerCase()}.${environmentName.toLowerCase()}.cd.dev.mycompany.com/internal/selfdiagnose.html
 * environmentIndex=1
 * projectIndex=2
 * 
 */
Logger log = LoggerFactory.getLogger(this.class);

StateDto state = binding.getVariable("state")
state.setState(StateType.UNSTABLE)

def properties = binding.getVariable("properties")

def groupMatcherPattern = properties["groupMatcherPattern"]
def urlConstructTemplate = properties["urlConstructTemplate"]
def environmentIndex = properties["environmentIndex"] as int
def projectIndex = properties["projectIndex"] as int

Marathon marathon = MarathonClient.getInstance(properties["server"])

def identifier = properties["tag"]
if (!identifier){
    identifier = "marathon"
}

ApplicationContext applicationContext = binding.getVariable("applicationContext")
InstanceLogic instanceLogic = applicationContext.getBean(InstanceLogic.class)
ProjectLogic projectLogic = applicationContext.getBean(ProjectLogic.class)
EnvironmentLogic environmentLogic = applicationContext.getBean(EnvironmentLogic.class)


def apps = marathon.getApps()["apps"];
apps.each { app ->
    
    def appId = app["id"]
    Matcher matcher = appId =~ groupMatcherPattern 
    log.info("Validating environment for app with id [${appId}]")
    
    if (matcher.matches()){
        def environmentName = matcher[0][environmentIndex].toUpperCase();
        def projectName = matcher[0][projectIndex].toUpperCase();
        def project = projectLogic.findProject(projectName)
        if (!project){
            log.info("There's no project defined for projectName [${projectName}], creating it...")
            project = new ProjectDto()
            project.name = projectName
            project.tags = identifier
            projectLogic.createOrUpdateProject(project)
        }
        
        def environment = environmentLogic.findEnvironment(environmentName)
        if (!environment){
            log.info("There's no environment defined for environmentName [${environmentName}], creating it...")
            environment = new EnvironmentDto()
            environment.name = environmentName
            environment.tags = identifier
            environmentLogic.createOrUpdate(environment)
        }
        
        List<InstanceDto> instances = instanceLogic.getInstancesForProjectAndEnvironment(projectName, environmentName)
        
        if (!instances){
            log.info("No instances found! Creating...")
            InstanceDto instance = instanceLogic.generateInstanceDto("selfDiagnoseStateResolver", project.id, environment.id)
            instance.name = app["id"]
            instance.tags = identifier
            
            SimpleTemplateEngine engine = new SimpleTemplateEngine()
            
            def templateBinding = ["projectName":projectName, "environmentName":environmentName]
            def template = engine.createTemplate(urlConstructTemplate).make(templateBinding)
            
            instance.homepageUrl=template.toString()
            instance.configuration = ['url':template.toString(),'pattern':'Maven POM properties']
            instanceLogic.createOrUpdateInstance(instance)
        } else {
            log.info("One or more instances already configured. Skipping...")
        }
    } else {
        log.info("appId [${appId}] does not match given groupMatcherPattern [${groupMatcherPattern}]. Skipping...")
    }
}

log.info("Cleaning up no longer used environments which match the given identifier [${identifier}]")
def environments = environmentLogic.getEnvironments().findAll{it.tags?.equalsIgnoreCase(identifier)}
environments.each { environment ->
    def appEnvironmentPrefix ="/${environment.name.toLowerCase()}/"
    if (!apps.find{app -> app["id"].startsWith(appEnvironmentPrefix)}){
       log.info("Removing environment [${environment.name}] as it is no longer used...")
       environmentLogic.delete(environment.id)
    }
}
log.info("All done!")
state.setState(StateType.STABLE)


