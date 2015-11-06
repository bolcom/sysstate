package nl.unionsoft.sysstate.common.extending;

import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;

public abstract class DescriptedStateResolver<T extends PluginDescriptor<StateResolver, InstanceDto>> implements StateResolver {

    public void setState(InstanceDto instance, StateDto state) {
        T d = createPluginDescriptor();
        setState(instance, state, d);
    }

    public abstract void setState(InstanceDto instance, StateDto state, T pluginDescriptor);

    public abstract T createPluginDescriptor();

}
