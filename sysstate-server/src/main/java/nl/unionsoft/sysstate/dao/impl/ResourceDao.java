package nl.unionsoft.sysstate.dao.impl;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import nl.unionsoft.sysstate.domain.Resource;

@Service("resourceDao")
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ResourceDao {

    @Inject
    @Named("entityManager")
    private EntityManager entityManager;

    public Resource getResource(final Long resourceId) {
        return entityManager.find(Resource.class, resourceId);
    }

    public List<Resource> getResourcesByManager(String manager) {

        return entityManager
                .createQuery( //
                "FROM Resource rce WHERE rce.manager = :manager", Resource.class)
                .setParameter("manager", manager)
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }

    public Optional<Resource> getResourceByNameAndManager(String name, String manager) {
        try {

            return Optional.of(entityManager
                    .createQuery( //
                    "FROM Resource rce WHERE rce.name = :name AND rce.manager = :manager", Resource.class)
                    .setParameter("name", name)
                    .setParameter("manager", manager)
                    .setHint("org.hibernate.cacheable", true)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (NonUniqueResultException e) {
            throw new IllegalStateException("More then one result found for name [" + name + "] and manager [" + manager + "].", e);
        }
    }

    public void createOrUpdate(final Resource resource) {
        if (resource.getId() == null) {
            entityManager.persist(resource);
        } else {
            entityManager.merge(resource);
        }
    }

    public void delete(final Long resource) {
        entityManager.remove(entityManager.find(Resource.class, resource));
    }

}
