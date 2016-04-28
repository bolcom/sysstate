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
	private String tags;
	private String search;

	public FilterDto() {
		projects = new ArrayList<Long>();
		environments = new ArrayList<Long>();
		stateResolvers = new ArrayList<String>();
		states = new ArrayList<StateType>();

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

	public List<StateType> getStates() {
		return states;
	}

	public void setStates(final List<StateType> states) {
		this.states = states;
	}

}
