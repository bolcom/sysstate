package nl.unionsoft.sysstate.common.logic;

import java.util.List;

import nl.unionsoft.sysstate.common.dto.InstanceLinkDto;

public interface InstanceLinkLogic {
    
    public void link(Long fromInstanceId, List<Long> toInstanceIds, String name);
    
    public void link(Long fromInstanceId, Long toInstanceId, String name);
    
    public void unlink(Long fromInstanceId, Long toInstanceId, String name);
    
    public List<InstanceLinkDto> getInstanceLinks(Long instanceId); 
    
}
