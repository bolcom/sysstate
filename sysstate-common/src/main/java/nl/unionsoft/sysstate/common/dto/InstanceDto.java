package nl.unionsoft.sysstate.common.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import nl.unionsoft.common.util.PropertiesUtil;

import org.joda.time.DateTime;

public class InstanceDto {

    private Long id;

    @NotNull()
    @Size(min = 1, max = 15)
    private String name;

    private String configuration;

    private StateDto state;

    private String homepageUrl;

    @NotNull()
    @Size(min = 1)
    private String pluginClass;
    private boolean enabled;
    private String tags;

    @Min(10000L)
    private int refreshTimeout;
    private DateTime nextUpdate;

    private StateDto lastStable;
    private StateDto lastUnstable;
    private StateDto lastError;
    private StateDto lastPending;
    private StateDto lastDisabled;

    @NotNull()
    private ProjectEnvironmentDto projectEnvironment;

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(final String configuration) {
        this.configuration = configuration;
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

    public StateDto getState() {
        return state;
    }

    public void setState(final StateDto state) {
        this.state = state;
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
        this.refreshTimeout = refreshTimeout;
    }

    public DateTime getNextUpdate() {
        return nextUpdate;
    }

    public void setNextUpdate(final DateTime nextUpdate) {
        this.nextUpdate = nextUpdate;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(final String tags) {
        this.tags = tags;
    }

    public StateDto getLastStable() {
        return lastStable;
    }

    public void setLastStable(final StateDto lastStable) {
        this.lastStable = lastStable;
    }

    public StateDto getLastUnstable() {
        return lastUnstable;
    }

    public void setLastUnstable(final StateDto lastUnstable) {
        this.lastUnstable = lastUnstable;
    }

    public StateDto getLastError() {
        return lastError;
    }

    public void setLastError(final StateDto lastError) {
        this.lastError = lastError;
    }

    public StateDto getLastPending() {
        return lastPending;
    }

    public void setLastPending(final StateDto lastPending) {
        this.lastPending = lastPending;
    }

    public StateDto getLastDisabled() {
        return lastDisabled;
    }

    public void setLastDisabled(final StateDto lastDisabled) {
        this.lastDisabled = lastDisabled;
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
        result = prime * result + ((pluginClass == null) ? 0 : pluginClass.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final InstanceDto other = (InstanceDto) obj;
        if (configuration == null) {
            if (other.configuration != null) {
                return false;
            }
        } else if (!PropertiesUtil.stringToProperties(configuration).equals(PropertiesUtil.stringToProperties(other.configuration))) {
            return false;
        }
        if (pluginClass == null) {
            if (other.pluginClass != null) {
                return false;
            }
        } else if (!pluginClass.equals(other.pluginClass)) {
            return false;
        }
        return true;
    }

}
