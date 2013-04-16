package nl.unionsoft.sysstate.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "SSE_VIEW")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class View {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", nullable = true)
    private String name;

    @Column(name = "TEMPLATE", nullable = true, length = 128)
    private String template;

    @Column(name = "COMMON_TAGS", nullable = true, length = 512)
    private String commonTags;

    @ManyToOne
    @JoinColumn(name = "FTR_ID", nullable = true)
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

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
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

}
