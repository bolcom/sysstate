package nl.unionsoft.sysstate.common.extending;

import java.util.List;

import nl.unionsoft.sysstate.common.dto.PropertyMetaValue;

public interface PluginDescriptor<P, S> {

    
    public List<PropertyMetaValue> getPropertyMeta(P parent);
    
    public void populate(S from);
    
}
