package nl.unionsoft.sysstate.dao.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import nl.unionsoft.sysstate.dao.PropertyDao;
import nl.unionsoft.sysstate.domain.GroupProperty;
import nl.unionsoft.sysstate.domain.Instance;
import nl.unionsoft.sysstate.domain.InstanceProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("propertyDao")
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class PropertyDaoImpl implements PropertyDao {
    private static final Logger LOG = LoggerFactory.getLogger(PropertyDaoImpl.class);
    @Inject
    @Named("entityManager")
    private EntityManager entityManager;

    public void setInstanceProperty(final Instance instance, final String key, final String value) {

        try {
            // @formatter:off
            InstanceProperty result=  entityManager.createQuery(
                    "FROM InstanceProperty" +
                            " WHERE instance = :instance " +
                            "AND key = :key", InstanceProperty.class)
                            .setParameter("instance", instance)
                            .setParameter("key", key)
                            .getSingleResult();
            // @formatter: on
            result.setValue(value);
        } catch (final NoResultException nre) {
            InstanceProperty result = new InstanceProperty();
            result.setKey(key);
            result.setValue(value);
            result.setInstance(instance);
            instance.getInstanceProperties().add(result);
            entityManager.persist(result);
        }
    }


    public void setGroupProperty(String group, String key, String value) {
        try {
            // @formatter:off
            GroupProperty result=  entityManager.createQuery(
                    "FROM GroupProperty " +
                            "WHERE group = :group " +
                            "AND key = :key", GroupProperty.class)
                            .setParameter("group", group)
                            .setParameter("key", key)
                            .getSingleResult();
            // @formatter: on
            result.setValue(value);
        } catch (final NoResultException nre) {
            GroupProperty result = new GroupProperty();
            result.setGroup(group);
            result.setKey(key);
            result.setValue(value);
            entityManager.persist(result);
        }

        
    }

    public List<GroupProperty> getGroupProperties(String group) {
        // @formatter:off
        return  entityManager.createQuery(
                    "FROM GroupProperty "+ 
                    "WHERE group = :group ", GroupProperty.class)
                    .setParameter("group", group)
                    .getResultList();
        // @formatter:on
    }

    public GroupProperty getGroupProperty(String group, String key) {

        GroupProperty result = null;
        try {
            // @formatter:off
            result = entityManager.createQuery(
                "FROM GroupProperty "+ 
                "WHERE group = :group AND key = :key ", GroupProperty.class)
                .setParameter("group", group)
                .setParameter("key", key)
                .getSingleResult();
            // @formatter:on
        } catch (final NoResultException nre) {
            // Nothing to see here, move along!
        }
        return result;

    }

}
