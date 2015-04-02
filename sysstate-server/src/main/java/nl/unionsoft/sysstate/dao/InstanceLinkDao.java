package nl.unionsoft.sysstate.dao;

public interface InstanceLinkDao {

    
    public void create(Long instanceFromId, Long instanceToId, String name);
    
    public void delete(Long instanceFromId, Long instanceToId, String name);
}
