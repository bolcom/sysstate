package nl.unionsoft.sysstate.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "SSE_ENVIRONMENT")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Environment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", nullable = true)
    private String name;

    @Column(name = "DEFAULT_ORDER", nullable = true)
    private int order;

    @Column(name = "DEFAULT_INSTANCE_TIMEOUT", nullable = true)
    private int defaultInstanceTimeout;

    @Column(name = "TAGS", nullable = true, length = 512)
    private String tags;

    @OneToMany(mappedBy = "environment", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ProjectEnvironment> projectEnvironments;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return StringUtils.upperCase(StringUtils.substring(name, 0, 4));
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(final int order) {
        this.order = order;
    }

    public int getDefaultInstanceTimeout() {
        return defaultInstanceTimeout;
    }

    public void setDefaultInstanceTimeout(int defaultInstanceTimeout) {
        this.defaultInstanceTimeout = defaultInstanceTimeout;
    }

    public List<ProjectEnvironment> getProjectEnvironments() {
        return projectEnvironments;
    }

    public void setProjectEnvironments(List<ProjectEnvironment> projectEnvironments) {
        this.projectEnvironments = projectEnvironments;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

}
