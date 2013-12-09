package nl.unionsoft.sysstate.converter;

import java.util.List;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.sysstate.common.dto.FilterDto;
import nl.unionsoft.sysstate.common.dto.ViewDto;
import nl.unionsoft.sysstate.domain.Filter;
import nl.unionsoft.sysstate.domain.View;

import org.springframework.stereotype.Service;

@Service("filterConverter")
public class FilterConverter implements Converter<FilterDto, Filter> {

    public FilterDto convert(Filter filter) {
        FilterDto result = null;
        if (filter != null) {
            result = new FilterDto();
            result.setEnvironments(filter.getEnvironments());
            result.setId(filter.getId());
            result.setName(filter.getName());
            result.setProjects(filter.getProjects());
            result.setSearch(filter.getSearch());
            result.setStateResolvers(filter.getStateResolvers());
            result.setStates(filter.getStates());
            result.setTags(filter.getTags());
            List<View> views = filter.getViews();
            if (views != null){
                List<ViewDto> viewDtos = result.getViews();
                for (View view : views){
                    ViewDto viewDto  =new ViewDto();
                    viewDto.setName(view.getName());
                    viewDto.setId(view.getId());
                    viewDtos.add(viewDto);
                }
            }
        }
        return result;
    }

}
