package nl.unionsoft.sysstate.logic.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.Constants;
import nl.unionsoft.sysstate.dao.PropertyDao;
import nl.unionsoft.sysstate.domain.Property;
import nl.unionsoft.sysstate.logic.PropertyLogic;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service("propertyLogic")
public class PropertyLogicImpl implements PropertyLogic, InitializingBean {
    @Inject
    @Named("propertyDao")
    private PropertyDao propertyDao;

    public void setProperty(final String key, final String value) {
        propertyDao.setProperty(key, value);
    }

    public String getProperty(final String key, final String defaultValue) {
        return propertyDao.getProperty(key, defaultValue);
    }

    public List<Property> getProperties() {
        return propertyDao.getProperties();
    }

    public void registerProperty(String key, final String defaultValue) {
        final String value = propertyDao.getProperty(key, null);
        if (StringUtils.isEmpty(value)) {
            propertyDao.setProperty(key, defaultValue);
        }
    }

    public void afterPropertiesSet() throws Exception {
        registerProperty(Constants.MAX_DAYS_TO_KEEP_STATES, String.valueOf(Constants.MAX_DAYS_TO_KEEP_STATES_VALUE));
        registerProperty(Constants.DEFAULT_TEMPLATE, Constants.DEFAULT_TEMPLATE_VALUE);
        registerProperty(Constants.MAINTENANCE_MODE, Constants.MAINTENANCE_MODE_VALUE);
    }

}
