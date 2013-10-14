package nl.unionsoft.sysstate.dao;

import java.util.List;

import nl.unionsoft.sysstate.domain.GroupProperty;

public interface PropertyDao {

    public void setInstanceProperty(long instanceId, String key, String value);

    public void setGroupProperty(String group, String key, String value);

    public List<GroupProperty> getGroupProperties(String group);
   

}
