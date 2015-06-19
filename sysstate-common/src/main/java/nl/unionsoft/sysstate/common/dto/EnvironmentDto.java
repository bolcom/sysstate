package nl.unionsoft.sysstate.common.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class EnvironmentDto implements Serializable {

    private static final long serialVersionUID = 8246343549796642142L;

    private Long id;

    @NotNull()
    @Size(min = 1, max = 15)
    private String name;
    private boolean enabled;
    private int order;
    private String tags;
    private int defaultInstanceTimeout;

    public EnvironmentDto() {
        enabled = true;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(final int order) {
        this.order = order;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(final String tags) {
        this.tags = tags;
    }

    public int getDefaultInstanceTimeout() {
        return defaultInstanceTimeout;
    }

    public void setDefaultInstanceTimeout(final int defaultInstanceTimeout) {
        this.defaultInstanceTimeout = defaultInstanceTimeout;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        EnvironmentDto other = (EnvironmentDto) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

}
