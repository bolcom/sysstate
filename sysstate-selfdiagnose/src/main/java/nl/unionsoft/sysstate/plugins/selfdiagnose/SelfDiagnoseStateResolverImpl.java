package nl.unionsoft.sysstate.plugins.selfdiagnose;

import static nl.unionsoft.sysstate.common.util.XmlUtil.getAttributePropertyFromElement;
import static nl.unionsoft.sysstate.common.util.XmlUtil.getElementWithKeyFromDocument;
import static nl.unionsoft.sysstate.common.util.XmlUtil.getElementWithKeyFromElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.ProjectEnvironmentDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.common.logic.InstanceLinkLogic;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.common.logic.ProjectEnvironmentLogic;
import nl.unionsoft.sysstate.plugins.http.XMLBeanStateResolverImpl;

import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.XmlObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Service("selfDiagnoseStateResolver")
public class SelfDiagnoseStateResolverImpl extends XMLBeanStateResolverImpl {

    private static final Logger logger = LoggerFactory.getLogger(SelfDiagnoseStateResolverImpl.class);
    
    private static final String FORMAT_XML = "?format=xml";

    
    @Inject
    private InstanceLogic instanceLogic;

    @Inject
    private InstanceLinkLogic instanceLinkLogic;

    
    @Override
    protected void handleXmlObject(final XmlObject xmlObject, final StateDto state,  Map<String, String> configuration, final InstanceDto parent) {

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
            if (StringUtils.equalsIgnoreCase(document.getChildNodes().item(0).getNodeName(),"HTML")){
                state.appendMessage("The version request returned HTML instead of XML. This is a bug in SelfDiagnose for versions 2.5.1 to 2.5.7. Upgrade to SelfDiagnose 2.5.8 or higher.");
                state.setDescription("HTML CONTENT");
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

        Pattern versionPattern = createPatternIfApplicable(configuration.get("pattern"));
        
        Pattern checkbackspinservicerunningPattern = createPatternIfApplicable(StringUtils.defaultIfEmpty(configuration.get("bsrPattern"), ".*http.*://([a-zA-Z0-9]*)-([a-zA-Z0-9]*)-.*"));
        int bsrProjectIndex = Integer.valueOf(StringUtils.defaultIfEmpty(configuration.get("bsrProjectIndex"),"2"));
        int bsrEnvironmentIndex = Integer.valueOf(StringUtils.defaultIfEmpty(configuration.get("bsrEnvironmentIndex"),"1"));
        
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

            boolean matches = versionPattern == null || (versionPattern.matcher(message).matches() || versionPattern.matcher(comment).matches());

            if ("reportmavenpomproperties".equals(task) && matches) {
                // Maven
                if (StringUtils.isNotBlank(message)) {
                    final String version = StringUtils.trim(StringUtils.substringBetween(message, "Version=", " build"));
                    state.setDescription(version);
                    versionMatchCount++;
                }
            } else if ("checkbackspinservicerunning".equals(task) && StringUtils.isNotBlank(message))  {
                Matcher matcher = checkbackspinservicerunningPattern.matcher(message);
                if (matcher.matches()){
                    String projectName = matcher.group(bsrProjectIndex);
                    String environmentName = matcher.group(bsrEnvironmentIndex);
                    if (StringUtils.isNotBlank(projectName) && StringUtils.isNotBlank(environmentName)){
                        link(parent, projectName, environmentName);
                    }
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

    private void link(final InstanceDto parent, String projectName, String environmentName) {
        logger.info("Linking this instance to project [{}] and environment [{}]", projectName, environmentName);
        List<InstanceDto> instances = instanceLogic.getInstancesForProjectAndEnvironment(projectName, environmentName);
        if (instances == null || instances.isEmpty()){
            logger.info("The projectEnvironment for project [{}] and environment [{}] could not be found.", projectName, environmentName);
        } else {
            
            List<Long> linkedInstances = new ArrayList<Long>();
            for (InstanceDto instance : instances){
                logger.info("Linking this instance to: [{}]", instance);
                linkedInstances.add(instance.getId());
                
            }
            instanceLinkLogic.link(parent.getId(), linkedInstances, "dependency");
        }
    }

    private Pattern createPatternIfApplicable(String versionPatternString) {
        Pattern versionPattern = null;
        if (StringUtils.isNotEmpty(versionPatternString)) {
            versionPattern = Pattern.compile(versionPatternString);
        }
        return versionPattern;
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
