package nl.unionsoft.sysstate.web.rest.converter;

import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.common.converter.ListConverter;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.sysstate_1_0.Instance;
import nl.unionsoft.sysstate.sysstate_1_0.InstanceLinkDirection;
import nl.unionsoft.sysstate.sysstate_1_0.Property;
import nl.unionsoft.sysstate.sysstate_1_0.State;

import org.springframework.stereotype.Service;

@Service("restInstanceConverter")
public class InstanceConverter implements Converter<Instance, InstanceDto> {

    @Inject
    @Named("restStateConverter")
    private Converter<State, StateDto> stateConverter;

    @Inject
    @Named("restInstanceLinkConverter")
    private InstanceLinkConverter instanceLinkConverter;

    @Inject
    @Named("restProjectEnvironmentConverter")
    private ProjectEnvironmentConverter projectEnvironmentConverter;

    @Override
    public Instance convert(InstanceDto dto) {
        if (dto == null) {
            return null;
        }
        Instance instance = new Instance();
        instance.setId(dto.getId());
        instance.setName(dto.getName());
        instance.setHomepageUrl(dto.getHomepageUrl());
        instance.setEnabled(dto.isEnabled());
        instance.setReference(dto.getReference());
        instance.setPlugin(dto.getPluginClass());
        instance.setRefreshTimeout(dto.getRefreshTimeout());
        instance.setState(stateConverter.convert(dto.getState()));
        instance.getInstanceLinks().addAll(ListConverter.convert(instanceLinkConverter, dto.getIncommingInstanceLinks(), InstanceLinkDirection.INCOMMING));
        instance.getInstanceLinks().addAll(ListConverter.convert(instanceLinkConverter, dto.getOutgoingInstanceLinks(), InstanceLinkDirection.OUTGOING));
        instance.setProjectEnvironment(projectEnvironmentConverter.convert(dto.getProjectEnvironment()));

        //@formatter:off
        instance.getProperties().addAll(dto.getConfiguration().entrySet().stream().map(e -> {
            Property property = new Property();
            property.setKey(e.getKey());
            property.setValue(e.getValue());
            return property;
        }).collect(Collectors.toList()));
        //@formatter:on
        return instance;
    }

}
