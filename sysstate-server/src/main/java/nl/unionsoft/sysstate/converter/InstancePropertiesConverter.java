package nl.unionsoft.sysstate.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import nl.unionsoft.common.converter.BidirectionalConverter;
import nl.unionsoft.sysstate.domain.InstanceProperty;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Service;

@Service("instancePropertiesConverter")
public class InstancePropertiesConverter implements BidirectionalConverter<Properties, List<InstanceProperty>> {

    public Properties convert(List<InstanceProperty> instanceProperties) {

        Properties properties = new Properties();
        if (instanceProperties != null) {
            for (InstanceProperty instanceProperty : instanceProperties) {
                properties.setProperty(instanceProperty.getKey(), instanceProperty.getValue());
            }
        }

        return properties;
    }

    public List<InstanceProperty> convertBack(Properties properties) {
        List<InstanceProperty> instanceProperties = new ArrayList<InstanceProperty>();
        if (properties != null) {
            for (Entry<Object, Object> entry : properties.entrySet()) {
                InstanceProperty instanceProperty = new InstanceProperty();
                instanceProperty.setKey(ObjectUtils.toString(entry.getKey()));
                instanceProperty.setValue(ObjectUtils.toString(entry.getValue()));
            }
        }
        return instanceProperties;
    }

}
