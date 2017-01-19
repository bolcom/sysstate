package nl.unionsoft.sysstate.plugins.groovy.marathon

import groovy.json.JsonSlurper

import javax.inject.Inject
import javax.inject.Named

import nl.unionsoft.sysstate.common.dto.InstanceDto
import nl.unionsoft.sysstate.common.dto.StateDto
import nl.unionsoft.sysstate.common.enums.StateType
import nl.unionsoft.sysstate.common.extending.StateResolver
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

@Named("marathonAppStateResolver")
class MarathonAppStateResolver implements StateResolver{

    private ResourceLogic resourceLogic;

    @Inject
    public MarathonAppStateResolver(ResourceLogic resourceLogic) {
        this.resourceLogic = resourceLogic;
    }
    @Override
    public void setState(InstanceDto instance, StateDto state) {

        Map<String, String> configuration = instance.getConfiguration()

        def ignoreHealthStatus = configuration.get("ignoreHealthStatus").toBoolean()

        def app = getAppResults(configuration)['app']

        if (app['tasksStaged'] + app['tasksRunning'] == 0){
            state.setState(StateType.ERROR)
            state.appendMessage("No tasks found for applicationPath [${configuration['applicationPath']}]")
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
        return instance.configuration.get("serverUrl") ;
    }

    def getAppResults(Map<String, String> configuration){
        def applicationPath = configuration.get("applicationPath");
        HttpClient httpClient = resourceLogic.getResourceInstance(HttpConstants.RESOURCE_MANAGER_NAME, StringUtils.defaultIfEmpty(configuration.get(HttpConstants.HTTP_CLIENT_ID), HttpConstants.DEFAULT_RESOURCE));

        def serverUrl = configuration.get("serverUrl")
        assert serverUrl,"No serverUrl defined."

        return  new HttpGetBuilder(httpClient, "${serverUrl}/v2/apps/${applicationPath}")
                .withBasicAuthentication( configuration.get("userName"), configuration.get("password"))
                .execute(new JsonSlurperHttpGetCallback());
    }
}
