package nl.unionsoft.sysstate.plugins.groovy.basic

import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.StatusLine
import org.apache.http.client.HttpClient

import nl.unionsoft.sysstate.common.dto.InstanceDto
import nl.unionsoft.sysstate.common.dto.StateDto
import nl.unionsoft.sysstate.common.dto.TextDto;
import nl.unionsoft.sysstate.common.enums.StateType
import nl.unionsoft.sysstate.common.logic.ResourceLogic;
import nl.unionsoft.sysstate.common.logic.TextLogic
import nl.unionsoft.sysstate.plugins.http.HttpConstants;
import spock.lang.Specification

class JPathStateResolverSpec extends Specification{

    JPathStateResolver jPathStateResolver;

    TextLogic textLogic = Mock(TextLogic)
    
    ResourceLogic resourceLogic = Mock(ResourceLogic)
    HttpClient httpClient = Mock(HttpClient)
    HttpResponse httpResponse = Mock(HttpResponse)
    StatusLine statusLine = Mock(StatusLine)
    HttpEntity httpEntity = Mock(HttpEntity)


    void setup() {
        jPathStateResolver = new JPathStateResolver(textLogic)
        jPathStateResolver.setResourceLogic(resourceLogic)
    }

    def 'SelfDiagnose can be parsed to valid version with a given jpath'() {
        given:
        InstanceDto instance = new InstanceDto();
        instance.setConfiguration([
            "httpClientId" : "test",
            "url"          : "http://service/internal/selfdiagnose.html?format=xml",
            "jpath"        : '$.build.version'
        ])

        StateDto state = new StateDto();

        when:
        jPathStateResolver.setState(instance, state)

        then:
        1 * resourceLogic.getResourceInstance(HttpConstants.RESOURCE_MANAGER_NAME, "test") >> httpClient
        1 * httpClient.execute(_) >> httpResponse
        1 * httpResponse.getStatusLine() >> statusLine
        1 * statusLine.getStatusCode() >> 200
        1 * httpResponse.getEntity() >> httpEntity
        1 * httpEntity.getContent() >> getClass().getResourceAsStream("/nl/unionsoft/sysstate/plugins/groovy/basic/jsonresult.json")
        state.state == StateType.STABLE
        state.description == '110.0.7.85'
    }
    
    def 'SelfDiagnose can be parsed to valid version with a predefined jpath'() {
        given:
        InstanceDto instance = new InstanceDto();
        instance.setConfiguration([
            "httpClientId"    : "test",
            "url"             : "http://service/internal/selfdiagnose.html?format=xml",
            "predefinedJpath" : "1"
        ])

        StateDto state = new StateDto();
        TextDto textDto = new TextDto(text:'$.build.version')
        
        when:
        jPathStateResolver.setState(instance, state)

        then:
        1 * resourceLogic.getResourceInstance(HttpConstants.RESOURCE_MANAGER_NAME, "test") >> httpClient
        1 * httpClient.execute(_) >> httpResponse
        1 * httpResponse.getStatusLine() >> statusLine
        1 * statusLine.getStatusCode() >> 200
        1 * httpResponse.getEntity() >> httpEntity
        1 * httpEntity.getContent() >> getClass().getResourceAsStream("/nl/unionsoft/sysstate/plugins/groovy/basic/jsonresult.json")
        1 * textLogic.getText("1") >> Optional.of(textDto)
        
        println(state.getMessage())
        state.state == StateType.STABLE
        state.description == '110.0.7.85'
    }

}
