package nl.unionsoft.sysstate.web.lov;

import java.util.LinkedHashMap;
import java.util.Map;

import nl.unionsoft.sysstate.common.dto.PropertyMetaValue;
import nl.unionsoft.sysstate.common.extending.ListOfValueResolver;

import org.springframework.stereotype.Service;

@Service("booleanLovResolver")
public class BooleanLovResolver implements ListOfValueResolver {

    public Map<String, String> getListOfValues(final PropertyMetaValue propertyMetaValue) {
        Map<String, String> results = new LinkedHashMap<String, String>();
        results.put("true", "TRUE");
        results.put("false", "FALSE");
        return results;
    }
}
