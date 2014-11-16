package nl.unionsoft.sysstate.dao;

import java.util.List;

import nl.unionsoft.sysstate.domain.Project;

public interface ProjectDao {
    public List<Project> getProjects();

    public Project getProject(Long projectId);

    public void createOrUpdateProject(Project project);

    public void delete(Long projectId);

    public Project getProjectByName(String name);

}
