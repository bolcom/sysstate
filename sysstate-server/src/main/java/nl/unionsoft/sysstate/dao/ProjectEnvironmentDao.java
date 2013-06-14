package nl.unionsoft.sysstate.dao;

import java.util.List;

import nl.unionsoft.sysstate.domain.ProjectEnvironment;

public interface ProjectEnvironmentDao {

    void createOrUpdate(ProjectEnvironment projectEnvironment);

    ProjectEnvironment getProjectEnvironment(Long projectEnvironmentId);

    ProjectEnvironment getProjectEnvironment(Long projectId, Long environmentId);

    List<ProjectEnvironment> getProjectEnvironments();

}
