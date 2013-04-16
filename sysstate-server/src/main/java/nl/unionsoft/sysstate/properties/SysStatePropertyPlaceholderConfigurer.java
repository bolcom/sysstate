package nl.unionsoft.sysstate.properties;

import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

public class SysStatePropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

    private static final Logger LOG = LoggerFactory.getLogger(SysStatePropertyPlaceholderConfigurer.class);

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
        super.processProperties(beanFactoryToProcess, props);
        LOG.info("Processed properties: \n {}, ", logProperties(props));
        logProperties(props);
    }

    private String logProperties(Properties props) {
        final StringBuilder propertyBuilder = new StringBuilder(4000);
        if (props != null) {
            for (final Entry<Object, Object> entry : props.entrySet()) {
                final String key = ObjectUtils.toString(entry.getKey());
                final String value = ObjectUtils.toString(entry.getValue());
                propertyBuilder.append('\t');
                propertyBuilder.append(key);
                propertyBuilder.append("=");
                if (StringUtils.contains(key, "pass")) {
                    propertyBuilder.append("***********");
                } else {
                    propertyBuilder.append(value);
                }
                propertyBuilder.append('\n');
            }
        }
        return propertyBuilder.toString();
    }
}
