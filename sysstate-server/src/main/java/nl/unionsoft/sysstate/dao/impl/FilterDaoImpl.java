package nl.unionsoft.sysstate.dao.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import nl.unionsoft.sysstate.dao.FilterDao;
import nl.unionsoft.sysstate.domain.Filter;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("filterDao")
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class FilterDaoImpl implements FilterDao {

    @Inject
    @Named("entityManager")
    private EntityManager entityManager;

    public void createOrUpdate(Filter filter) {
        if (filter.getId() == null) {
            entityManager.persist(filter);
        } else {
            entityManager.merge(filter);
        }

    }

    public List<Filter> getFilters() {
        return entityManager.createQuery("FROM Filter", Filter.class).getResultList();
    }

    public Filter getFilter(Long filterId) {
        return entityManager.find(Filter.class, filterId);
    }

    public void delete(Long filterId) {
        entityManager.remove(entityManager.find(Filter.class, filterId));
    }

}
