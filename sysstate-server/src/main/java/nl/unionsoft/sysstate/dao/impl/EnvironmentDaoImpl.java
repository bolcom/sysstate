package nl.unionsoft.sysstate.dao.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import nl.unionsoft.sysstate.dao.EnvironmentDao;
import nl.unionsoft.sysstate.domain.Environment;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("environmentDao")
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class EnvironmentDaoImpl implements EnvironmentDao {
    @Inject
    @Named("entityManager")
    private EntityManager entityManager;

    public List<Environment> getEnvironments() {
        //@formatter:off
        return entityManager.createQuery( //
                "FROM Environment environment ORDER by order ASC ", Environment.class)
                .setHint("org.hibernate.cacheable", true)
                
                .getResultList();
    }

    public Environment getEnvironment(final Long environmentId) {
        return entityManager.find(Environment.class, environmentId);
    }

    public void createOrUpdate(final Environment environment) {
        if (environment.getId() == null) {
            entityManager.persist(environment);

        } else {
            entityManager.merge(environment);
        }

    }


    public void delete(final Long environmentId) {
        entityManager.remove(entityManager.find(Environment.class, environmentId));
    }

    public Environment findEnvironment(String name) {
            String search = name;
            if (!StringUtils.startsWith(name, "%")) {
                search = "%" + search;
            }
            if (!StringUtils.endsWith(name, "%")) {
                search = search + "%";
            }
            Environment environment = null;
            try {
                environment = entityManager.createQuery(
                    "FROM Environment environment " +
                    "WHERE environment.name LIKE :search", Environment.class)
                    .setParameter("search", search)
                    .getSingleResult();
            } catch (final NonUniqueResultException nre){
                // this is ok..
            } catch(final NoResultException nre) {
                // this is ok..
            }
            return environment;
    }


}
