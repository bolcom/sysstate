package nl.unionsoft.sysstate.plugins.jenkins;

import static nl.unionsoft.sysstate.common.util.XmlUtil.getCharacterDataFromObjectWithKey;

import java.util.Properties;

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

    private static final String API_XML = "/api/xml";

    @Override
    protected void handleXmlObject(final XmlObject xmlObject, final StateDto state, final Properties properties) {
        final Node node = xmlObject.getDomNode();
        final Document document = (Document) node;

        final String color = getCharacterDataFromObjectWithKey(document, "color");
        if (StringUtils.startsWithIgnoreCase(color, "green") || StringUtils.startsWithIgnoreCase(color, "blue")) {
            state.setDescription("STABLE");
            state.setState(StateType.STABLE);
        } else if (StringUtils.startsWithIgnoreCase(color, "yellow")) {
            state.setDescription("UNSTABLE");
            state.setState(StateType.UNSTABLE);
        } else if (StringUtils.startsWithIgnoreCase(color, "red") || StringUtils.startsWithIgnoreCase(color, "aborted")) {
            state.setDescription("FAILED");
            state.setState(StateType.ERROR);
        } else if (StringUtils.startsWithIgnoreCase(color, "disabled")) {
            state.setState(StateType.DISABLED);
            state.setDescription("DISABLED");
            state.setMessage("Project is disabled, visit project homepage for more information!");
        } else if (StringUtils.startsWithIgnoreCase(color, "grey")) {
            state.setState(StateType.PENDING);
            state.setDescription("PENDING");
            state.setMessage("Project is awaiting (first) build...");
        }
    }

    @Override
    public String processUri(final String uri) {
        final StringBuilder uriBuilder = new StringBuilder();
        uriBuilder.append(super.processUri(uri));
        if (!StringUtils.endsWith(uri, API_XML)) {
            uriBuilder.append(API_XML);
        }
        return uriBuilder.toString();
    }

}
