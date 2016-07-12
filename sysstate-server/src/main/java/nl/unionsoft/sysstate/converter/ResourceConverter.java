package nl.unionsoft.sysstate.converter;

import org.springframework.stereotype.Service;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.sysstate.common.dto.ResourceDto;
import nl.unionsoft.sysstate.domain.Resource;

@Service("resourceConverter")
public class ResourceConverter implements Converter<ResourceDto, Resource> {

    @Override
    public ResourceDto convert(Resource resource) {
        if (resource == null) {
            return null;
        }
        
        ResourceDto dto = new ResourceDto();
        dto.setManager(resource.getManager());
        dto.setName(resource.getName());
        dto.setConfiguration(resource.getConfiguration());
        return dto;
    }

}
