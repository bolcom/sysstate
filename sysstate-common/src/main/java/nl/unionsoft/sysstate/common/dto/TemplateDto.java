package nl.unionsoft.sysstate.common.dto;

import java.util.Date;
import java.util.Properties;

public class TemplateDto {

    private String name;

    private String writer;

    private String contentType;

    private String resource;

    private Boolean includeViewResults;
    
    private Date lastUpdated;

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
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

}
