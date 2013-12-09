package nl.unionsoft.sysstate.logic;

import java.util.List;

import nl.unionsoft.sysstate.common.dto.FilterDto;

public interface FilterLogic {

    public void createOrUpdate(FilterDto filter);
    
    public void delete(Long filterId);

    public List<FilterDto> getFilters();

    public FilterDto getFilter(Long filterId);
}
