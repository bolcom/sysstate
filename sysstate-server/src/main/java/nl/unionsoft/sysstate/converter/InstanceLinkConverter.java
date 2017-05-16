package nl.unionsoft.sysstate.converter;

import org.springframework.stereotype.Service;

import nl.unionsoft.commons.converter.Converter;
import nl.unionsoft.commons.converter.ConverterWithConfig;
import nl.unionsoft.sysstate.common.dto.InstanceLinkDto;
import nl.unionsoft.sysstate.common.dto.InstanceLinkDto.Direction;
import nl.unionsoft.sysstate.domain.Instance;
import nl.unionsoft.sysstate.domain.InstanceLink;
@Service("instanceLinkConverter")
public class InstanceLinkConverter implements ConverterWithConfig<InstanceLinkDto, InstanceLink, Direction> {

    @Override
    public InstanceLinkDto convert(InstanceLink instanceLink, Direction direction) {
        if (instanceLink == null) {
            return null;
        }
        InstanceLinkDto dto = new InstanceLinkDto();
        dto.setName(instanceLink.getName());
        dto.setInstanceToId(instanceLink.getTo().getId());
        dto.setInstanceFromId(instanceLink.getFrom().getId());
        dto.setDirection(direction);
        return dto;
    }

}
