package nl.unionsoft.sysstate.plugins.groovy.marathon

import nl.unionsoft.sysstate.common.dto.EnvironmentDto
import nl.unionsoft.sysstate.common.dto.InstanceDto
import nl.unionsoft.sysstate.common.dto.ProjectDto
import nl.unionsoft.sysstate.common.dto.StateDto
import nl.unionsoft.sysstate.common.enums.StateType
import nl.unionsoft.sysstate.common.logic.EnvironmentLogic
import nl.unionsoft.sysstate.common.logic.InstanceLinkLogic
import nl.unionsoft.sysstate.common.logic.InstanceLogic
import nl.unionsoft.sysstate.common.logic.ProjectLogic
import nl.unionsoft.sysstate.common.logic.ResourceLogic
import nl.unionsoft.sysstate.common.logic.TemplateLogic
import nl.unionsoft.sysstate.plugins.http.HttpConstants

import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.StatusLine
import org.apache.http.client.HttpClient

import spock.lang.Specification

class MarathonAppInstanceResolverSpec extends Specification{

    
    MarathonAppInstanceResolver marathonAppInstanceResolver;
    
    InstanceLogic instanceLogic = Mock()
    InstanceLinkLogic instanceLinkLogic = Mock()
    ResourceLogic resourceLogic = Mock()
    TemplateLogic templateLogic = Mock()

    HttpClient httpClient = Mock(HttpClient)
    HttpResponse httpResponse = Mock(HttpResponse)
    StatusLine statusLine = Mock(StatusLine)
    HttpEntity httpEntity = Mock(HttpEntity)
    
    void setup() {
        marathonAppInstanceResolver = new MarathonAppInstanceResolver( instanceLogic,  instanceLinkLogic,  resourceLogic,  templateLogic)
    }
    
    def 'When I query marathon, I expect instances to be created.'() {
        given:
        InstanceDto instance = new InstanceDto();
        instance.setConfiguration([
            "httpClientId"        : "test",
            "serverUrl"           : "http://localhost:8500",
            "environmentTemplate" : "use-\${environmentName}",
            "idPattern"     : '/(?<environment>[a-z]*-[0-9]*|[a-z]*)(?:.*-|/)(?<application>[a-z]*)(?:/|$)(.*)'
        ])
        StateDto state = new StateDto();

        def firstProject = new ProjectDto('name' : 'WIZFIRST', 'id':1)
        def secondProject = new ProjectDto('name': 'WIZSECOND', 'id':2)
        
        def proEnvironment = new EnvironmentDto('name' : 'PRO', 'id' : 1)
        def testEnvironment = new EnvironmentDto('name' : 'TEST', 'id' : 2)
        
        when:
        marathonAppInstanceResolver.setState(instance, state)

        then:
        
        1 *  instanceLinkLogic.getInstanceLinks(instance.getId()) >> []
        
        then:
        1 * resourceLogic.getResourceInstance(HttpConstants.RESOURCE_MANAGER_NAME, "test") >> httpClient
        1 * httpClient.execute(_) >> httpResponse
        1 * httpResponse.getStatusLine() >> statusLine
        1 * statusLine.getStatusCode() >> 200
        1 * httpResponse.getEntity() >> httpEntity
        1 * httpEntity.getContent() >> getClass().getResourceAsStream("/nl/unionsoft/sysstate/plugins/groovy/consul/services.json")
        
        then:
        1 * templateLogic.writeTemplate("asdf", ['project':firstProject, 'environment' : proEnvironment], _)
        1 * templateLogic.writeTemplate("asdf", ['project':secondProject, 'environment' : testEnvironment], _)
        
        state.state == StateType.STABLE
        state.description == 'OK'
        

    }
    
    def createProject(name, id){
        return 
    }
}
