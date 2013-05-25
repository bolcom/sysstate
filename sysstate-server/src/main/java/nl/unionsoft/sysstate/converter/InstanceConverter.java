package nl.unionsoft.sysstate.converter;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.domain.Instance;

import org.springframework.stereotype.Service;

@Service("instanceConverter")
public class InstanceConverter implements Converter<InstanceDto, Instance> {

    @Inject
    @Named("projectEnvironmentConverter")
    private ProjectEnvironmentConverter projectEnvironmentConverter;
    //
    // @Inject
    // @Named("pluginLogic")
    // private PluginLogic pluginLogic;

    @Inject
    @Named("stateConverter")
    private StateConverter stateConverter;

    public InstanceDto convert(final Instance instance) {
        InstanceDto result = null;
        if (instance != null) {
            result = new InstanceDto();
            result.setId(instance.getId());
            result.setName(instance.getName());
            result.setConfiguration(instance.getConfiguration());
            result.setProjectEnvironment(projectEnvironmentConverter.convert(instance.getProjectEnvironment()));
            result.setHomepageUrl(instance.getHomepageUrl());
            result.setPluginClass(instance.getPluginClass());
            result.setEnabled(instance.isEnabled());
            result.setTags(instance.getTags());
            result.setRefreshTimeout(instance.getRefreshTimeout());
        }
        return result;
    }
}