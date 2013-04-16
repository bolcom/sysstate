package nl.unionsoft.sysstate.common.logic;

import java.util.List;

import nl.unionsoft.sysstate.common.dto.ProjectDto;

public interface ProjectLogic {

    public List<ProjectDto> getProjects();

    public ProjectDto getProject(Long projectId);

    public ProjectDto findProject(String name);

    public void createOrUpdateProject(ProjectDto project);

    public void delete(Long projectId);

}
