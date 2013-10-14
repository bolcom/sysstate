package nl.unionsoft.sysstate.web.mvc.form;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import nl.unionsoft.sysstate.common.dto.StateDto;

public class InstanceForm {
    private Long id;

    @NotNull()
    @Size(min = 1, max = 15)
    private String name;

    private StateDto state;

    private String homepageUrl;

    @NotNull()
    @Size(min = 1)
    private String pluginClass;

    @NotNull()
    @Size(min = 1)
    private String pluginConfiguredByClass;

    private boolean enabled;
    private String tags;

    @Min(10000L)
    private int refreshTimeout;

    @Min(1L)
    private long projectEnvironmentId;

    private Map<String, String> instanceConfigurationParams;

    public InstanceForm() {
        instanceConfigurationParams = new HashMap<String, String>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StateDto getState() {
        return state;
    }

    public void setState(StateDto state) {
        this.state = state;
    }

    public String getHomepageUrl() {
        return homepageUrl;
    }

    public void setHomepageUrl(String homepageUrl) {
        this.homepageUrl = homepageUrl;
    }

    public String getPluginClass() {
        return pluginClass;
    }

    public void setPluginClass(String pluginClass) {
        this.pluginClass = pluginClass;
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

    public int getRefreshTimeout() {
        return refreshTimeout;
    }

    public void setRefreshTimeout(int refreshTimeout) {
        this.refreshTimeout = refreshTimeout;
    }

    public long getProjectEnvironmentId() {
        return projectEnvironmentId;
    }

    public void setProjectEnvironmentId(long projectEnvironmentId) {
        this.projectEnvironmentId = projectEnvironmentId;
    }

    public String getPluginConfiguredByClass() {
        return pluginConfiguredByClass;
    }

    public void setPluginConfiguredByClass(String pluginConfiguredByClass) {
        this.pluginConfiguredByClass = pluginConfiguredByClass;
    }

    public Map<String, String> getInstanceConfigurationParams() {
        return instanceConfigurationParams;
    }

    public void setInstanceConfigurationParams(Map<String, String> instanceConfigurationParams) {
        this.instanceConfigurationParams = instanceConfigurationParams;
    }


}
