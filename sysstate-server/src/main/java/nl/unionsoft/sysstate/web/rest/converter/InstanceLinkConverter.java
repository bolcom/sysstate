package nl.unionsoft.sysstate.web.rest.converter;

import org.springframework.stereotype.Service;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.common.converter.ConverterWithConfig;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.InstanceLinkDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.sysstate_1_0.Instance;
import nl.unionsoft.sysstate.sysstate_1_0.InstanceLink;
import nl.unionsoft.sysstate.sysstate_1_0.InstanceLinkDirection;
import nl.unionsoft.sysstate.sysstate_1_0.State;

@Service("restInstanceLinkConverter")
public class InstanceLinkConverter implements ConverterWithConfig<InstanceLink, InstanceLinkDto, InstanceLinkDirection>{


    @Override
    public InstanceLink convert(InstanceLinkDto dto, InstanceLinkDirection direction) {
        if (dto == null){
            return null;
        }

        InstanceLink instanceLink = new InstanceLink();
        instanceLink.setInstanceId(dto.getInstanceToId());
        instanceLink.setName(dto.getName());
        instanceLink.setDirection(direction);
        return instanceLink;
    }



}
