package nl.unionsoft.sysstate.common.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class PropertyGroupUtil {

    public static Set<String> getGroupIds(Properties properties, String group) {
        Set<String> ids = new HashSet<String>();
        for (String propertyName : properties.stringPropertyNames()) {
            if (StringUtils.startsWith(propertyName, group + ".")) {
                String[] parts = StringUtils.split(propertyName, '.');
                ids.add(parts[1]);
            }
        }
        return ids;
    }

    public static Properties getGroupProperties(Properties properties, String group, String id) {
        Properties results = new Properties();
        String basePattern = group + "." + id + ".";
        for (String propertyName : properties.stringPropertyNames()) {
            if (StringUtils.startsWith(propertyName, basePattern)) {
                String key = StringUtils.substringAfter(propertyName, basePattern);
                String value = properties.getProperty(propertyName);
                results.setProperty(key, value);
            }
        }
        return results;
    }

    public static Map<String, Properties> getGroupProperties(Properties properties, String group) {
        Set<String> ids = getGroupIds(properties, group);
        Map<String, Properties> results = new HashMap<String, Properties>();
        for (String id : ids) {
            results.put(id, getGroupProperties(properties, group, id));
        }
        return results;
    }
}
