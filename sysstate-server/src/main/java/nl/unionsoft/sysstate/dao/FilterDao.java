package nl.unionsoft.sysstate.dao;

import java.util.List;
import java.util.Optional;

import nl.unionsoft.sysstate.domain.Filter;

public interface FilterDao {
    public void createOrUpdate(Filter filter);

    public List<Filter> getFilters();

    public Optional<Filter> getFilter(Long filterId);

    public void delete(Long filterId);

    public void addInstanceToFilter(Long filterId, Long instanceId);
    
    public void removeInstanceFromFilter(Long filterId, Long instanceId);

    public void notifyFilterQueried(Long filterId, Long queryTime);
    
}
