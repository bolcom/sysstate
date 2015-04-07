package nl.unionsoft.sysstate.plugins.jenkins;

import static nl.unionsoft.sysstate.common.util.XmlUtil.getCharacterDataFromObjectWithKey;

import java.util.Map;

import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.plugins.http.XMLBeanStateResolverImpl;

import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.XmlObject;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

@Service("jenkinsJobStateResolver")
public class JenkinsJobStateResolverImpl extends XMLBeanStateResolverImpl {

    @Override
    protected void handleXmlObject(final XmlObject xmlObject, final StateDto state, final Map<String, String> properties, final InstanceDto instance) {
        final Node node = xmlObject.getDomNode();
        final Document document = (Document) node;

        final String color = getCharacterDataFromObjectWithKey(document, "color");
        JenkinsStateType jenkinsStateType = JenkinsStateType.valueOf(StringUtils.upperCase(color));
        StateType stateType = jenkinsStateType.stateType;

        state.setState(stateType);
        state.setDescription(jenkinsStateType.description);
        state.appendMessage(jenkinsStateType.message);
    }

    @Override
    public String processUri(final String uri) {
        return JenkinsUtils.appendApi(super.processUri(uri));
    }

}
