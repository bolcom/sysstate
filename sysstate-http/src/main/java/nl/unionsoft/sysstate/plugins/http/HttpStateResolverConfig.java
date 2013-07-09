package nl.unionsoft.sysstate.plugins.http;

import nl.unionsoft.common.param.Param;

public class HttpStateResolverConfig {

    private String url;

    @Param(title="URL")
    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

}
