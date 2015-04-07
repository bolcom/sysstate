package nl.unionsoft.sysstate.common.logic;

import java.util.List;

public interface InstanceLinkLogic {

    
    public void link(Long fromInstanceId, List<Long> toInstanceIds, String name);
    
    public void link(Long fromInstanceId, Long toInstanceId, String name);
    
    public void unlink(Long fromInstanceId, Long toInstanceId, String name);
    
}
