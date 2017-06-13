package nl.unionsoft.sysstate.plugins.groovy.basic

import javax.inject.Inject
import javax.inject.Named

import org.apache.commons.io.IOUtils
import org.apache.http.HttpEntity

import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath

import nl.unionsoft.sysstate.common.dto.InstanceDto
import nl.unionsoft.sysstate.common.dto.StateDto
import nl.unionsoft.sysstate.common.dto.TextDto
import nl.unionsoft.sysstate.common.enums.StateType
import nl.unionsoft.sysstate.common.logic.TextLogic
import nl.unionsoft.sysstate.plugins.http.HttpStateResolverImpl

@Named("jPathStateResolver")
class JPathStateResolver extends HttpStateResolverImpl {

    public static final String PREDEFINED_JPATH = "predefinedJpath"

    private static final String JPATH = "jpath"

    TextLogic textLogic;

    @Inject
    public JPathStateResolver(@Named("textLogic") TextLogic textLogic){
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

            def jpath = getJpath(configuration)
            
            String version = JsonPath.parse(contentStream).read(jpath)

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

    public String getJpath(Map<String, String> configuration){

        if (configuration.get(JPATH)){
            return configuration.get(JPATH);
        }

        def predefinedXpath = configuration.get(PREDEFINED_JPATH)
        if (predefinedXpath){
            Optional<TextDto> optText = textLogic.getText(predefinedXpath)
            if (optText.isPresent()){
                return optText.get().text
            }
            throw new IllegalStateException("No text with name [${predefinedXpath}] can be found.")
        }

        throw new IllegalStateException("Cannot determin xpath. No 'jpath' or 'predefinedJpath' is defined.")
    }
}
