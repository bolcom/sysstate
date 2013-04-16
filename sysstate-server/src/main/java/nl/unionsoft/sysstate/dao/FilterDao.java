package nl.unionsoft.sysstate.dao;

import java.util.List;

import nl.unionsoft.sysstate.domain.Filter;

public interface FilterDao {
    public void createOrUpdate(Filter filter);

    public List<Filter> getFilters();

    public Filter getFilter(Long filterId);

}
