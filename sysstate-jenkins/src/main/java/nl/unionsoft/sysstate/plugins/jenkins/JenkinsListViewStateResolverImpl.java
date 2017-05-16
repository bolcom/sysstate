package nl.unionsoft.sysstate.plugins.jenkins;

import static nl.unionsoft.sysstate.common.util.XmlUtil.getCharacterDataFromObjectWithKey;
import static nl.unionsoft.sysstate.common.util.XmlUtil.getElementWithKeyFromDocument;

import java.util.Map;

import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.plugins.http.XMLBeanStateResolverImpl;

@Named("jenkinsListViewStateResolver")
public class JenkinsListViewStateResolverImpl extends XMLBeanStateResolverImpl {

    @Override
    protected void handleXmlObject(XmlObject xmlObject, StateDto state, Map<String, String> configuration, final InstanceDto instance) {
        final Node rootNode = xmlObject.getDomNode();
        final Document document = (Document) rootNode;
        Element listView = getElementWithKeyFromDocument(document, "listView");
        StateType result = state.getState();
        NodeList nodeList = listView.getChildNodes();
        StringBuilder messageBuilder = new StringBuilder(4000);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (StringUtils.equalsIgnoreCase("job", node.getNodeName())) {
                String color = getCharacterDataFromObjectWithKey(node, "color");

                JenkinsStateType jenkinsStateType = JenkinsStateType.valueOf(StringUtils.upperCase(color));
                StateType stateType = jenkinsStateType.stateType;
                if (stateType != StateType.STABLE) {
                    messageBuilder.append(getCharacterDataFromObjectWithKey(node, "name"));
                    if (StringUtils.isNotEmpty(jenkinsStateType.message)) {
                        messageBuilder.append(" - ");
                        messageBuilder.append(jenkinsStateType.message);
                        messageBuilder.append("\n");
                    } else {
                        messageBuilder.append(" reported ");
                        messageBuilder.append(color);
                        messageBuilder.append("\n");
                    }
                }
                result = StateType.transfer(result, jenkinsStateType.stateType);
            }
        }
        state.setState(result);
        state.setDescription(result.name());
        state.appendMessage(messageBuilder.toString());

    }

    @Override
    public String processUri(final String uri) {
        return JenkinsUtils.appendApi(super.processUri(uri));
    }

}
