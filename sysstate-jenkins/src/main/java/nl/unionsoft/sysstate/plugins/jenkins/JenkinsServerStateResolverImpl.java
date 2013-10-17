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

@Service("jenkinsServerStateResolver")
public class JenkinsServerStateResolverImpl extends XMLBeanStateResolverImpl {

    private static final String API_XML = "/api/xml";

    @Override
    protected void handleXmlObject(final XmlObject xmlObject, final StateDto state, final Properties properties) {
        final Node node = xmlObject.getDomNode();
        final Document document = (Document) node;

        final String offline = getCharacterDataFromObjectWithKey(document, "offline");
        final String temporarilyOffline = getCharacterDataFromObjectWithKey(document, "temporarilyOffline");
        final String numExecutors = getCharacterDataFromObjectWithKey(document, "numExecutors");
        state.appendMessage("Instance has '" + numExecutors + "' executors.");
        if (Boolean.valueOf(offline)) {
            state.setState(StateType.ERROR);
            state.setDescription("OFFLINE");
            if (Boolean.valueOf(temporarilyOffline)) {
                state.appendMessage("Instance is temporarily offline!");

            } else {
                state.appendMessage("Instance is offline!");
            }
        } else {
            state.setState(StateType.STABLE);
            state.setDescription("ONLINE");
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
