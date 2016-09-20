package nl.unionsoft.sysstate.logic;

import java.util.List;
import java.util.Optional;

import nl.unionsoft.sysstate.common.dto.FilterDto;

public interface FilterLogic {

    public void createOrUpdate(FilterDto filter);
    
    public void delete(Long filterId);

    public List<FilterDto> getFilters();

    public Optional<FilterDto> getFilter(Long filterId);
    
    public void addInstanceToFilter(Long filterId, Long instanceId);
    
    public void removeInstanceFromFilter(Long filterId, Long instanceId);
    
    public void updateFilterSubscriptions();
    
    public void updateFilterSubscriptions(FilterDto filter);
}
