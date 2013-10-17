package nl.unionsoft.sysstate.converter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import nl.unionsoft.common.converter.BidirectionalConverter;
import nl.unionsoft.sysstate.domain.InstanceProperty;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Service;

@Service("instancePropertiesConverter")
public class InstancePropertiesConverter implements BidirectionalConverter<Map<String, String>, List<InstanceProperty>> {

    public Map<String, String> convert(List<InstanceProperty> instanceProperties) {

        Map<String, String> configuration = new LinkedHashMap<String, String>();
        if (instanceProperties != null) {
            for (InstanceProperty instanceProperty : instanceProperties) {
                configuration.put(instanceProperty.getKey(), instanceProperty.getValue());
            }
        }

        return configuration;
    }

    public List<InstanceProperty> convertBack(Map<String, String> configuration) {
        List<InstanceProperty> instanceProperties = new ArrayList<InstanceProperty>();
        if (configuration != null) {
            for (Entry<String, String> entry : configuration.entrySet()) {
                InstanceProperty instanceProperty = new InstanceProperty();
                instanceProperty.setKey(ObjectUtils.toString(entry.getKey()));
                instanceProperty.setValue(ObjectUtils.toString(entry.getValue()));
            }
        }
        return instanceProperties;
    }

}
