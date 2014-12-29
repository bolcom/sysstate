@Grapes(
    @Grab(group='org.codehaus.groovy', module='groovy-json', version='2.4.0-rc-1')
    )
import java.util.regex.Matcher;
import nl.unionsoft.sysstate.common.dto.*
import nl.unionsoft.sysstate.common.enums.StateType
import groovy.json.*

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
 * applicationPath=/someEnvironment/someApplication
 *
 */

def server = properties["server"].toString().trim()
assert server,"No server defined in properties..."

def applicationPath = properties["applicationPath"]
assert applicationPath,"No applicationPath defined in properties..."

def appResults = new JsonSlurper().parseText(new URL("${server}/v2/apps/${applicationPath}").text)
def app = appResults['app']

if (app['tasksStaged'] + app['tasksRunning'] == 0){
    state.setState(StateType.ERROR)
    state.appendMessage("No tasks found for applicationPath [${applicationPath}]")
    return
}

if (app['tasksStaged'] > 0){
    state.setState(StateType.UNSTABLE)
    state.appendMessage("Application contains one or more tasks that haven't staged yet...")
    return
}

if (app['deployments']){

    state.setState(StateType.UNSTABLE)
    state.appendMessage("Application has running/pending deployments")
    return

}
state.setState(StateType.STABLE)
