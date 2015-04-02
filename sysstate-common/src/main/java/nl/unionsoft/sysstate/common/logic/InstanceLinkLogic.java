package nl.unionsoft.sysstate.common.logic;

public interface InstanceLinkLogic {

    
    public void link(Long fromInstanceId, Long toInstanceId, String name);
    
    public void unlink(Long fromInstanceId, Long toInstanceId, String name);
    
}
