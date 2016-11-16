package nl.unionsoft.sysstate.common.logic;

import java.util.List;
import java.util.Optional;

import nl.unionsoft.sysstate.common.dto.ProjectDto;

public interface ProjectLogic {

    public List<ProjectDto> getProjects();

    public ProjectDto getProject(Long projectId);

    public Optional<ProjectDto> getProjectByName(String name);

    public Long createOrUpdateProject(ProjectDto project);

    public void delete(Long projectId);
    
    public ProjectDto findOrCreateProject(String projectName); 

}
