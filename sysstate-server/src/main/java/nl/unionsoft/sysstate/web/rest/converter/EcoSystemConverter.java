package nl.unionsoft.sysstate.web.rest.converter;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.stereotype.Service;

import nl.unionsoft.commons.converter.Converter;
import nl.unionsoft.commons.converter.ListConverter;
import nl.unionsoft.sysstate.common.dto.ViewResultDto;
import nl.unionsoft.sysstate.sysstate_1_0.EcoSystem;

@Service("restEcoSystemConverter")
public class EcoSystemConverter implements Converter<EcoSystem, ViewResultDto> {

    @Inject
    @Named("restProjectConverter")
    private ProjectConverter projectConverter;

    @Inject
    @Named("restEnvironmentConverter")
    private EnvironmentConverter environmentConverter;

    @Inject
    @Named("restProjectEnvironmentConverter")
    private ProjectEnvironmentConverter projectEnvironmentConverter;

    @Inject
    @Named("instanceStateConverter")
    private InstanceStateConverter instanceStateConverter;

    @Override
    public EcoSystem convert(ViewResultDto dto) {
        if (dto == null) {
            return null;
        }
        EcoSystem ecoSystem = new EcoSystem();
        ecoSystem.getEnvironments().addAll(ListConverter.convert(environmentConverter, dto.getEnvironments()));
        ecoSystem.getProjects().addAll(ListConverter.convert(projectConverter, dto.getProjects()));
        ecoSystem.getInstances().addAll(ListConverter.convert(instanceStateConverter, dto.getInstanceStates()));
        ecoSystem.getProjectEnvironments().addAll(ListConverter.convert(projectEnvironmentConverter, dto.getProjectEnvironments()));
        return ecoSystem;
    }
}
