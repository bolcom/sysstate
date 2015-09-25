package nl.unionsoft.sysstate.plugins.groovy.marathon

import javax.inject.Named;

import groovy.json.JsonSlurper
import nl.unionsoft.sysstate.common.annotations.ParameterDefinition;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.common.extending.StateResolver;
@Named
class MarathonAppStateResolver implements StateResolver{

    @ParameterDefinition(title = "Server Url")
    String serverUrl

    @ParameterDefinition(title = "Application Path")
    String applicationPath

    @ParameterDefinition(title = "Ignore HealthStatus")
    boolean ignoreHealthStatus


    @Override
    public void setState(InstanceDto instance, StateDto state) {

        assert applicationPath,"No applicationPath defined in properties..."
        assert serverUrl,"No server defined in properties..."

        def appResults = new JsonSlurper().parseText(new URL("${serverUrl}/v2/apps${applicationPath}").getText(["connectTimeout" : 5000, "readTimeout":5000], "UTF-8"))
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
