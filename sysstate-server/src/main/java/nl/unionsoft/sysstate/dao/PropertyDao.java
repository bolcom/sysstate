package nl.unionsoft.sysstate.dao;

import java.util.List;

import nl.unionsoft.sysstate.domain.GroupProperty;
import nl.unionsoft.sysstate.domain.Instance;

public interface PropertyDao {

    public void setInstanceProperty(Instance instance, String key, String value);

    public void setGroupProperty(String group, String key, String value);

    public List<GroupProperty> getGroupProperties(String group);
    public GroupProperty getGroupProperty(String group, String key);
   

}
