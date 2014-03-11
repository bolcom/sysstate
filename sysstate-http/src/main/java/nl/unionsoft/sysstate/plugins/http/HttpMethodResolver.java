package nl.unionsoft.sysstate.plugins.http;

import java.util.LinkedHashMap;
import java.util.Map;

import nl.unionsoft.sysstate.common.dto.PropertyMetaValue;
import nl.unionsoft.sysstate.common.extending.ListOfValueResolver;

import org.springframework.stereotype.Service;

@Service("httpMethodResolver")
public class HttpMethodResolver implements ListOfValueResolver {

    public Map<String, String> getListOfValues(final PropertyMetaValue propertyMetaValue) {
        Map<String, String> methods = new LinkedHashMap<String, String>();
        methods.put("GET", "GET");
        methods.put("POST", "POST");
        methods.put("PUT", "PUT");
        methods.put("DELETE", "DELETE");
        return methods;
    }

}
