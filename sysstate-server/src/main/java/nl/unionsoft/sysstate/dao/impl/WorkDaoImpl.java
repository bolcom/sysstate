package nl.unionsoft.sysstate.dao.impl;

import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import nl.unionsoft.sysstate.dao.WorkDao;
import nl.unionsoft.sysstate.domain.Work;

@Service("workDao")
@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
public class WorkDaoImpl implements WorkDao {

    private final EntityManager entityManager;

    @Inject
    public WorkDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Work> getWork(String reference) {

        try {
            return Optional.of(entityManager
                    .createQuery( //
                            "FROM Work wrk WHERE wrk.reference = :reference ", Work.class)
                    .setParameter("reference", reference)
                    .setHint("org.hibernate.cacheable", true)
                    .getSingleResult());
        } catch (NoResultException nre) {
            return Optional.empty();
        }
    }

    @Override
    public void createOrUpdateWork(Work work) {
        if (work.getId() == null) {
            entityManager.persist(work);
        } else {
            entityManager.merge(work);
        }

    }

}
