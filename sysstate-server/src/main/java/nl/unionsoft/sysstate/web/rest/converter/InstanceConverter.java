package nl.unionsoft.sysstate.web.rest.converter;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.stereotype.Service;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.common.converter.ListConverter;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.InstanceLinkDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.sysstate_1_0.Instance;
import nl.unionsoft.sysstate.sysstate_1_0.InstanceLink;
import nl.unionsoft.sysstate.sysstate_1_0.State;

@Service("restInstanceConverter")
public class InstanceConverter implements Converter<Instance, InstanceDto>{

    @Inject
    @Named("restStateConverter")
    private Converter<State, StateDto> stateConverter;

    @Inject
    @Named("restInstanceLinkConverter")
    private Converter<InstanceLink, InstanceLinkDto> instanceLinkConverter;

    
    @Override
    public Instance convert(InstanceDto dto) {
        if (dto == null){
            return null;
        }
        Instance instance = new Instance();
        instance.setName(dto.getName());
        instance.setState(stateConverter.convert(dto.getState()));
        instance.getInstanceLinks().addAll(ListConverter.convert(instanceLinkConverter, dto.getInstanceLinks()));
        return instance;
    }

}
