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

}
