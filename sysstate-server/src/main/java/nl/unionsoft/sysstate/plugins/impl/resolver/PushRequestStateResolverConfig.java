package nl.unionsoft.sysstate.plugins.impl.resolver;

import nl.unionsoft.common.param.Param;
import nl.unionsoft.sysstate.common.extending.InstanceConfiguration;

public class PushRequestStateResolverConfig implements InstanceConfiguration {

    private String timeout;

    public PushRequestStateResolverConfig() {

    }

    public PushRequestStateResolverConfig(final String timeout) {
        this.timeout = timeout;
    }

    @Param(title = "Timeout After Ms")
    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(final String timeout) {
        this.timeout = timeout;
    }

}
