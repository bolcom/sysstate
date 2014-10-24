package nl.unionsoft.sysstate.converter;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.sysstate.common.dto.ProjectDto;
import nl.unionsoft.sysstate.domain.Project;

import org.springframework.stereotype.Service;

@Service("projectConverter")
public class ProjectConverter implements Converter<ProjectDto, Project> {

    public ProjectDto convert(Project project) {
        ProjectDto result = null;
        if (project != null) {
            result = new ProjectDto();
            result.setId(project.getId());
            result.setName(project.getName());
            result.setOrder(project.getOrder());
            result.setEnabled(project.isEnabled());
            result.setTags(project.getTags());

        }
        return result;

    }

}
