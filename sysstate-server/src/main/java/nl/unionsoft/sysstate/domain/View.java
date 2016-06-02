package nl.unionsoft.sysstate.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "SSE_VIEW", indexes = {
        @Index(columnList = "NAME"),
})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class View {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", nullable = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "TPE_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_TEMPLATE_VIEW") )
    private Template template;

    @Column(name = "COMMON_TAGS", nullable = true, length = 512)
    private String commonTags;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_REQUEST_DATE", nullable = true)
    private Date lastRequestDate;

    @Column(name = "REQUEST_COUNT", nullable = false)
    private long requestCount;

    @Column(name = "AVG_REQUEST_TIME", nullable = false)
    private long averageRequestTime;

    @Column(name = "LAST_REQUEST_TIME", nullable = false)
    private long lastRequestTime;

    @ManyToOne
    @JoinColumn(name = "FTR_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_FILTER_VIEW") )
    private Filter filter;

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

    public String getCommonTags() {
        return commonTags;
    }

    public void setCommonTags(String commonTags) {
        this.commonTags = StringUtils.lowerCase(commonTags);
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    public Date getLastRequestDate() {
        return lastRequestDate;
    }

    public void setLastRequestDate(Date lastRequestDate) {
        this.lastRequestDate = lastRequestDate;
    }

    public long getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(long requestCount) {
        this.requestCount = requestCount;
    }

    public long getAverageRequestTime() {
        return averageRequestTime;
    }

    public void setAverageRequestTime(long averageRequestTime) {
        this.averageRequestTime = averageRequestTime;
    }

    public long getLastRequestTime() {
        return lastRequestTime;
    }

    public void setLastRequestTime(long lastRequestTime) {
        this.lastRequestTime = lastRequestTime;
    }

}
