package nl.unionsoft.sysstate.common.logic;

import java.util.List;

import nl.unionsoft.sysstate.common.dto.ProjectEnvironmentDto;

public interface ProjectEnvironmentLogic {
    public void createOrUpdate(ProjectEnvironmentDto projectEnvironment);

    public Long createIfNotExists(Long projectId, Long environmentId);

    public ProjectEnvironmentDto getProjectEnvironment(Long projectId, Long environmentId);
    public ProjectEnvironmentDto getProjectEnvironment(String projectName, String environmentName);

    public List<ProjectEnvironmentDto> getProjectEnvironments(final boolean resolveNestedProps);

}
