package nl.unionsoft.sysstate.plugins.selfdiagnose;

import static nl.unionsoft.sysstate.common.util.XmlUtil.getAttributePropertyFromElement;
import static nl.unionsoft.sysstate.common.util.XmlUtil.getElementWithKeyFromDocument;
import static nl.unionsoft.sysstate.common.util.XmlUtil.getElementWithKeyFromElement;

import java.io.StringWriter;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.plugins.http.XMLBeanStateResolverImpl;

import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.XmlObject;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Service("selfDiagnoseStateResolver")
public class SelfDiagnoseStateResolverImpl extends XMLBeanStateResolverImpl {

    private static final String FORMAT_XML = "?format=xml";

    @Override
    protected void handleXmlObject(final XmlObject xmlObject, final StateDto state,  Map<String, String> configuration) {

        final Node node = xmlObject.getDomNode();
        final Document document = (Document) node;
        if (document == null){
            state.setState(StateType.UNSTABLE);
            state.appendMessage("No document could be found.");
            state.setDescription("No Document");
            return;
        } 
        
        if (document.getChildNodes().getLength() == 0){
            state.setState(StateType.UNSTABLE);
            state.appendMessage("Document doesn't contain any child nodes");
            state.setDescription("No childs");
            return;
        }
        
        final Element selfdiagnose = getElementWithKeyFromDocument(document, "selfdiagnose");
        if (selfdiagnose == null){
            state.setState(StateType.UNSTABLE);
            //Check if this is actually HTML content
            if (StringUtils.equalsIgnoreCase(document.getChildNodes().item(0).getNodeName(),"FOUND HTML")){
                state.appendMessage("The version request returned HTML instead of XML. This is a bug in SelfDiagnose for versions 2.5.1 to 2.5.7. Upgrade to SelfDiagnose 2.5.8 or higher.");
                state.setDescription("HTML");
            } else {
                state.appendMessage("No SelfDiagnose element could be found in the document.");
                state.setDescription("No SelfDiagnose");
            }
            return;
        }
        
        final Element results = getElementWithKeyFromElement(selfdiagnose, "results");
        if (results == null){
            state.setState(StateType.UNSTABLE);
            state.appendMessage("SelfDiagnose element does not contain any results.");
            state.setDescription("No Results");
            return;
        }
        
        final NodeList nodeList = results.getElementsByTagName("result");
        final int nodeListLength = nodeList.getLength();

        final StringBuilder messageBuilder = new StringBuilder(4012);
        boolean allFailed = true;

        String patternStr = configuration.get("pattern");
        Pattern pattern = null;
        if (StringUtils.isNotEmpty(patternStr)) {
            pattern = Pattern.compile(patternStr);
        }
        long versionMatchCount = 0;
        for (int i = 0; i < nodeListLength; i++) {
            final Node resultNode = nodeList.item(i);

            final Element resultNodeElement = (Element) resultNode;
            final String task = getAttributePropertyFromElement(resultNodeElement, "task");
            final String message = getAttributePropertyFromElement(resultNodeElement, "message");
            final String comment = getAttributePropertyFromElement(resultNodeElement, "comment");
            final String status = getAttributePropertyFromElement(resultNodeElement, "status");

            if ("passed".equals(status)) {
                allFailed = false;
            } else {

                state.setState(StateType.UNSTABLE);
                addInfoLineToMessageBuilder(messageBuilder, task, message, status);
            }

            boolean matches = pattern == null || (pattern.matcher(message).matches() || pattern.matcher(comment).matches());

            if ("reportmavenpomproperties".equals(task) && matches) {
                // Maven
                if (StringUtils.isNotBlank(message)) {
                    final String version = StringUtils.trim(StringUtils.substringBetween(message, "Version=", " build"));
                    state.setDescription(version);
                    versionMatchCount++;
                }
            }
        }
        if (allFailed) {
            state.setState(StateType.ERROR);
        } else if (versionMatchCount > 1) {
            state.setState(StateType.UNSTABLE);
            state.appendMessage("There were " + versionMatchCount
                    + " matches for reportmavenpomproperties. Either no pattern is applied, or the applied pattern doesn't return a unique result!");
        }

        if (messageBuilder.length() > 0) {
            state.appendMessage(messageBuilder.toString());
        }
    }

    @Override
    public String processUri(final String uri) {
        final StringBuilder uriBuilder = new StringBuilder();
        uriBuilder.append(super.processUri(uri));
        if (!StringUtils.endsWith(uri, FORMAT_XML)) {
            uriBuilder.append(FORMAT_XML);
        }
        return uriBuilder.toString();
    }

    private void addInfoLineToMessageBuilder(final StringBuilder messageBuilder, final String task, final String message, final String status) {
        messageBuilder.append(StringUtils.trim(task));
        messageBuilder.append(": ");
        messageBuilder.append(status);
        messageBuilder.append(", ");
        messageBuilder.append(StringUtils.trim(message));
        messageBuilder.append('\n');
    }
    
   
}
