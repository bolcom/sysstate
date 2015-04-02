package nl.unionsoft.sysstate.web.rest.converter;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.sysstate.common.dto.ProjectDto;
import nl.unionsoft.sysstate.common.dto.ProjectEnvironmentDto;
import nl.unionsoft.sysstate.sysstate_1_0.Project;
import nl.unionsoft.sysstate.sysstate_1_0.ProjectEnvironment;

import org.springframework.stereotype.Service;

@Service("restProjectConverter")
public class ProjectConverter implements Converter<Project, ProjectDto>{



    
    @Override
    public Project convert(ProjectDto dto) {
        if (dto == null){
            return null;
        }
        Project project = new Project();
        project.setId(dto.getId());
        project.setName(dto.getName());
        return project;
    }

}
