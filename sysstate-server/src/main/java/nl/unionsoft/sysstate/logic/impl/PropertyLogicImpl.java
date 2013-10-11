package nl.unionsoft.sysstate.logic.impl;

import java.util.List;
import java.util.Properties;

import nl.unionsoft.sysstate.common.logic.PropertyLogic;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service("propertyLogic")
public class PropertyLogicImpl implements PropertyLogic, InitializingBean {

    public void afterPropertiesSet() throws Exception {
        // TODO Auto-generated method stub

    }

    public void setProperty(final String key, final String value) {
        // TODO Auto-generated method stub

    }

    public String getProperty(final String key, final String defaultValue) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Properties> getProperties() {
        // TODO Auto-generated method stub
        return null;
    }
    //    @Inject
    //    @Named("propertyDao")
    //    private PropertyDao propertyDao;
    //
    //    public void setProperty(final String key, final String value) {
    //        propertyDao.setProperty(key, value);
    //    }
    //
    //    public String getProperty(final String key, final String defaultValue) {
    //        return propertyDao.getProperty(key, defaultValue);
    //    }
    //
    //
    //    public void registerProperty(final String key, final String defaultValue) {
    //        final String value = propertyDao.getProperty(key, null);
    //        if (StringUtils.isEmpty(value)) {
    //            propertyDao.setProperty(key, defaultValue);
    //        }
    //    }
    //
    //    public void afterPropertiesSet() throws Exception {
    //        registerProperty(Constants.MAX_DAYS_TO_KEEP_STATES, String.valueOf(Constants.MAX_DAYS_TO_KEEP_STATES_VALUE));
    //        registerProperty(Constants.DEFAULT_TEMPLATE, Constants.DEFAULT_TEMPLATE_VALUE);
    //        registerProperty(Constants.MAINTENANCE_MODE, Constants.MAINTENANCE_MODE_VALUE);
    //    }
    //
    //    public List<Properties> getProperties() {
    //        // TODO Auto-generated method stub
    //        return null;
    //    }

}
