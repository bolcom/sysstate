package nl.unionsoft.sysstate.common.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import nl.unionsoft.sysstate.common.enums.StateType;

public class FilterDto implements Serializable {

    private static final long serialVersionUID = 2063143092239342545L;

    private Long id;

    private String name;
    private List<Long> projects;
    private List<Long> environments;
    private List<StateType> states;
    private List<String> stateResolvers;
    private List<ViewDto> views;
    private String tags;
    private String search;

    public FilterDto() {
        projects = new ArrayList<Long>();
        environments = new ArrayList<Long>();
        states = new ArrayList<StateType>();
        stateResolvers = new ArrayList<String>();
        views = new ArrayList<ViewDto>();

    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Long getFirstProject() {
        return projects.size() > 0 ? projects.get(0) : null;
    }

    public List<Long> getProjects() {
        return projects;
    }

    public void setProjects(final List<Long> projects) {
        this.projects = projects;
    }

    public Long getFirstEnvironment() {
        return environments.size() > 0 ? environments.get(0) : null;
    }
    
    public List<Long> getEnvironments() {
        return environments;
    }

    public void setEnvironments(final List<Long> environments) {
        this.environments = environments;
    }

    public List<StateType> getStates() {
        return states;
    }

    public void setStates(final List<StateType> states) {
        this.states = states;
    }

    public List<String> getStateResolvers() {
        return stateResolvers;
    }

    public void setStateResolvers(final List<String> stateResolvers) {
        this.stateResolvers = stateResolvers;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(final String tags) {
        this.tags = tags;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(final String search) {
        this.search = search;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public List<ViewDto> getViews() {
        return views;
    }

    public void setViews(List<ViewDto> views) {
        this.views = views;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((environments == null) ? 0 : environments.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((projects == null) ? 0 : projects.hashCode());
        result = prime * result + ((search == null) ? 0 : search.hashCode());
        result = prime * result + ((stateResolvers == null) ? 0 : stateResolvers.hashCode());
        result = prime * result + ((states == null) ? 0 : states.hashCode());
        result = prime * result + ((tags == null) ? 0 : tags.hashCode());
        result = prime * result + ((views == null) ? 0 : views.hashCode());
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
        FilterDto other = (FilterDto) obj;
        if (environments == null) {
            if (other.environments != null)
                return false;
        } else if (!environments.equals(other.environments))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (projects == null) {
            if (other.projects != null)
                return false;
        } else if (!projects.equals(other.projects))
            return false;
        if (search == null) {
            if (other.search != null)
                return false;
        } else if (!search.equals(other.search))
            return false;
        if (stateResolvers == null) {
            if (other.stateResolvers != null)
                return false;
        } else if (!stateResolvers.equals(other.stateResolvers))
            return false;
        if (states == null) {
            if (other.states != null)
                return false;
        } else if (!states.equals(other.states))
            return false;
        if (tags == null) {
            if (other.tags != null)
                return false;
        } else if (!tags.equals(other.tags))
            return false;
        if (views == null) {
            if (other.views != null)
                return false;
        } else if (!views.equals(other.views))
            return false;
        return true;
    }
    
    

}
