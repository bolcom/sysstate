package nl.unionsoft.sysstate.dao.impl;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import nl.unionsoft.common.list.model.ListRequest;
import nl.unionsoft.common.list.model.ListResponse;
import nl.unionsoft.common.list.worker.ListRequestWorker;
import nl.unionsoft.sysstate.Constants;
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

    public State getLastStateForInstance(final Long instanceId) {
        State result = null;
        try {
            // @formatter:off
            result = entityManager.createNamedQuery("findLastStateForInstance", State.class).setParameter("instanceId", instanceId).setMaxResults(1)
                    .setHint("org.hibernate.cacheable", true).getSingleResult();
            // @formatter:on
        } catch (NoResultException nre) {
            // Nothing to see here, move along!
        }
        return result;
    }

    public State getLastStateForInstance(final Long instanceId, final StateType stateType) {
        State result = null;
        try {
            // @formatter:off
            result = entityManager.createNamedQuery("findLastStateForInstanceWithStateType", State.class).setParameter("instanceId", instanceId)
                    .setParameter("stateType", stateType).setMaxResults(1).getSingleResult();
            // @formatter:on
        } catch (NoResultException nre) {
            // Nothing to see here, move along!
        }
        return result;
    }

    public List<State> getStates() {
        return entityManager.createQuery("FROM State", State.class).setHint("org.hibernate.cacheable", true).getResultList();

    }

    public void clean() {

        final int maxDaysToKeepStates = propertyDao.getProperty(Constants.MAX_DAYS_TO_KEEP_STATES, Constants.MAX_DAYS_TO_KEEP_STATES_VALUE);
        final Date boundary = new DateTime().minusDays(maxDaysToKeepStates).toDate();
        LOG.info("Cleaning states older then {} days (before date {}).", maxDaysToKeepStates, boundary);
        // @formatter:off
        final int removed = entityManager.createQuery("DELETE FROM State " + "WHERE state.creationDate < :maxDateToKeepStates")
                .setParameter("maxDateToKeepStates", boundary).executeUpdate();
        // @formatter:on
        LOG.info("Deleted {} States", removed);
    }

    public ListResponse<State> getStates(final ListRequest listRequest) {
        return listRequestWorker.getResults(State.class, listRequest);
    }

}
