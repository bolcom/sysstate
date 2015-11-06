package nl.unionsoft.sysstate.common.logic;

import java.util.Map;

import nl.unionsoft.sysstate.common.dto.PropertyMetaValue;
import nl.unionsoft.sysstate.common.extending.ListOfValueResolver;

public interface ListOfValueLogic {

    public Map<String, String> getListOfValues(String lovResolver, PropertyMetaValue propertyMetaValue);

    public Map<String, String> getListOfValues(Class<? extends ListOfValueResolver> lovResolverClass, PropertyMetaValue propertyMetaValue);
}
