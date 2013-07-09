package nl.unionsoft.sysstate.plugins.impl.resolver;

import nl.unionsoft.common.param.Param;

public class PushRequestStateResolverConfig {

    private String timeout;

    @Param(title="Timeout After Ms")
    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(final String timeout) {
        this.timeout = timeout;
    }

}
