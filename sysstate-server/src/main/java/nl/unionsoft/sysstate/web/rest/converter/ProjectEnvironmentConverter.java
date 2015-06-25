package nl.unionsoft.sysstate.web.rest.converter;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.stereotype.Service;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.common.converter.ListConverter;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.InstanceLinkDto;
import nl.unionsoft.sysstate.common.dto.ProjectEnvironmentDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.sysstate_1_0.Instance;
import nl.unionsoft.sysstate.sysstate_1_0.InstanceLink;
import nl.unionsoft.sysstate.sysstate_1_0.ProjectEnvironment;
import nl.unionsoft.sysstate.sysstate_1_0.State;

@Service("restProjectEnvironmentConverter")
public class ProjectEnvironmentConverter implements Converter<ProjectEnvironment, ProjectEnvironmentDto>{


    @Inject
    @Named("restProjectConverter")
    private ProjectConverter projectConverter;
    
    @Inject
    @Named("restEnvironmentConverter")
    private EnvironmentConverter environmentConverter;
    
    @Override
    public ProjectEnvironment convert(ProjectEnvironmentDto dto) {
        if (dto == null){
            return null;
        }
        ProjectEnvironment projectEnvironment = new ProjectEnvironment();
        projectEnvironment.setId(dto.getId());
        projectEnvironment.setProject(projectConverter.convert(dto.getProject()));
        projectEnvironment.setEnvironment(environmentConverter.convert(dto.getEnvironment()));
        projectEnvironment.setState((dto.getState() == null ? StateType.PENDING : dto.getState()).name());
        return projectEnvironment;
    }

}
