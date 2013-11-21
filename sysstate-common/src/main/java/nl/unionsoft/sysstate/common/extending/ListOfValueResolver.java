package nl.unionsoft.sysstate.common.extending;

import java.util.Map;

import nl.unionsoft.sysstate.common.dto.PropertyMetaValue;

public interface ListOfValueResolver {

    public Map<String, String> getListOfValues(PropertyMetaValue propertyMetaValue);
}
