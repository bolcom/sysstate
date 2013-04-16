package nl.unionsoft.sysstate.common.dto;

public class StatisticsDto {

    private Long projects;
    private Long environments;
    private Long instances;
    private Long projectEnvironments;
    private Long views;
    private Long filters;
    private Long updates;
    private Long states;

    public Long getProjects() {
        return projects;
    }

    public void setProjects(Long projects) {
        this.projects = projects;
    }

    public Long getEnvironments() {
        return environments;
    }

    public void setEnvironments(Long environments) {
        this.environments = environments;
    }

    public Long getInstances() {
        return instances;
    }

    public void setInstances(Long instances) {
        this.instances = instances;
    }

    public Long getProjectEnvironments() {
        return projectEnvironments;
    }

    public void setProjectEnvironments(Long projectEnvironments) {
        this.projectEnvironments = projectEnvironments;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    public Long getFilters() {
        return filters;
    }

    public void setFilters(Long filters) {
        this.filters = filters;
    }

    public Long getUpdates() {
        return updates;
    }

    public void setUpdates(Long updates) {
        this.updates = updates;
    }

    public Long getStates() {
        return states;
    }

    public void setStates(Long states) {
        this.states = states;
    }

}
