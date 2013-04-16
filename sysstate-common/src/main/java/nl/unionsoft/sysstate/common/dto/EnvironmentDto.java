package nl.unionsoft.sysstate.common.dto;

public class EnvironmentDto {
    private Long id;
    private String name;
    private int order;
    private String tags;
    private int defaultInstanceTimeout;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getDefaultInstanceTimeout() {
        return defaultInstanceTimeout;
    }

    public void setDefaultInstanceTimeout(int defaultInstanceTimeout) {
        this.defaultInstanceTimeout = defaultInstanceTimeout;
    }

}
