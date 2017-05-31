package nl.unionsoft.sysstate.web.rest.converter;

import nl.unionsoft.commons.converter.Converter;
import nl.unionsoft.sysstate.common.dto.ProjectDto;
import nl.unionsoft.sysstate.common.dto.ProjectEnvironmentDto;
import nl.unionsoft.sysstate.sysstate_1_0.Project;
import nl.unionsoft.sysstate.sysstate_1_0.ProjectEnvironment;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

@Service("restProjectConverter")
public class ProjectConverter implements Converter<Project, ProjectDto> {

    @Override
    public Project convert(ProjectDto dto) {
        if (dto == null) {
            return null;
        }
        Project project = new Project();
        project.setId(dto.getId());
        project.setName(dto.getName());
        project.setOrder(dto.getOrder());
        
        if (StringUtils.isNotEmpty(dto.getTags())){
            project.getTags().addAll(Arrays.asList(StringUtils.split(dto.getTags(), " ")));    
        }
        
        return project;
    }

}
