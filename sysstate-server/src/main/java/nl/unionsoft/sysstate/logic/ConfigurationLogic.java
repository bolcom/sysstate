package nl.unionsoft.sysstate.logic;

import nl.unionsoft.sysstate.common.extending.GroupConfiguration;
import nl.unionsoft.sysstate.common.extending.InstanceConfiguration;

public interface ConfigurationLogic {

    public InstanceConfiguration getInstanceConfiguration(long instanceId);

    public InstanceConfiguration generateInstanceConfigurationForType(String type);

    public void setInstanceConfiguration(long instanceId, InstanceConfiguration instanceConfiguration);
    
    public <T extends GroupConfiguration> T getGroupConfiguration(final Class<T> groupConfigurationClass);
   
    public void setGroupConfiguration(GroupConfiguration groupConfiguration);
}
