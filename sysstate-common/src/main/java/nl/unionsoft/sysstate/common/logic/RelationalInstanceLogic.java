package nl.unionsoft.sysstate.common.logic;

import java.util.List;
import java.util.Optional;

import nl.unionsoft.sysstate.common.dto.InstanceDto;

public interface RelationalInstanceLogic {

    public List<InstanceDto> getChildInstances(InstanceDto parent);
    
    public Optional<Long> findChildInstanceId(List<InstanceDto> childInstances, String reference);

    public void createOrUpdateInstance(InstanceDto child, InstanceDto parent);
    
    public void deleteNoLongerValidInstances(List<InstanceDto> updatedInstances, List<InstanceDto> children);
}
