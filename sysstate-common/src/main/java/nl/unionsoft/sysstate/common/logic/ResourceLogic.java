package nl.unionsoft.sysstate.common.logic;

import java.util.List;
import java.util.Optional;

import nl.unionsoft.sysstate.common.dto.ResourceDto;

public interface ResourceLogic {

    public <T> T getResourceInstance(String resourceManager, String name);

    
    public Optional<ResourceDto> getResource(String resourceManager, String name);
    
    public List<ResourceDto> getResources();
    
    public List<String> getResourceNames(String resourceManager);
    
    public void createOrUpdate(ResourceDto resourceDto);
    
    public void deleteResource(String resourceManager, String name);

}
