package nl.unionsoft.sysstate.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import nl.unionsoft.sysstate.dao.FilterDao;
import nl.unionsoft.sysstate.domain.Filter;
import nl.unionsoft.sysstate.domain.FilterInstance;
import nl.unionsoft.sysstate.domain.Instance;

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

    public Optional<Filter> getFilter(Long filterId) {
        return Optional.ofNullable(entityManager.find(Filter.class, filterId));
    }

    public void delete(Long filterId) {
        entityManager.remove(entityManager.find(Filter.class, filterId));
    }

    @Override
    public void addInstanceToFilter(Long filterId, Long instanceId) {
        //@formatter:on
        List<FilterInstance> filterInstances = entityManager.createQuery("FROM FilterInstance fi "
                + "WHERE fi.instance.id = :instanceId "
                + "AND fi.filter.id = :filterId", FilterInstance.class)
                .setParameter("instanceId", instanceId)
                .setParameter("filterId", filterId)
                .getResultList();
        //@formatter:off
        if (filterInstances.isEmpty()){
            FilterInstance filterInstance = new FilterInstance();
            filterInstance.setInstance(entityManager.find(Instance.class, instanceId));
            filterInstance.setFilter(entityManager.find(Filter.class, filterId));
            entityManager.persist(filterInstance);
        }

        
    }

    @Override
    public void removeInstanceFromFilter(Long filterId, Long instanceId) {
    	//@formatter:off
        entityManager.createQuery("DELETE FROM FilterInstance fi WHERE fi.instance.id = :instanceId AND fi.filter.id = :filterId")
        .setParameter("instanceId", instanceId)
        .setParameter("filterId", filterId)
        .executeUpdate();
    	//@formatter:on
    }

    @Override
    public void notifyFilterQueried(Long filterId) {
        //@formatter:off
        entityManager.createQuery("UPDATE Filter SET lastQueryDate = :lastQueryDate WHERE id = :id")
        .setParameter("lastQueryDate", new Date())
        .setParameter("id", filterId)
        .executeUpdate();
        //@formatter:on
    }

}
