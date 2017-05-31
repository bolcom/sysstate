package nl.unionsoft.sysstate.plugins.http;

import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.common.dto.PropertyMetaValue;
import nl.unionsoft.sysstate.common.extending.ListOfValueResolver;
import nl.unionsoft.sysstate.common.logic.ResourceLogic;

@Named("httpClientLovResolver")
public class HttpClientLovResolver implements ListOfValueResolver {

    @Inject
    private ResourceLogic resourceLogic;

    public Map<String, String> getListOfValues(final PropertyMetaValue propertyMetaValue) {
        return resourceLogic.getResourceNames(HttpConstants.RESOURCE_MANAGER_NAME).stream().collect(Collectors.toMap(String::toString, String::toString));
    }

}
