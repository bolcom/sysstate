package nl.unionsoft.sysstate.converter;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.commons.converter.Converter;
import nl.unionsoft.commons.converter.ConverterWithConfig;
import nl.unionsoft.sysstate.common.dto.ProjectEnvironmentDto;
import nl.unionsoft.sysstate.domain.ProjectEnvironment;

import org.springframework.stereotype.Service;

@Service("projectEnvironmentConverter")
public class ProjectEnvironmentConverter implements ConverterWithConfig<ProjectEnvironmentDto, ProjectEnvironment, Boolean>, Converter<ProjectEnvironmentDto, ProjectEnvironment> {
    @Inject
    @Named("projectConverter")
    private ProjectConverter projectConverter;

    @Inject
    @Named("environmentConverter")
    private EnvironmentConverter environmentConverter;

    public ProjectEnvironmentDto convert(ProjectEnvironment projectEnvironment, Boolean nest) {
        ProjectEnvironmentDto result = null;
        if (projectEnvironment != null) {

            result = new ProjectEnvironmentDto();
            result.setId(projectEnvironment.getId());
            result.setHomepageUrl(projectEnvironment.getHomepageUrl());
            if (nest) {
                result.setEnvironment(environmentConverter.convert(projectEnvironment.getEnvironment()));
                result.setProject(projectConverter.convert(projectEnvironment.getProject()));
            }
        }
        return result;
    }

    public ProjectEnvironmentDto convert(ProjectEnvironment projectEnvironment) {
        return convert(projectEnvironment, true);
    }

}
