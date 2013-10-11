package nl.unionsoft.sysstate.logic;

import nl.unionsoft.sysstate.common.extending.InstanceConfiguration;
import nl.unionsoft.sysstate.common.extending.StateResolver;

public interface StateResolverLogic {
    public <T extends StateResolver> T getStateResolver(String name);

    public String[] getStateResolverNames();

    public StateResolverMeta getStateResolverMeta(String stateResolverName);

    public class StateResolverMeta {

        private String name;

        private Class<? extends InstanceConfiguration> configurationClass;

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public Class<? extends InstanceConfiguration> getConfigurationClass() {
            return configurationClass;
        }

        public void setConfigurationClass(final Class<? extends InstanceConfiguration> configurationClass) {
            this.configurationClass = configurationClass;
        }

    }
}
