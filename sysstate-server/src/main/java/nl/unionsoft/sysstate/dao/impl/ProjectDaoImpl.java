package nl.unionsoft.sysstate.dao.impl;

import static org.apache.commons.lang.StringUtils.upperCase;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import nl.unionsoft.sysstate.dao.ProjectDao;
import nl.unionsoft.sysstate.domain.Project;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("projectDao")
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ProjectDaoImpl implements ProjectDao {

    @Inject
    @Named("entityManager")
    private EntityManager entityManager;

    public List<Project> getProjects() {
        //@formatter:off
        return entityManager.createQuery(
            "From Project project " +
            "ORDER BY name ASC", Project.class)
            .setHint("org.hibernate.cacheable", true)
            .getResultList();
        //@formatter:on
    }

    public Project getProject(final Long projectId) {
        return entityManager.find(Project.class, projectId);
    }

    public void createOrUpdateProject(final Project project) {
        if (project.getId() == null) {
            entityManager.persist(project);
        } else {
            entityManager.merge(project);
        }
    }

    public void delete(final Long projectId) {
        
        Long instanceCount = entityManager.createQuery(
                "SELECT COUNT(instance) FROM Instance instance " + 
                "WHERE instance.projectEnvironment.project.id = :projectId", Long.class)
                .setParameter("projectId", projectId).getSingleResult();
        
        if (instanceCount != 0){
            throw new IllegalStateException("Unable to delete project with id [" + projectId + "], it still contains [" + instanceCount +"] instances.");
        }
        entityManager.remove(entityManager.find(Project.class, projectId));
    }
    
    public Project getProjectByName(String name) {
        Project project = null;
        try {
            //@formatter:off
            project = entityManager.createQuery(
                    "FROM Project project " + 
                    "WHERE project.name = :name", Project.class)
                    .setParameter("name", upperCase(name))
                    .getSingleResult();
            //@formatter:on
        } catch(final NonUniqueResultException nre) {
            throw new IllegalStateException("More then one projects with name [" + upperCase(name) + "] found.", nre);
        } catch(final NoResultException nre) {
            // this is ok..
        }
        return project;
    }
}
