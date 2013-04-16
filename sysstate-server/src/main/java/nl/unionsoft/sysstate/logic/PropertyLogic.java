package nl.unionsoft.sysstate.logic;

import java.util.List;

import nl.unionsoft.sysstate.domain.Property;

public interface PropertyLogic {
    public void setProperty(String key, String value);

    public String getProperty(String key, String defaultValue);

    public List<Property> getProperties();
}
