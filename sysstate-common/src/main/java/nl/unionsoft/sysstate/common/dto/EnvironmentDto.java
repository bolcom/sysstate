package nl.unionsoft.sysstate.common.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class EnvironmentDto {
    private Long id;

    @NotNull()
    @Size(min = 1, max = 15)
    private String name;

    private int order;
    private String tags;
    private int defaultInstanceTimeout;

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

}
