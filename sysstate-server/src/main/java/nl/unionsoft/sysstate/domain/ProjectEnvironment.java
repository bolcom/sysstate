package nl.unionsoft.sysstate.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
//@formatter:off
@Table(name = "SSE_PROJECT_ENVIRONMENT", indexes = { 
        @Index(columnList = "HOMEPAGE_URL"), 
        })
//@formatter:on
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ProjectEnvironment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "HOMEPAGE_URL", nullable = true, length = 4096)
    private String homepageUrl;

    @ManyToOne
    @JoinColumn(name = "PJT_ID", foreignKey = @ForeignKey(name = "FK_PROJECT_PROJECT_ENVIRONMENT"))
    private Project project;

    @ManyToOne
    @JoinColumn(name = "EVT_ID", foreignKey = @ForeignKey(name = "FK_ENVIRONMENT_PROJECT_ENVIRONMENT"))
    private Environment environment;

    @OneToMany(mappedBy = "projectEnvironment", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Instance> instances;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public String getHomepageUrl() {
        return homepageUrl;
    }

    public void setHomepageUrl(String homepageUrl) {
        this.homepageUrl = homepageUrl;
    }

    public List<Instance> getInstances() {
        return instances;
    }

    public void setInstances(List<Instance> instances) {
        this.instances = instances;
    }

}
