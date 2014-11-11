package nl.unionsoft.sysstate.plugins.groovy;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Inject;

import nl.unionsoft.sysstate.common.dto.PropertyMetaValue;
import nl.unionsoft.sysstate.common.extending.ListOfValueResolver;

import org.springframework.stereotype.Service;

@Service("groovyScriptTypeLovResolver")
public class GroovyScriptTypeLovResolver implements ListOfValueResolver {

    public Map<String, String> getListOfValues(final PropertyMetaValue propertyMetaValue) {
        
        Map<String, String> results = new LinkedHashMap<String, String>();
        for (GroovyScriptType groovyScriptType : GroovyScriptType.values()) {
            results.put(groovyScriptType.name(), groovyScriptType.name());
        }
        return results;
    }

}
