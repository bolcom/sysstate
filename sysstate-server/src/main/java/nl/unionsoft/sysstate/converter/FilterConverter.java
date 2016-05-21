package nl.unionsoft.sysstate.converter;

import org.springframework.stereotype.Service;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.sysstate.common.dto.FilterDto;
import nl.unionsoft.sysstate.domain.Filter;

@Service("filterConverter")
public class FilterConverter implements Converter<FilterDto, Filter> {

    public FilterDto convert(Filter filter) {

        if (filter == null) {
            return null;
        }
        FilterDto result = new FilterDto();
        result.setEnvironments(filter.getEnvironments());
        result.setId(filter.getId());
        result.setName(filter.getName());
        result.setProjects(filter.getProjects());
        result.setSearch(filter.getSearch());
        result.setStateResolvers(filter.getStateResolvers());
        result.setTags(filter.getTags());
        result.setLastQueryDate(filter.getLastQueryDate());
        return result;
    }

}
