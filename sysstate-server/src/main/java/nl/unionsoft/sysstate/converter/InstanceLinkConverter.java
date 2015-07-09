package nl.unionsoft.sysstate.converter;

import org.springframework.stereotype.Service;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.sysstate.common.dto.InstanceLinkDto;
import nl.unionsoft.sysstate.domain.Instance;
import nl.unionsoft.sysstate.domain.InstanceLink;
@Service("instanceLinkConverter")
public class InstanceLinkConverter implements Converter<InstanceLinkDto, InstanceLink> {

    @Override
    public InstanceLinkDto convert(InstanceLink instanceLink) {
        if (instanceLink == null) {
            return null;
        }
        InstanceLinkDto dto = new InstanceLinkDto();
        dto.setName(instanceLink.getName());
        dto.setInstanceToId(instanceLink.getTo().getId());
        dto.setInstanceFromId(instanceLink.getFrom().getId());
        return dto;
    }

}
