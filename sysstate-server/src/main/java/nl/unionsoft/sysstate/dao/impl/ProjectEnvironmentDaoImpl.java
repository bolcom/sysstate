package nl.unionsoft.sysstate.dao.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import nl.unionsoft.sysstate.dao.ProjectEnvironmentDao;
import nl.unionsoft.sysstate.domain.ProjectEnvironment;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("projectEnvironmentDao")
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ProjectEnvironmentDaoImpl implements ProjectEnvironmentDao {
    private static final Logger LOG = LoggerFactory.getLogger(ProjectEnvironmentDaoImpl.class);
    @Inject
    @Named("entityManager")
    private EntityManager entityManager;

    public void createOrUpdate(final ProjectEnvironment projectEnvironment) {
        if (projectEnvironment.getId() == null) {

            final Long projectId = projectEnvironment.getProject().getId();
            final Long environmentId = projectEnvironment.getEnvironment().getId();
            if (getProjectEnvironment(projectId, environmentId) != null) {
                throw new IllegalStateException("ProjectEnvironment for project with id '{" + projectId + "}' and environment with id '" + environmentId
                        + "' already exists.");
            }
            entityManager.persist(projectEnvironment);
        } else {
            entityManager.merge(projectEnvironment);
        }

    }

    public ProjectEnvironment getProjectEnvironment(final Long projectId, final Long environmentId) {
        ProjectEnvironment result = null;
        try {
            // @formatter:off
            result = entityManager
                    .createQuery("FROM ProjectEnvironment pet " +
                            "WHERE pet.project.id = :projectId " +
                            "AND pet.environment.id = :environmentId", ProjectEnvironment.class)
                            .setParameter("projectId", projectId).setParameter("environmentId", environmentId).setHint("org.hibernate.cacheable", true)
                            .getSingleResult();
            // @formatter:on
        } catch (final NoResultException nre) {
            // Nothing to see here, move along!
        }

        return result;
    }

    public List<ProjectEnvironment> getProjectEnvironments() {

        return entityManager
                .createQuery("FROM ProjectEnvironment prjEnv ORDER BY prjEnv.project.name ASC, prjEnv.environment.order ASC", ProjectEnvironment.class)
                .setHint("org.hibernate.cacheable", true).getResultList();
    }

    public ProjectEnvironment getProjectEnvironment(final Long projectEnvironmentId) {
        return entityManager.find(ProjectEnvironment.class, projectEnvironmentId);
    }

    public ProjectEnvironment getProjectEnvironment(final String projectName, final String environmentName) {
        LOG.debug("Searching for project with name '{}' and environment with name '{}'", new Object[] { projectName, environmentName });
        ProjectEnvironment result = null;
        try {
            // @formatter:off
            result = entityManager
                    .createQuery("FROM ProjectEnvironment pet " +
                            "WHERE pet.project.name = :projectName " +
                            "AND pet.environment.name = :environmentName",
                            ProjectEnvironment.class)
                            .setParameter("projectName",  StringUtils.upperCase(projectName)).setParameter("environmentName",  StringUtils.upperCase(environmentName)).setHint("org.hibernate.cacheable", true)
                            .getSingleResult();
            // @formatter:on
        } catch (final NoResultException nre) {
            LOG.debug("No results found for query!");
            // Nothing to see here, move along!
        }

        return result;
    }
}
