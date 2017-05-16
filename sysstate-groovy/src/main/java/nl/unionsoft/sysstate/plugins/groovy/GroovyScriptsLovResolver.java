package nl.unionsoft.sysstate.plugins.groovy;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.common.dto.PropertyMetaValue;
import nl.unionsoft.sysstate.common.extending.ListOfValueResolver;

@Named("groovyScriptsLovResolver")
public class GroovyScriptsLovResolver implements ListOfValueResolver {


    private GroovyScriptManager groovyScriptManager;

    @Inject
    public GroovyScriptsLovResolver(GroovyScriptManager groovyScriptManager) {
        this.groovyScriptManager = groovyScriptManager;
    }

    public Map<String, String> getListOfValues(final PropertyMetaValue propertyMetaValue) {
        Map<String, String> results = new LinkedHashMap<String, String>();
        for (String scriptName : groovyScriptManager.getScriptNames()) {
            results.put(scriptName, scriptName);
        }
        return results;
    }

}
