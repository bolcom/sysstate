package nl.unionsoft.sysstate.common.logic.impl;

import nl.unionsoft.sysstate.common.extending.Configuration;
import nl.unionsoft.sysstate.common.logic.ConfigurationManager;

public class BasicConfigurationManager implements ConfigurationManager {

    private Class<? extends Configuration> configurationClass;

    /**
     * @return the configurationClass
     */
    public Class<? extends Configuration> getConfigurationClass() {
        return configurationClass;
    }

    /**
     * @param configurationClass
     *            the configurationClass to set
     */
    public void setConfigurationClass(final Class<? extends Configuration> configurationClass) {
        this.configurationClass = configurationClass;
    }

}
