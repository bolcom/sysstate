package nl.unionsoft.sysstate.domain;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "SSE_TEMPLATE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Template implements Serializable {

    private static final long serialVersionUID = 4470429386271283974L;

    @Id()
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(name = "NAME", length = 128, nullable = false, unique = true)
    private String name;

    @Column(name = "WRITER", nullable = false, length = 512)
    private String writer;

    @Column(name = "CONTENT_TYPE", nullable = false, length = 512)
    private String contentType;
    
    @Column(name = "RESOURCE", nullable = false, length = 1024)
    private String resource;
    
    
    @Column(name = "INCLUDE_VIEW_RESULTS", nullable = false)
    private Boolean includeViewResults;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_UPDATED", nullable = false)
    private Date lastUpdated;

    @OneToMany(mappedBy = "template", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<View> views;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public List<View> getViews() {
        return views;
    }

    public void setViews(List<View> views) {
        this.views = views;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public Boolean getIncludeViewResults() {
        return includeViewResults;
    }

    public void setIncludeViewResults(Boolean includeViewResults) {
        this.includeViewResults = includeViewResults;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    

}
