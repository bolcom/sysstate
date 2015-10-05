package nl.unionsoft.sysstate.plugins.groovy.marathon

import groovy.json.JsonSlurper

import javax.inject.Named

import nl.unionsoft.sysstate.common.dto.InstanceDto
import nl.unionsoft.sysstate.common.dto.StateDto
import nl.unionsoft.sysstate.common.enums.StateType
import nl.unionsoft.sysstate.common.extending.StateResolver

@Named("marathonAppStateResolver")
class MarathonAppStateResolver implements StateResolver{

    @Override
    public void setState(InstanceDto instance, StateDto state) {

        Map<String, String> configuration = instance.getConfiguration()

        def applicationPath = configuration.get("applicationPath");
        def ignoreHealthStatus = configuration.get("ignoreHealthStatus").toBoolean()
        def serverUrl = configuration.get("serverUrl").trim()
        
        def connectTimeout = (properties["connectTimeout"] ? properties['connectTimeout'] : '5000') as int
        def readTimeout = (properties["readTimeout"] ? properties['readTimeout'] : '5000') as int

        assert applicationPath,"No applicationPath defined in properties..."
        assert serverUrl,"No server defined in properties..."

        def appResults = new JsonSlurper().parseText(new URL("${serverUrl}/v2/apps${applicationPath}").getText(["connectTimeout" : connectTimeout, "readTimeout":readTimeout], "UTF-8"))
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

        if (app["tasksUnhealthy"] > 0){
            state.setState(StateType.UNSTABLE)
            state.appendMessage("Application has [${app['tasksUnhealthy']}] unhealthy tasks.")
            return
        }

        if (app["healthChecks"].size == 0 && !ignoreHealthStatus){
            state.setState(StateType.UNSTABLE)
            state.appendMessage("Application does not have any healthchecks defined. Set property 'ignoreHealthStatus' to true to ignore this.")
            return
        }

        state.setState(StateType.STABLE)
    }

    @Override
    public String generateHomePageUrl(InstanceDto instance) {
        // TODO Auto-generated method stub
        return serverUrl;
    }
}
