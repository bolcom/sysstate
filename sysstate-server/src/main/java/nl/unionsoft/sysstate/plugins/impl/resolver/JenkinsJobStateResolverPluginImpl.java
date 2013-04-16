package nl.unionsoft.sysstate.plugins.impl.resolver;

import static nl.unionsoft.sysstate.util.XmlUtil.getCharacterDataFromObjectWithKey;

import java.util.Properties;

import net.xeoh.plugins.base.annotations.Capabilities;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.common.stateresolver.impl.XMLBeanStateResolverPluginImpl;

import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

@PluginImplementation
public class JenkinsJobStateResolverPluginImpl extends XMLBeanStateResolverPluginImpl {

    private static final String API_XML = "/api/xml";

    @Override
    protected void handleXmlObject(XmlObject xmlObject, StateDto state, Properties properties) {
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
    public String processUri(String uri) {
        final StringBuilder uriBuilder = new StringBuilder();
        uriBuilder.append(super.processUri(uri));
        if (!StringUtils.endsWith(uri, API_XML)) {
            uriBuilder.append(API_XML);
        }
        return uriBuilder.toString();
    }

    @Override
    @Capabilities
    public String[] capabilities() {
        return new String[] { "jenkinsStateResolver" };
    }

}
