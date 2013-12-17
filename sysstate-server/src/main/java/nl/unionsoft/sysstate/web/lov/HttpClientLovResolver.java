package nl.unionsoft.sysstate.web.lov;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.common.dto.PropertyMetaValue;
import nl.unionsoft.sysstate.common.extending.ListOfValueResolver;
import nl.unionsoft.sysstate.common.logic.HttpClientLogic;

import org.springframework.stereotype.Service;

@Service("httpClientLovResolver")
public class HttpClientLovResolver implements ListOfValueResolver {

    @Inject
    @Named("httpClientLogic")
    private HttpClientLogic httpClientLogic;

    public Map<String, String> getListOfValues(final PropertyMetaValue propertyMetaValue) {
        Map<String, String> results = new LinkedHashMap<String, String>();
        for (String id : httpClientLogic.getHttpClientIds()) {
            results.put(id, id);
        }
        return results;
    }

}
