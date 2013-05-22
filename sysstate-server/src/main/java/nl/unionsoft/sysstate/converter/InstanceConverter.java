package nl.unionsoft.sysstate.converter;

import javax.inject.Inject;
import javax.inject.Named;

import net.xeoh.plugins.base.Plugin;
import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.plugins.StateResolverPlugin;
import nl.unionsoft.sysstate.domain.Instance;
import nl.unionsoft.sysstate.logic.PluginLogic;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

@Service("instanceConverter")
public class InstanceConverter implements Converter<InstanceDto, Instance> {

    @Inject
    @Named("projectEnvironmentConverter")
    private ProjectEnvironmentConverter projectEnvironmentConverter;

    @Inject
    @Named("pluginLogic")
    private PluginLogic pluginLogic;

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
            final String className = instance.getPluginClass();
            if (StringUtils.isNotEmpty(className)) {
                final Plugin plugin = pluginLogic.getPlugin(className);
                if (plugin instanceof StateResolverPlugin) {
                    final StateResolverPlugin stateResolverPlugin = (StateResolverPlugin) plugin;
                    result.setPluginClass(stateResolverPlugin.getClass().getName());
                }

            }

            result.setEnabled(instance.isEnabled());
            result.setTags(instance.getTags());
            result.setRefreshTimeout(instance.getRefreshTimeout());
        }
        return result;
    }
}
