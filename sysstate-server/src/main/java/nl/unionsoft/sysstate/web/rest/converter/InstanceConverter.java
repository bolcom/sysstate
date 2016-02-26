package nl.unionsoft.sysstate.web.rest.converter;

import java.util.Arrays;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.common.converter.ConverterWithConfig;
import nl.unionsoft.common.converter.ListConverter;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.sysstate_1_0.Instance;
import nl.unionsoft.sysstate.sysstate_1_0.InstanceLinkDirection;
import nl.unionsoft.sysstate.sysstate_1_0.Property;
import nl.unionsoft.sysstate.sysstate_1_0.State;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

@Service("restInstanceConverter")
public class InstanceConverter implements Converter<Instance, InstanceDto>, ConverterWithConfig<Instance, InstanceDto, Integer[]> {

    public static final Integer PROJECT_ENVIRONMENT = 0;
    public static final Integer PROPERTIES = 1;
    public static final Integer INSTANCE_LINKS = 2;
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
    public Instance convert(InstanceDto dto, Integer[] options) {
        if (dto == null) {
            return null;
        }
        Instance instance = new Instance();
        instance.setId(dto.getId());
        instance.setName(dto.getName());
        instance.setHomepageUrl(dto.getHomepageUrl());
        instance.setEnabled(dto.isEnabled());
        instance.setReference(dto.getReference());
        if (StringUtils.isNotEmpty(dto.getTags())){
            instance.getTags().addAll(Arrays.asList(StringUtils.split(dto.getTags(), " ")));    
        }
        instance.setPlugin(dto.getPluginClass());
        instance.setRefreshTimeout(dto.getRefreshTimeout());
        instance.setState(stateConverter.convert(dto.getState()));
        
        if (isSet(INSTANCE_LINKS, options)) {
            instance.getInstanceLinks().addAll(ListConverter.convert(instanceLinkConverter, dto.getIncommingInstanceLinks(), InstanceLinkDirection.INCOMMING));
            instance.getInstanceLinks().addAll(ListConverter.convert(instanceLinkConverter, dto.getOutgoingInstanceLinks(), InstanceLinkDirection.OUTGOING));
        }
        if (isSet(PROJECT_ENVIRONMENT, options)) {
            instance.setProjectEnvironment(projectEnvironmentConverter.convert(dto.getProjectEnvironment()));
        }

        if (isSet(PROPERTIES, options)) {
            //@formatter:off
            instance.getProperties().addAll(dto.getConfiguration().entrySet().stream().map(e -> {
                Property property = new Property();
                property.setKey(e.getKey());
                property.setValue(e.getValue());
                return property;
            }).collect(Collectors.toList()));
        }
        //@formatter:on
        return instance;
    }

    @Override
    public Instance convert(InstanceDto dto) {
        return convert(dto, new Integer[] { PROPERTIES, INSTANCE_LINKS, PROJECT_ENVIRONMENT });
    }

    private boolean isSet(Integer option, Integer[] options) {
        return ArrayUtils.contains(options, option);
    }
}
