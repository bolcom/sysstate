package nl.unionsoft.sysstate.dao;

import java.util.List;
import java.util.Map;

import nl.unionsoft.sysstate.domain.GroupProperty;
import nl.unionsoft.sysstate.domain.Instance;

public interface PropertyDao {

    public void setInstanceProperties(Instance instance,Map<String, String> values);

    public void setGroupProperty(String group, String key, String value);

    public List<GroupProperty> getGroupProperties(String group);
    public GroupProperty getGroupProperty(String group, String key);
   

}
