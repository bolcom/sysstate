package nl.unionsoft.sysstate.dao.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import nl.unionsoft.sysstate.dao.ProjectDao;
import nl.unionsoft.sysstate.domain.Project;

import org.apache.commons.lang.StringUtils;
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
        entityManager.remove(entityManager.find(Project.class, projectId));
    }

    public Project findProject(String name) {

        String search = name;
        if (!StringUtils.startsWith(name, "%")) {
            search = "%" + search;
        }
        if (!StringUtils.endsWith(name, "%")) {
            search = search + "%";
        }
        Project project = null;
        try {
            project = entityManager.createQuery("FROM Project project " + "WHERE project.name LIKE :search", Project.class).setParameter("search", search).getSingleResult();
        } catch(final NonUniqueResultException nre) {
            // this is ok..
        } catch(final NoResultException nre) {
            // this is ok..
        }
        return project;
    }
}
