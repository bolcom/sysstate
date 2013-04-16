package nl.unionsoft.sysstate.common.logic;

import java.util.List;
import java.util.Properties;

import nl.unionsoft.sysstate.common.dto.InstanceDto;

public interface DiscoveryLogic {

    public void discover(String plugin, Properties properties);

    public void addDiscoveredInstance(InstanceDto instance);

    public List<InstanceDto> getDiscoveredInstances();

}
