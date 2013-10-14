package nl.unionsoft.sysstate.web.mvc.converter;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.common.converter.BidirectionalConverter;
import nl.unionsoft.common.param.Context;
import nl.unionsoft.common.param.ParamContextLogicImpl;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.ProjectEnvironmentDto;
import nl.unionsoft.sysstate.common.extending.InstanceConfiguration;
import nl.unionsoft.sysstate.web.mvc.form.InstanceForm;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Service;

@Service("instanceFormConverter")
public class InstanceFormConverter implements BidirectionalConverter<InstanceDto<InstanceConfiguration>, InstanceForm> {

    @Inject
    @Named("paramContextLogic")
    private ParamContextLogicImpl paramContextLogic;

    public InstanceDto<InstanceConfiguration> convert(InstanceForm instanceForm) {

        InstanceDto<InstanceConfiguration> result = null;

        if (instanceForm != null) {
            result = new InstanceDto<InstanceConfiguration>();
            result.setId(instanceForm.getId());
            result.setEnabled(instanceForm.isEnabled());
            result.setHomepageUrl(instanceForm.getHomepageUrl());
            try {
                @SuppressWarnings("unchecked")
                Class<InstanceConfiguration> configuredByClass = (Class<InstanceConfiguration>) Class.forName(instanceForm.getPluginConfiguredByClass());
                InstanceConfiguration instanceConfiguration = configuredByClass.newInstance();
                Map<String, String> icParams = instanceForm.getInstanceConfigurationParams();
                if (icParams != null) {
                    // Ugly map transformation
                    Map<String, Object> icParamsTransformed = new HashMap<String, Object>();
                    for (Entry<String, String> entry : icParams.entrySet()) {
                        icParamsTransformed.put(entry.getKey(), entry.getValue());
                    }
                    paramContextLogic.setBeanValues(instanceConfiguration, icParamsTransformed);
                }
                result.setInstanceConfiguration(instanceConfiguration);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            result.setName(instanceForm.getName());
            result.setPluginClass(instanceForm.getPluginClass());

            ProjectEnvironmentDto projectEnvironmentDto = new ProjectEnvironmentDto();
            projectEnvironmentDto.setId(instanceForm.getProjectEnvironmentId());
            result.setProjectEnvironment(projectEnvironmentDto);

            result.setRefreshTimeout(instanceForm.getRefreshTimeout());
            result.setTags(instanceForm.getTags());
        }
        return result;
    }

    public InstanceForm convertBack(InstanceDto<InstanceConfiguration> instanceDto) {

        InstanceForm result = null;
        if (instanceDto != null) {
            result = new InstanceForm();
            result.setId(instanceDto.getId());
            result.setEnabled(instanceDto.isEnabled());
            result.setHomepageUrl(instanceDto.getHomepageUrl());
            result.setName(instanceDto.getName());
            result.setPluginClass(instanceDto.getPluginClass());
            ProjectEnvironmentDto projectEnvironmentDto = instanceDto.getProjectEnvironment();
            if (projectEnvironmentDto != null) {
                result.setProjectEnvironmentId(projectEnvironmentDto.getId());
            }
            result.setRefreshTimeout(instanceDto.getRefreshTimeout());
            result.setTags(instanceDto.getTags());
            InstanceConfiguration instanceConfiguration = instanceDto.getInstanceConfiguration();
            Class<? extends InstanceConfiguration> instanceConfigurationClass = instanceConfiguration.getClass();
            result.setPluginConfiguredByClass(instanceConfigurationClass.getCanonicalName());
            Map<String, String> instanceConfigurationParams = new LinkedHashMap<String, String>();
            for (Context context : paramContextLogic.getContext(instanceConfigurationClass)) {
                try {
                    String value = ObjectUtils.toString(BeanUtils.getProperty(instanceConfiguration, context.getId()));
                    instanceConfigurationParams.put(context.getId(), value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }

            }
            result.setInstanceConfigurationParams(instanceConfigurationParams);
        }
        return result;
    }

}
