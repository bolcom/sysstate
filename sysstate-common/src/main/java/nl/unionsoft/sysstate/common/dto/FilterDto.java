package nl.unionsoft.sysstate.common.dto;

import java.util.ArrayList;
import java.util.List;

import nl.unionsoft.sysstate.common.enums.StateType;

public class FilterDto {

    private Long id;
    private String name;
    private List<Long> projects;
    private List<Long> environments;
    private List<StateType> states;
    private List<String> stateResolvers;
    private String tags;
    private String search;

    public FilterDto () {
        projects = new ArrayList<Long>();
        environments = new ArrayList<Long>();
        states = new ArrayList<StateType>();
        stateResolvers = new ArrayList<String>();

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getProjects() {
        return projects;
    }

    public void setProjects(List<Long> projects) {
        this.projects = projects;
    }

    public List<Long> getEnvironments() {
        return environments;
    }

    public void setEnvironments(List<Long> environments) {
        this.environments = environments;
    }

    public List<StateType> getStates() {
        return states;
    }

    public void setStates(List<StateType> states) {
        this.states = states;
    }

    public List<String> getStateResolvers() {
        return stateResolvers;
    }

    public void setStateResolvers(List<String> stateResolvers) {
        this.stateResolvers = stateResolvers;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
