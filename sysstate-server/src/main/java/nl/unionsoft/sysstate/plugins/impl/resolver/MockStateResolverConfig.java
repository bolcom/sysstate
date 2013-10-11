package nl.unionsoft.sysstate.plugins.impl.resolver;

import nl.unionsoft.common.param.Param;
import nl.unionsoft.sysstate.common.extending.InstanceConfiguration;

public class MockStateResolverConfig implements InstanceConfiguration{
    private String state;
    private String sleep;
    private String mode;

    @Param(title = "Mock State")
    public String getState() {
        return state;
    }

    public void setState(final String state) {
        this.state = state;
    }

    @Param(title = "Mock Sleep")
    public String getSleep() {
        return sleep;
    }

    public void setSleep(final String sleep) {
        this.sleep = sleep;
    }

    @Param(title = "Mock Mode")
    public String getMode() {
        return mode;
    }

    public void setMode(final String mode) {
        this.mode = mode;
    }

}
