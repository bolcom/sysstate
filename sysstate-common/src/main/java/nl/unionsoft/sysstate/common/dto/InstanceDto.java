package nl.unionsoft.sysstate.common.dto;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class InstanceDto implements Serializable {

	private static final long serialVersionUID = -4695232477842680459L;

	private Long id;

	private static final int minRefreshTimeOut = 10000;
	@NotNull()
	@Size(min = 1, max = 15)
	private String name;

	private String reference;

	private String homepageUrl;

	private Map<String, String> configuration;

	@NotNull()
	@Size(min = 1)
	private String pluginClass;
	private boolean enabled;
	private String tags;

	@Min(minRefreshTimeOut)
	private int refreshTimeout;



	@NotNull()
	private ProjectEnvironmentDto projectEnvironment;

	public InstanceDto() {
		configuration = new LinkedHashMap<String, String>();
		refreshTimeout = 10000;
	}

	public ProjectEnvironmentDto getProjectEnvironment() {
		return projectEnvironment;
	}

	public void setProjectEnvironment(final ProjectEnvironmentDto projectEnvironment) {
		this.projectEnvironment = projectEnvironment;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getHomepageUrl() {
		return homepageUrl;
	}

	public void setHomepageUrl(final String homepageUrl) {
		this.homepageUrl = homepageUrl;
	}

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getPluginClass() {
		return pluginClass;
	}

	public void setPluginClass(final String pluginClass) {
		this.pluginClass = pluginClass;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	public int getRefreshTimeout() {
		return refreshTimeout;
	}

	public void setRefreshTimeout(final int refreshTimeout) {
		if (refreshTimeout <= minRefreshTimeOut) {
			this.refreshTimeout = minRefreshTimeOut;
		} else {
			this.refreshTimeout = refreshTimeout;
		}

	}

	public String getTags() {
		return tags;
	}

	public void setTags(final String tags) {
		this.tags = tags;
	}

	@Override
	public String toString() {
		return "InstanceDto [id=" + id + ", name=" + name + "]";
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((configuration == null) ? 0 : configuration.hashCode());
		result = prime * result + (enabled ? 1231 : 1237);
		result = prime * result + ((homepageUrl == null) ? 0 : homepageUrl.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((pluginClass == null) ? 0 : pluginClass.hashCode());
		result = prime * result + ((reference == null) ? 0 : reference.hashCode());
		result = prime * result + refreshTimeout;
		result = prime * result + ((tags == null) ? 0 : tags.hashCode());
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
		InstanceDto other = (InstanceDto) obj;
		if (configuration == null) {
			if (other.configuration != null)
				return false;
		} else if (!configuration.equals(other.configuration))
			return false;
		if (enabled != other.enabled)
			return false;
		if (homepageUrl == null) {
			if (other.homepageUrl != null)
				return false;
		} else if (!homepageUrl.equals(other.homepageUrl))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (pluginClass == null) {
			if (other.pluginClass != null)
				return false;
		} else if (!pluginClass.equals(other.pluginClass))
			return false;
		if (reference == null) {
			if (other.reference != null)
				return false;
		} else if (!reference.equals(other.reference))
			return false;
		if (refreshTimeout != other.refreshTimeout)
			return false;
		if (tags == null) {
			if (other.tags != null)
				return false;
		} else if (!tags.equals(other.tags))
			return false;
		return true;
	}

	public Map<String, String> getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Map<String, String> configuration) {
		this.configuration = configuration;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}


}
