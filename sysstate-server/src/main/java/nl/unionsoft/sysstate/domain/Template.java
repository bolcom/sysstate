package nl.unionsoft.sysstate.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "SSE_TEMPLATE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Template implements Serializable {

    private static final long serialVersionUID = 4470429386271283974L;

    @Id
    @Column(name = "ID")
    private String id;

    @Lob()
    @Column(name = "CSS", nullable = true)
    private String css;

    @Column(name = "NAME", nullable = true, length = 256)
    private String layout;

    @Column(name = "RENDER_HINTS", nullable = true, length = 1024)
    private String renderHints;

    @Column(name = "REFRESH", nullable = true)
    private int refresh;
    @Column(name = "SYSTEM_TEMPLATE", nullable = false)
    private boolean systemTemplate;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getCss() {
        return css;
    }

    public void setCss(final String css) {
        this.css = css;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(final String layout) {
        this.layout = layout;
    }

    public int getRefresh() {
        return refresh;
    }

    public void setRefresh(final int refresh) {
        this.refresh = refresh;
    }

    public String getRenderHints() {
        return renderHints;
    }

    public void setRenderHints(final String renderHints) {
        this.renderHints = renderHints;
    }

    public boolean isSystemTemplate() {
        return systemTemplate;
    }

    public void setSystemTemplate(boolean systemTemplate) {
        this.systemTemplate = systemTemplate;
    }

}
