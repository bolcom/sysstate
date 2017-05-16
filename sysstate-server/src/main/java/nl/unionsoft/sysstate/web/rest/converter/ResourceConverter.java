package nl.unionsoft.sysstate.web.rest.converter;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import nl.unionsoft.commons.converter.Converter;
import nl.unionsoft.sysstate.common.dto.ResourceDto;
import nl.unionsoft.sysstate.sysstate_1_0.Property;
import nl.unionsoft.sysstate.sysstate_1_0.Resource;

@Service("restResourceConverter")
public class ResourceConverter implements Converter<Resource, ResourceDto> {

    @Override
    public Resource convert(ResourceDto dto) {
        if (dto == null) {
            return null;
        }

        Resource resource = new Resource();
        resource.setName(dto.getName());
        resource.setManager(dto.getManager());
        resource.getProperties().addAll(dto.getConfiguration().entrySet().stream().map(e -> {
            Property property = new Property();
            property.setKey(e.getKey());
            property.setValue(e.getValue());
            return property;
        }).collect(Collectors.toList()));
        return resource;
    }

}
