package nl.unionsoft.sysstate.web.lov;

import java.util.Properties;

import nl.unionsoft.sysstate.common.extending.ListOfValueResolver;

import org.springframework.stereotype.Service;

@Service("booleanLovResolver")
public class BooleanLovResolver implements ListOfValueResolver {

    public Properties getListOfValues() {
        Properties properties = new Properties();
        properties.setProperty("true", "TRUE");
        properties.setProperty("false", "FALSE");
        return properties;
    }
}
