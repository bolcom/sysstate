package nl.unionsoft.sysstate.logic;

import java.util.List;

import nl.unionsoft.sysstate.domain.ProjectEnvironment;

public interface ProjectEnvironmentLogic {
    public void createOrUpdate(ProjectEnvironment projectEnvironment);

    public Long createIfNotExists(Long projectId, Long environmentId);

    public ProjectEnvironment getProjectEnvironment(Long projectId, Long environmentId);

    public List<ProjectEnvironment> getProjectEnvironments();

}
