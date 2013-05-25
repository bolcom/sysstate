package nl.unionsoft.sysstate.dao.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import nl.unionsoft.sysstate.dao.ProjectEnvironmentDao;
import nl.unionsoft.sysstate.domain.ProjectEnvironment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("projectEnvironmentDao")
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ProjectEnvironmentDaoImpl implements ProjectEnvironmentDao {

    @Inject
    @Named("entityManager")
    private EntityManager entityManager;

    public void createOrUpdate(ProjectEnvironment projectEnvironment) {
        if (projectEnvironment.getId() == null) {

            final Long projectId = projectEnvironment.getProject().getId();
            final Long environmentId = projectEnvironment.getEnvironment().getId();
            if (getProjectEnvironment(projectId, environmentId) != null) {
                throw new IllegalStateException("ProjectEnvironment for project with id '{" + projectId + "}' and environment with id '" + environmentId + "' already exists.");
            }
            entityManager.persist(projectEnvironment);
        } else {
            entityManager.merge(projectEnvironment);
        }

    }

    public ProjectEnvironment getProjectEnvironment(Long projectId, Long environmentId) {
        ProjectEnvironment result = null;
        try {
            // @formatter:off
            result =  entityManager.createQuery(
            "FROM ProjectEnvironment pet " +
            "WHERE pet.project.id = :projectId " +
            "AND pet.environment.id = :environmentId", ProjectEnvironment.class)
            .setParameter("projectId", projectId)
            .setParameter("environmentId", environmentId)
            .setHint("org.hibernate.cacheable", true)
            .getSingleResult();
            // @formatter:on
        } catch(final NoResultException nre) {
            // Nothing to see here, move along!
        }

        return result;
    }

    public List<ProjectEnvironment> getProjectEnvironments() {

        return entityManager.createQuery("FROM ProjectEnvironment prjEnv ORDER BY prjEnv.project.name ASC, prjEnv.environment.order ASC", ProjectEnvironment.class)
            .setHint("org.hibernate.cacheable", true).getResultList();
    }

}