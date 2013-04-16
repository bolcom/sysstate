package nl.unionsoft.sysstate.common.dto;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

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

    public ProjectEnvironmentDto () {
        instances = new ArrayList<InstanceDto>();
        count = new CountDto();
    }

    public ProjectDto getProject() {
        return project;
    }

    public void setProject(ProjectDto project) {
        this.project = project;
    }

    public EnvironmentDto getEnvironment() {
        return environment;
    }

    public void setEnvironment(EnvironmentDto environment) {
        this.environment = environment;
    }

    public ViewResultDto getEcoSystemDto() {
        return viewResultDto;
    }

    public void setEcoSystemDto(ViewResultDto viewResultDto) {
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

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHomepageUrl() {
        return homepageUrl;
    }

    public void setHomepageUrl(String homepageUrl) {
        this.homepageUrl = homepageUrl;
    }

    public StateType getState() {
        return state;
    }

    public void setState(StateType state) {
        this.state = state;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
