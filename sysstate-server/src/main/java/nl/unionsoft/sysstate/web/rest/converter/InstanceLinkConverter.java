package nl.unionsoft.sysstate.web.rest.converter;

import org.springframework.stereotype.Service;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.sysstate.common.dto.InstanceLinkDto;
import nl.unionsoft.sysstate.sysstate_1_0.InstanceLink;
import nl.unionsoft.sysstate.sysstate_1_0.InstanceLinkDirection;

@Service("restInstanceLinkConverter")
public class InstanceLinkConverter implements Converter<InstanceLink, InstanceLinkDto> {

    @Override
    public InstanceLink convert(InstanceLinkDto dto) {
        if (dto == null) {
            return null;
        }

        InstanceLink instanceLink = new InstanceLink();
        
        instanceLink.setName(dto.getName());
        instanceLink.setInstanceId(dto.getInstanceToId());
        switch (dto.getDirection()) {
            case INCOMMING:
                instanceLink.setDirection(InstanceLinkDirection.INCOMMING);
                break;
            case OUTGOING:
                instanceLink.setDirection(InstanceLinkDirection.OUTGOING);
                break;
            default:
                throw new IllegalArgumentException("Unmappable direction [" + dto.getDirection() + "]");
        }
        return instanceLink;
    }
}
