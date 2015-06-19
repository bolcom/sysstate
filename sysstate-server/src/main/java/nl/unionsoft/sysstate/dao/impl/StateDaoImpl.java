package nl.unionsoft.sysstate.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;

import nl.unionsoft.common.list.model.ListRequest;
import nl.unionsoft.common.list.model.ListResponse;
import nl.unionsoft.common.list.worker.ListRequestWorker;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.dao.PropertyDao;
import nl.unionsoft.sysstate.dao.StateDao;
import nl.unionsoft.sysstate.domain.State;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("stateDao")
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class StateDaoImpl implements StateDao {
    private static final Logger LOG = LoggerFactory.getLogger(StateDaoImpl.class);

    @Inject
    @Named("criteriaListRequestWorker")
    private ListRequestWorker listRequestWorker;

    @Inject
    @Named("propertyDao")
    private PropertyDao propertyDao;

    @Inject
    @Named("entityManager")
    private EntityManager entityManager;

    public void createOrUpdate(final State state) {
        if (state.getId() == null) {
            entityManager.persist(state);
        } else {
            entityManager.merge(state);
        }
    }

    public Optional<State> getLastStateForInstance(final Long instanceId) {
        try {
            // @formatter:off
            return Optional.of(entityManager.createNamedQuery("findLastStateForInstance", State.class)
                    .setParameter("instanceId", instanceId)
                    .setMaxResults(1)
                    .getSingleResult());
            // @formatter:on
        } catch (NoResultException nre) {
            // Nothing to see here, move along!
        } catch (EntityNotFoundException e) {
            LOG.warn("Unable to find an state for instanceId [{}] while there should've been one. Maybe it got cleaned up?", instanceId, e);
        }
        return Optional.empty();
    }

    public Optional<State> getLastStateForInstance(final Long instanceId, final StateType stateType) {
        try {
            // @formatter:off
            return Optional.of(entityManager.createNamedQuery("findLastStateForInstanceWithStateType", State.class)
                    .setParameter("instanceId", instanceId)
                    .setParameter("stateType", stateType)
                    .setMaxResults(1)
                    .getSingleResult());
            // @formatter:on
        } catch (NoResultException nre) {
            // Nothing to see here, move along!
        }
        return Optional.empty();
    }

    public List<State> getStates() {
        return entityManager.createQuery("FROM State", State.class).getResultList();
    }

    public void cleanStatesOlderThanDays(int maxDaysToKeepStates) {
        final Date boundary = new DateTime().minusDays(maxDaysToKeepStates).toDate();
        LOG.info("Cleaning states older then {} days (before date {}).", maxDaysToKeepStates, boundary);
        // @formatter:off
        final int removed = entityManager.createQuery(
                "DELETE FROM State " + 
                "WHERE state.creationDate < :maxDateToKeepStates")
                .setParameter("maxDateToKeepStates", boundary).executeUpdate();
        // @formatter:on
        LOG.info("Deleted {} States", removed);
    }

    public ListResponse<State> getStates(final ListRequest listRequest) {
        return listRequestWorker.getResults(State.class, listRequest);
    }

}
