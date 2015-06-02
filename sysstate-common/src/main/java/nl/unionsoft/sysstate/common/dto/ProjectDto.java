package nl.unionsoft.sysstate.common.dto;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ProjectDto {
    private Long id;

    @NotNull()
    @Size(min = 1, max = 15)
    private String name;
    private int order;
    private boolean enabled;
    private String tags;
    
    private final List<ProjectEnvironmentDto> projectEnvironments;

    public ProjectDto() {
        enabled = true;
        projectEnvironments = new ArrayList<ProjectEnvironmentDto>();
    }

    public List<ProjectEnvironmentDto> getProjectEnvironments() {
        return projectEnvironments;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(final int order) {
        this.order = order;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProjectDto other = (ProjectDto) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
    
    

}
