package nl.unionsoft.sysstate.plugins.selfdiagnose;

import nl.unionsoft.common.param.Param;
import nl.unionsoft.sysstate.plugins.http.HttpStateResolverConfig;

public class SelfDiagnoseStateResolverConfig extends HttpStateResolverConfig {

    private String pattern;

    @Param(title = "Match Pattern")
    public String getPattern() {
        return pattern;
    }

    public void setPattern(final String pattern) {
        this.pattern = pattern;
    }

}
