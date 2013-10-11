package nl.unionsoft.sysstate.dao.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import nl.unionsoft.sysstate.dao.PropertyDao;
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

    public void setInstanceProperty(final long instanceId, final String key, final String value) {

        try {
            // @formatter:off
            InstanceProperty result=  entityManager.createQuery(
                    "FROM InstanceProperty" +
                            " WHERE instance.id = :instanceId " +
                            "AND key = :key", InstanceProperty.class)
                            .setParameter("instanceId", instanceId)
                            .setParameter("key", key)
                            .getSingleResult();
            // @formatter: on
            result.setValue(value);
        } catch (final NoResultException nre) {
            InstanceProperty result = new InstanceProperty();
            result.setInstance(entityManager.find(Instance.class, instanceId));
            result.setKey(key);
            result.setValue(value);
            entityManager.persist(result);
        }
    }
    // public void setProperty(final String key, final String value) {
    // Property property = entityManager.find(Property.class, key);
    // if (property == null) {
    // property = new Property();
    // property.setKey(key);
    // property.setValue(value);
    // entityManager.persist(property);
    // } else {
    // if (value == null) {
    // entityManager.remove(property);
    // } else {
    // property.setValue(value);
    // entityManager.merge(property);
    // }
    // }
    // }
    //
    // public String getProperty(final String key, final String defaultValue) {
    // String result = null;
    // final Property property = entityManager.find(Property.class, key);
    // if (property != null) {
    // result = property.getValue();
    // }
    // if (StringUtils.isEmpty(result)) {
    // result = defaultValue;
    // }
    // LOG.debug("Property for key '{}' has value '{}'", key, result);
    // return result;
    // }
    //
    // public List<Property> getProperties() {
    // return entityManager.createQuery("From Property", Property.class).setHint("org.hibernate.cacheable", true).getResultList();
    // }
    //
    // public long getProperty(final String key, final long defaultValue) {
    // final String value = getProperty(key, String.valueOf(defaultValue));
    // long result = 0;
    // if (NumberUtils.isNumber(value)) {
    // result = NumberUtils.toLong(value);
    // }
    // return result;
    // }
    //
    // public int getProperty(final String key, final int defaultValue) {
    // final String value = getProperty(key, String.valueOf(defaultValue));
    // int result = 0;
    // if (NumberUtils.isNumber(value)) {
    // result = NumberUtils.toInt(value);
    // }
    // return result;
    // }

}
