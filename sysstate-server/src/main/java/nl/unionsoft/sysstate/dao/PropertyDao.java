package nl.unionsoft.sysstate.dao;

import java.util.List;

import nl.unionsoft.sysstate.domain.Property;

public interface PropertyDao {

    public void setProperty(String key, String value);

    public String getProperty(String key, String defaultValue);

    public long getProperty(String key, long defaultValue);

    public int getProperty(String key, int defaultValue);

    public List<Property> getProperties();

}
