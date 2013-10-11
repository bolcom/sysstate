package nl.unionsoft.sysstate.dao;

public interface PropertyDao {

    public void setInstanceProperty(long instanceId, String key, String value);

    //
    // public void setProperty(String key, String value);
    //
    // public String getProperty(String key, String defaultValue);
    //
    // public long getProperty(String key, long defaultValue);
    //
    // public int getProperty(String key, int defaultValue);
    //
    // public List<Property> getProperties();

}
