package nl.unionsoft.sysstate.common.dto;

public class ViewDto {

    private Long id;
    private String name;
    private String templateId;
    private String commonTags;
    private FilterDto filter;

    public Long getId() {

        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getCommonTags() {
        return commonTags;
    }

    public void setCommonTags(String commonTags) {
        this.commonTags = commonTags;
    }

    public FilterDto getFilter() {
        return filter;
    }

    public void setFilter(FilterDto filter) {
        this.filter = filter;
    }

}
