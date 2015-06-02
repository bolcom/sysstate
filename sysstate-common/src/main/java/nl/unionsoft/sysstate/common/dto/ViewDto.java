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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((commonTags == null) ? 0 : commonTags.hashCode());
        result = prime * result + ((filter == null) ? 0 : filter.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((template == null) ? 0 : template.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ViewDto other = (ViewDto) obj;
        if (commonTags == null) {
            if (other.commonTags != null)
                return false;
        } else if (!commonTags.equals(other.commonTags))
            return false;
        if (filter == null) {
            if (other.filter != null)
                return false;
        } else if (!filter.equals(other.filter))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (template == null) {
            if (other.template != null)
                return false;
        } else if (!template.equals(other.template))
            return false;
        return true;
    }

}
