package nl.unionsoft.sysstate.common.dto;

import java.util.ArrayList;
import java.util.List;

public class ProjectDto {
    private Long id;
    private String name;
    private int order;
    private final List<ProjectEnvironmentDto> projectEnvironments;
    private String defaultInstancePlugin;

    public ProjectDto () {
        projectEnvironments = new ArrayList<ProjectEnvironmentDto>();
    }

    public List<ProjectEnvironmentDto> getProjectEnvironments() {
        return projectEnvironments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getDefaultInstancePlugin() {
        return defaultInstancePlugin;
    }

    public void setDefaultInstancePlugin(String defaultInstancePlugin) {
        this.defaultInstancePlugin = defaultInstancePlugin;
    }

}
