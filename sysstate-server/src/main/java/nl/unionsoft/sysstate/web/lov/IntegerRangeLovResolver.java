package nl.unionsoft.sysstate.web.lov;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import nl.unionsoft.sysstate.common.dto.PropertyMetaValue;
import nl.unionsoft.sysstate.common.extending.ListOfValueResolver;

import org.springframework.stereotype.Service;

@Service("integerRangeLovResolver")
public class IntegerRangeLovResolver implements ListOfValueResolver {

    public Map<String, String> getListOfValues(final PropertyMetaValue propertyMetaValue) {
        Properties properties = propertyMetaValue.getProperties();
        Map<String, String> result = new LinkedHashMap<String, String>();
        Integer min = Integer.valueOf(properties.getProperty("min", "3"));
        Integer max = Integer.valueOf(properties.getProperty("max", "10"));
        for (int i = min; i <= max; i++) {
            result.put(String.valueOf(i), String.valueOf(i));
        }
        return result;
    }

}
