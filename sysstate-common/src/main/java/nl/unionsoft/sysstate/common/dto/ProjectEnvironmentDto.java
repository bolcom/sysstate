package nl.unionsoft.sysstate.common.dto;

import java.util.ArrayList;
import java.util.List;

import nl.unionsoft.sysstate.common.enums.StateType;

public class ProjectEnvironmentDto {

    private Long id;
    private ProjectDto project;
    private EnvironmentDto environment;
    private ViewResultDto viewResultDto;
    private final List<InstanceDto> instances;
    private final CountDto count;
    private String description;
    private String homepageUrl;
    private StateType state;
    private int rating;

    public ProjectEnvironmentDto() {
        instances = new ArrayList<InstanceDto>();
        count = new CountDto();
    }

    public ProjectDto getProject() {
        return project;
    }

    public void setProject(final ProjectDto project) {
        this.project = project;
    }

    public EnvironmentDto getEnvironment() {
        return environment;
    }

    public void setEnvironment(final EnvironmentDto environment) {
        this.environment = environment;
    }

    public ViewResultDto getEcoSystemDto() {
        return viewResultDto;
    }

    public void setEcoSystemDto(final ViewResultDto viewResultDto) {
        this.viewResultDto = viewResultDto;
    }

    public List<InstanceDto> getInstances() {
        return instances;
    }

    public CountDto getCount() {
        return count;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getHomepageUrl() {
        return homepageUrl;
    }

    public void setHomepageUrl(final String homepageUrl) {
        this.homepageUrl = homepageUrl;
    }

    public StateType getState() {
        return state;
    }

    public void setState(final StateType state) {
        this.state = state;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(final int rating) {
        this.rating = rating;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((environment == null) ? 0 : environment.hashCode());
        result = prime * result + ((project == null) ? 0 : project.hashCode());
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
        ProjectEnvironmentDto other = (ProjectEnvironmentDto) obj;
        if (environment == null) {
            if (other.environment != null)
                return false;
        } else if (!environment.equals(other.environment))
            return false;
        if (project == null) {
            if (other.project != null)
                return false;
        } else if (!project.equals(other.project))
            return false;
        return true;
    }
    
    

}
