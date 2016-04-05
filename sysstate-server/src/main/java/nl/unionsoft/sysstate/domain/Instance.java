package nl.unionsoft.sysstate.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
//@formatter:off
@Table(name = "SSE_INSTANCE", indexes = { 
        @Index(columnList = "NAME"), 
        @Index(columnList = "TAGS"),
        @Index(columnList = "HOMEPAGE_URL"),
        @Index(columnList = "PLUGIN")
        })
//@formatter:on
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Instance {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "REFERENCE", nullable = true, length = 200)
    private String reference;

    @Column(name = "NAME", nullable = true, length = 200)
    private String name;

    @Column(name = "HOMEPAGE_URL", nullable = true, length = 4096)
    private String homepageUrl;

    @Column(name = "REFRESH_TIMEOUT", nullable = true)
    private int refreshTimeout;

    @Column(name = "TAGS", nullable = true, length = 512)
    private String tags;

    @Column(name = "PLUGIN", nullable = true, length = 512)
    private String pluginClass;

    @Column(name = "ENABLED", nullable = false, columnDefinition = "BIT")
    private boolean enabled;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creationDate", nullable = true)
    private Date creationDate;

    @OneToMany(mappedBy = "instance", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<State> states;

    @OneToMany(mappedBy = "instance", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<InstanceProperty> instanceProperties;

    @OneToMany(mappedBy = "to", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<InstanceLink> incommingInstanceLinks;

    @OneToMany(mappedBy = "from", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<InstanceLink> outgoingInstanceLinks;

    @ManyToOne
    @JoinColumn(name = "PET_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_PROJECT_ENVIRONMENT_INSTANCE"))
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

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public List<InstanceLink> getIncommingInstanceLinks() {
        return incommingInstanceLinks;
    }

    public void setIncommingInstanceLinks(List<InstanceLink> incommingInstanceLinks) {
        this.incommingInstanceLinks = incommingInstanceLinks;
    }

    public List<InstanceLink> getOutgoingInstanceLinks() {
        return outgoingInstanceLinks;
    }

    public void setOutgoingInstanceLinks(List<InstanceLink> outgoingInstanceLinks) {
        this.outgoingInstanceLinks = outgoingInstanceLinks;
    }

}
