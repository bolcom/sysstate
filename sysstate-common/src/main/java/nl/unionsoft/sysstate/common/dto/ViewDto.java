package nl.unionsoft.sysstate.common.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ViewDto {

    private Long id;

    @NotNull()
    @Size(min = 1, max = 128)
    private String name;

    private TemplateDto template;

    private String commonTags;
    
    private FilterDto filter;

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

    public String getCommonTags() {
        return commonTags;
    }

    public void setCommonTags(final String commonTags) {
        this.commonTags = commonTags;
    }

    public FilterDto getFilter() {
        return filter;
    }

    public void setFilter(final FilterDto filter) {
        this.filter = filter;
    }

    public TemplateDto getTemplate() {
        return template;
    }

    public void setTemplate(TemplateDto template) {
        this.template = template;
    }

}
