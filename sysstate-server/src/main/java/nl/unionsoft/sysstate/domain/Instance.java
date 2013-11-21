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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "SSE_INSTANCE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Instance {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", nullable = true, length = 200)
    private String name;

    @Column(name = "HOMEPAGE_URL", nullable = true, length = 4096)
    private String homepageUrl;

    @Column(name = "CONFIGURATION", nullable = true, length = 8192)
    @Deprecated
    private String configuration;

    @Column(name = "REFRESH_TIMEOUT", nullable = true)
    private int refreshTimeout;

    @Column(name = "TAGS", nullable = true, length = 512)
    private String tags;

    @Column(name = "PLUGIN", nullable = true, length = 512)
    private String pluginClass;

    @Column(name = "ENABLED", nullable = false, columnDefinition="BIT")
    private boolean enabled;

    @OneToMany(mappedBy = "instance", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<State> states;

    @OneToMany(mappedBy = "instance", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<InstanceProperty> instanceProperties;

    // @OneToOne()
    // @JoinColumn(name="LAST_STE_ID")
    // private State lastState;
    //
    // @OneToMany(mappedBy = "instance", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    // private List<InstanceWorkerPluginConfig> instanceWorkerPluginConfigs;

    @ManyToOne
    @JoinColumn(name = "PET_ID", nullable = false)
    private ProjectEnvironment projectEnvironment;

    public Instance() {
        instanceProperties = new ArrayList<InstanceProperty>();
    }

    /**
     * @return the instanceProperties
     */
    public List<InstanceProperty> getInstanceProperties() {
        return instanceProperties;
    }

    /**
     * @param instanceProperties
     *            the instanceProperties to set
     */
    public void setInstanceProperties(final List<InstanceProperty> instanceProperties) {
        this.instanceProperties = instanceProperties;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getPluginClass() {
        return pluginClass;
    }

    public void setPluginClass(final String pluginClass) {
        this.pluginClass = pluginClass;
    }

    public List<State> getStates() {
        return states;
    }

    public void setStates(final List<State> states) {
        this.states = states;
    }

    public int getRefreshTimeout() {
        return refreshTimeout;
    }

    public void setRefreshTimeout(final int refreshTimeout) {
        this.refreshTimeout = refreshTimeout;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(final String tags) {
        this.tags = StringUtils.lowerCase(tags);
    }

    public String getHomepageUrl() {
        return homepageUrl;
    }

    public void setHomepageUrl(final String homepageUrl) {
        this.homepageUrl = homepageUrl;
    }

    public ProjectEnvironment getProjectEnvironment() {
        return projectEnvironment;
    }

    public void setProjectEnvironment(final ProjectEnvironment projectEnvironment) {
        this.projectEnvironment = projectEnvironment;
    }

    @Override
    public String toString() {
        return "Instance [id=" + id + ", name=" + name + "]";
    }

    @Deprecated
    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }
    
    

}
