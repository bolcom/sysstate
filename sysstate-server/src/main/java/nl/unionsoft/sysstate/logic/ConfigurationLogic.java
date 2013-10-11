package nl.unionsoft.sysstate.logic;

import nl.unionsoft.sysstate.common.extending.InstanceConfiguration;

public interface ConfigurationLogic {


    public InstanceConfiguration getInstanceConfiguration(long instanceId);

    public void setInstanceConfiguration(long instanceId, InstanceConfiguration instanceConfiguration);
}
