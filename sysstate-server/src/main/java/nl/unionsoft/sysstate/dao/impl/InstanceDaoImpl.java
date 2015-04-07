package nl.unionsoft.sysstate.dao.impl;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import nl.unionsoft.sysstate.dao.InstanceDao;
import nl.unionsoft.sysstate.domain.Instance;
import nl.unionsoft.sysstate.domain.InstanceProperty;
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
            instance.setProjectEnvironment(entityManager.find(ProjectEnvironment.class, instance.getProjectEnvironment().getId()));
            instance.setCreationDate(new Date());
            entityManager.persist(instance);
        } else {
            final Instance editInstance = getInstance(instance.getId());
            editInstance.setName(instance.getName());
            editInstance.setHomepageUrl(instance.getHomepageUrl());
            editInstance.setRefreshTimeout(instance.getRefreshTimeout());
            editInstance.setPluginClass(instance.getPluginClass());
            editInstance.setProjectEnvironment(entityManager.find(ProjectEnvironment.class, instance.getProjectEnvironment().getId()));
            editInstance.setEnabled(instance.isEnabled());
            editInstance.setTags(instance.getTags());
            editInstance.setCreationDate(instance.getCreationDate());
            entityManager.merge(editInstance);
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

    public Instance getInstance(final Long instanceId) {

        Instance result = entityManager.find(Instance.class, instanceId);
        
//        List<InstanceProperty> instanceProperties = entityManager.createQuery("FROM InstanceProperty WHERE instance = :instance", InstanceProperty.class).setParameter("instance", result).getResultList();
//        System.out.println("Found:" + instanceProperties.size());
//        for (InstanceProperty instanceProperty : instanceProperties) {
//            System.out.println(instanceProperty.getInstance().getId());
//            System.out.println(instanceProperty.getKey() + ": " + instanceProperty.getValue());
//
//        }
//        System.out.println("Relational Lookup:" + result.getInstanceProperties().size());
        return result;
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

}
