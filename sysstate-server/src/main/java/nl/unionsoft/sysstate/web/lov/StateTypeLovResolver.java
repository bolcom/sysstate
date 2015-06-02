package nl.unionsoft.sysstate.web.lov;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import nl.unionsoft.sysstate.common.dto.PropertyMetaValue;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.common.extending.ListOfValueResolver;
@Service("stateTypeLovResolver")
public class StateTypeLovResolver implements ListOfValueResolver {

    @Override
    public Map<String, String> getListOfValues(PropertyMetaValue propertyMetaValue) {
        Map<String, String> results = new HashMap<String, String>();
        for (StateType stateType : StateType.values()) {
            results.put(stateType.name(), stateType.name());
        }
        return results;
    }

}
