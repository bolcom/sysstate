package nl.unionsoft.sysstate.plugins.groovy.basic

import nl.unionsoft.sysstate.common.dto.InstanceDto
import nl.unionsoft.sysstate.common.dto.StateDto
import nl.unionsoft.sysstate.common.dto.TextDto;
import nl.unionsoft.sysstate.common.enums.StateType
import nl.unionsoft.sysstate.common.logic.ResourceLogic;
import nl.unionsoft.sysstate.common.logic.TextLogic
import nl.unionsoft.sysstate.plugins.http.HttpConstants;

import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.StatusLine
import org.apache.http.client.HttpClient

import spock.lang.Specification

class XPathStateResolverSpec extends Specification{

    XPathStateResolver xPathStateResolver;

    TextLogic textLogic = Mock(TextLogic)
    
    ResourceLogic resourceLogic = Mock(ResourceLogic)
    HttpClient httpClient = Mock(HttpClient)
    HttpResponse httpResponse = Mock(HttpResponse)
    StatusLine statusLine = Mock(StatusLine)
    HttpEntity httpEntity = Mock(HttpEntity)


    void setup() {
        xPathStateResolver = new XPathStateResolver(textLogic)
        xPathStateResolver.setResourceLogic(resourceLogic)
    }

    def 'SelfDiagnose can be parsed to valid version with a given xpath'() {
        given:
        InstanceDto instance = new InstanceDto();
        instance.setConfiguration([
            "httpClientId" : "test",
            "url"          : "http://service/internal/selfdiagnose.html?format=xml",
            "xpath"        : "substring-before(substring-after(string(/selfdiagnose/results/result[@task='build information']/@message),': '),',')"
        ])

        StateDto state = new StateDto();

        when:
        xPathStateResolver.setState(instance, state)

        then:
        1 * resourceLogic.getResourceInstance(HttpConstants.RESOURCE_MANAGER_NAME, "test") >> httpClient
        1 * httpClient.execute(_) >> httpResponse
        1 * httpResponse.getStatusLine() >> statusLine
        1 * statusLine.getStatusCode() >> 200
        1 * httpResponse.getEntity() >> httpEntity
        1 * httpEntity.getContent() >> getClass().getResourceAsStream("/nl/unionsoft/sysstate/plugins/groovy/basic/selfdiagnoseresults.xml")
        state.state == StateType.STABLE
        state.description == '90.0.3.73'
    }
    
    def 'SelfDiagnose can be parsed to valid version with a predefined xpath'() {
        given:
        InstanceDto instance = new InstanceDto();
        instance.setConfiguration([
            "httpClientId"    : "test",
            "url"             : "http://service/internal/selfdiagnose.html?format=xml",
            "predefinedXpath" : "1"
        ])

        StateDto state = new StateDto();
        TextDto textDto = new TextDto(text:"substring-before(substring-after(string(/selfdiagnose/results/result[@task='build information']/@message),': '),',')")
        
        when:
        xPathStateResolver.setState(instance, state)

        then:
        1 * resourceLogic.getResourceInstance(HttpConstants.RESOURCE_MANAGER_NAME, "test") >> httpClient
        1 * httpClient.execute(_) >> httpResponse
        1 * httpResponse.getStatusLine() >> statusLine
        1 * statusLine.getStatusCode() >> 200
        1 * httpResponse.getEntity() >> httpEntity
        1 * httpEntity.getContent() >> getClass().getResourceAsStream("/nl/unionsoft/sysstate/plugins/groovy/basic/selfdiagnoseresults.xml")
        1 * textLogic.getText(1L) >> textDto
        state.state == StateType.STABLE
        state.description == '90.0.3.73'
    }

}
