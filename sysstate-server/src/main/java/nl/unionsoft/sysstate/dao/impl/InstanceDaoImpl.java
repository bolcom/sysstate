package nl.unionsoft.sysstate.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import nl.unionsoft.sysstate.dao.InstanceDao;
import nl.unionsoft.sysstate.domain.Instance;
import nl.unionsoft.sysstate.domain.ProjectEnvironment;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("instanceDao")
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class InstanceDaoImpl implements InstanceDao {
    @Inject
    @Named("entityManager")
    private EntityManager entityManager;

    public void createOrUpdate(final Instance instance) {
        if (instance.getId() == null) {
            instance.setCreationDate(new Date());
            entityManager.persist(instance);
        } else {
            entityManager.merge(instance);
        }
    }

    public List<Instance> getInstancesForProjectAndEnvironment(final Long projectId, final Long environmentId) {

        // @formatter:off
        return entityManager
                .createQuery( //
                        "FROM Instance ice " + 
                        "WHERE ice.projectEnvironment.environment.id = :environmentId " + 
                        "AND ice.projectEnvironment.project.id = :projectId", Instance.class)
                        .setParameter("projectId", projectId)
                        .setParameter("environmentId", environmentId)
                        .setHint("org.hibernate.cacheable", true).getResultList();
        // @formatter:on
    }

    public List<Instance> getInstances() {
        return entityManager.createQuery("FROM Instance", Instance.class).setHint("org.hibernate.cacheable", true).getResultList();
    }

    public Optional<Instance> getInstance(final Long instanceId) {
        return Optional.ofNullable(entityManager.find(Instance.class, instanceId));

    }

    public void delete(final Long instanceId) {
        entityManager.remove(entityManager.find(Instance.class, instanceId));

    }

    public List<Instance> getInstancesForProjectAndEnvironment(final String projectName, final String environmentName) {
        // @formatter:off
        return entityManager.createQuery( //
                        "FROM Instance ice " + 
                        "WHERE ice.projectEnvironment.environment.name = :environmentName " + 
                        "AND ice.projectEnvironment.project.name = :projectName", Instance.class)
                        .setParameter("projectName", StringUtils.upperCase(projectName))
                        .setParameter("environmentName",StringUtils.upperCase(environmentName))
                        .setHint("org.hibernate.cacheable", true)
                        .getResultList();
        // @formatter:on;
    }

    public List<Instance> getInstancesForProjectEnvironment(final Long projectEnvironmentId) {
        // @formatter:off
        return entityManager.createQuery( //
                "FROM Instance ice " + "WHERE ice.projectEnvironment.id = :projectEnvironmentId ", Instance.class)
                .setParameter("projectEnvironmentId", projectEnvironmentId).setHint("org.hibernate.cacheable", true).getResultList();
        // @formatter:on
    }

    @Override
    public List<Instance> getInstancesForEnvironment(Long environmentId) {
        // @formatter:off
        return entityManager.createQuery( //
                        "FROM Instance ice " + 
                        "WHERE ice.projectEnvironment.environment.id = :environmentId ", Instance.class)
                        .setParameter("environmentId", environmentId)
                        .setHint("org.hibernate.cacheable", true)
                        .getResultList();
        // @formatter:on;
    }

    @Override
    public Optional<Instance> getInstanceByReference(String reference) {
        try {
        // @formatter:off
         return Optional.of(entityManager.createQuery( //
                        "FROM Instance ice " + 
                        "WHERE ice.reference = :reference", Instance.class)
                        .setParameter("reference", reference)
                        .setHint("org.hibernate.cacheable", true)
                        .getSingleResult());
        } catch (NoResultException e){
            return Optional.empty();
        } catch (NonUniqueResultException e){
            throw new IllegalStateException("More then one result found for reference [" + reference + "]", e);
        }
        // @formatter:on;

    }

}
