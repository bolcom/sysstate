package nl.unionsoft.sysstate.domain;

import java.util.ArrayList;
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
@Table(name = "SSE_PROJECT")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", nullable = true, length = 200)
    private String name;

    @Column(name = "DEFAULT_ORDER", nullable = true)
    private int order;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ProjectEnvironment> projectEnvironments;

    @Column(name = "ENABLED", nullable = false, columnDefinition = "BIT default 1")
    private boolean enabled;

    public Project() {
        projectEnvironments = new ArrayList<ProjectEnvironment>();
        enabled = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return StringUtils.upperCase(name);
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

    public List<ProjectEnvironment> getProjectEnvironments() {
        return projectEnvironments;
    }

    public void setProjectEnvironments(final List<ProjectEnvironment> projectEnvironments) {
        this.projectEnvironments = projectEnvironments;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
