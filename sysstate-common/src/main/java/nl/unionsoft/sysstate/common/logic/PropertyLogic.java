package nl.unionsoft.sysstate.common.logic;

import java.util.List;
import java.util.Properties;

public interface PropertyLogic {
    public void setProperty(String key, String value);

    public String getProperty(String key, String defaultValue);

    public List<Properties> getProperties();
}
