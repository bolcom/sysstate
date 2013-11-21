package nl.unionsoft.sysstate.dao.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import nl.unionsoft.sysstate.common.dto.StatisticsDto;
import nl.unionsoft.sysstate.dao.StatisticsDao;
import nl.unionsoft.sysstate.domain.Environment;
import nl.unionsoft.sysstate.domain.Filter;
import nl.unionsoft.sysstate.domain.Instance;
import nl.unionsoft.sysstate.domain.Project;
import nl.unionsoft.sysstate.domain.ProjectEnvironment;
import nl.unionsoft.sysstate.domain.State;
import nl.unionsoft.sysstate.domain.View;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("statisticsDao")
@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class StatisticsDaoImpl implements StatisticsDao {

    @Inject
    @Named("entityManager")
    private EntityManager entityManager;

    public StatisticsDto getStatistics() {
        StatisticsDto statistics = new StatisticsDto();
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        statistics.setProjects(count(criteriaBuilder, Project.class));
        statistics.setEnvironments(count(criteriaBuilder, Environment.class));
        statistics.setInstances(count(criteriaBuilder, Instance.class));
        statistics.setProjectEnvironments(count(criteriaBuilder, ProjectEnvironment.class));
        statistics.setStates(count(criteriaBuilder, State.class));
        statistics.setViews(count(criteriaBuilder, View.class));
        statistics.setFilters(count(criteriaBuilder, Filter.class));
        return statistics;

    }

    private Long count(final CriteriaBuilder criteriaBuilder, final Class<?> theType) {
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<?> root = criteriaQuery.from(theType);
        criteriaQuery = criteriaQuery.select(criteriaBuilder.count(root));
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

}
