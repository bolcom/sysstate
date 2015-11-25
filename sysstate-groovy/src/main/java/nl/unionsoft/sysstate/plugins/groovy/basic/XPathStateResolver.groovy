package nl.unionsoft.sysstate.plugins.groovy.basic

import javax.inject.Inject;
import javax.inject.Named
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

import nl.unionsoft.sysstate.common.dto.InstanceDto
import nl.unionsoft.sysstate.common.dto.StateDto
import nl.unionsoft.sysstate.common.enums.StateType
import nl.unionsoft.sysstate.common.logic.TextLogic;
import nl.unionsoft.sysstate.plugins.http.HttpStateResolverImpl

import org.apache.commons.io.IOUtils
import org.apache.http.HttpEntity

@Named("xPathStateResolver")
class XPathStateResolver extends HttpStateResolverImpl {

    TextLogic textLogic;
    
    @Inject
    public XPathStateResolver(@Named("textLogic") TextLogic textLogic){
        this.textLogic = textLogic;
    }
    
    @Override
    public void handleEntity(HttpEntity httpEntity, Map<String, String> configuration, StateDto state, InstanceDto instance) throws IOException {
        if (httpEntity == null) {
            return;
        }
        InputStream contentStream = null;
        try {
            contentStream = httpEntity.getContent();
            def xpath = configuration.get("xpath") ?: textLogic.getText(configuration.get("predefinedXpath") as long).text
            def builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            def doc = builder.parse(contentStream)
            def expr = XPathFactory.newInstance().newXPath().compile(xpath)
            def version = expr.evaluate(doc, XPathConstants.STRING)
            if (version){
                state.setDescription(version)
                state.setState(StateType.STABLE)
            } else {
                state.setDescription("NOMATCH")
                state.appendMessage("No match for given xpath expression [${xpath}]")
                state.setState(StateType.UNSTABLE)
            }
        } finally {
            IOUtils.closeQuietly(contentStream);
        }
    }
}
