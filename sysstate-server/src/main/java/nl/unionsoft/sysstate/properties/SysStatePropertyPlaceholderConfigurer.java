package nl.unionsoft.sysstate.properties;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

public class SysStatePropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

    private static final Logger LOG = LoggerFactory.getLogger(SysStatePropertyPlaceholderConfigurer.class);


    @Override
    protected String resolvePlaceholder(String placeholder, Properties props, int systemPropertiesMode) {

        String value = super.resolvePlaceholder(placeholder, props, systemPropertiesMode);
        LOG.info("Resolved placeHolder [{}] to : [{}]", placeholder, obfuscatePass(placeholder, value));
        return value;
    }

    private String obfuscatePass(String key, String value) {
        return StringUtils.containsIgnoreCase(key, "pass") ? "***********" : value;
    }

}
