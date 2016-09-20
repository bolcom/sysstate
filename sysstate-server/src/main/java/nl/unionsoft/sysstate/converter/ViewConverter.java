package nl.unionsoft.sysstate.converter;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.sysstate.common.dto.FilterDto;
import nl.unionsoft.sysstate.common.dto.TemplateDto;
import nl.unionsoft.sysstate.common.dto.ViewDto;
import nl.unionsoft.sysstate.domain.Filter;
import nl.unionsoft.sysstate.domain.Template;
import nl.unionsoft.sysstate.domain.View;

import org.springframework.stereotype.Service;

@Service("viewConverter")
public class ViewConverter implements Converter<ViewDto, View> {

    @Inject
    @Named("filterConverter")
    private Converter<FilterDto, Filter> filterConverter;

    @Inject
    @Named("templateConverter")
    private Converter<TemplateDto, Template> templateConverter;
    
    public ViewDto convert(View view) {
        ViewDto result = null;
        if (view != null) {
            result = new ViewDto();
            result.setName(view.getName());
            result.setTemplate(templateConverter.convert(view.getTemplate()));
            result.setLastRequestDate(view.getLastRequestDate());
            result.setAverageRequestTime(view.getAverageRequestTime());
            result.setRequestCount(view.getRequestCount());
            result.setLastRequestTime(view.getLastRequestTime());
            final Filter filter = view.getFilter();
            if (filter != null) {
                result.setFilter(filterConverter.convert(filter));
            }
            result.setCommonTags(view.getCommonTags());
        }
        return result;
    }

}
