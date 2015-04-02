package nl.unionsoft.sysstate.web.rest.converter;

import org.springframework.stereotype.Service;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.InstanceLinkDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.sysstate_1_0.Instance;
import nl.unionsoft.sysstate.sysstate_1_0.InstanceLink;
import nl.unionsoft.sysstate.sysstate_1_0.State;

@Service("restInstanceLinkConverter")
public class InstanceLinkConverter implements Converter<InstanceLink, InstanceLinkDto>{

    @Override
    public InstanceLink convert(InstanceLinkDto dto) {
        if (dto == null){
            return null;
        }

        InstanceLink instanceLink = new InstanceLink();
        instanceLink.setInstanceId(dto.getInstanceId());
        instanceLink.setName(dto.getName());
        return instanceLink;
    }



}
