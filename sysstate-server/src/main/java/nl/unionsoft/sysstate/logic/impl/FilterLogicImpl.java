package nl.unionsoft.sysstate.logic.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.common.converter.ListConverter;
import nl.unionsoft.sysstate.common.dto.FilterDto;
import nl.unionsoft.sysstate.dao.FilterDao;
import nl.unionsoft.sysstate.domain.Filter;
import nl.unionsoft.sysstate.logic.FilterLogic;

import org.springframework.stereotype.Service;

@Service("filterLogic")
public class FilterLogicImpl implements FilterLogic {

    @Inject
    @Named("filterConverter")
    private Converter<FilterDto, Filter> filterConverter;

    @Inject
    @Named("filterDao")
    private FilterDao filterDao;

    public List<FilterDto> getFilters() {
        return ListConverter.convert(filterConverter, filterDao.getFilters());
    }

    public FilterDto getFilter(Long filterId) {
        return filterConverter.convert(filterDao.getFilter(filterId));
    }

    public void createOrUpdate(FilterDto dto) {
        final Filter filter = new Filter();
        filter.setId(dto.getId());
        filter.setEnvironments(dto.getEnvironments());
        filter.setName(dto.getName());
        filter.setProjects(dto.getProjects());
        filter.setSearch(dto.getSearch());
        filter.setStateResolvers(dto.getStateResolvers());
        filter.setStates(dto.getStates());
        filter.setTags(dto.getTags());
        filterDao.createOrUpdate(filter);
    }

}
