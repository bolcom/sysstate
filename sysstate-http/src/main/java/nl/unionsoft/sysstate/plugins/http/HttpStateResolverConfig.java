package nl.unionsoft.sysstate.plugins.http;

import nl.unionsoft.common.param.Param;
import nl.unionsoft.sysstate.common.extending.InstanceConfiguration;

public class HttpStateResolverConfig implements InstanceConfiguration {

    private String url;

    private String userAgent;

    public HttpStateResolverConfig() {

    }

    public HttpStateResolverConfig(final String url) {
        this.url = url;
    }

    @Param(title = "URL")
    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    /**
     * @return the userAgent
     */
    @Param(title = "User Agent")
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * @param userAgent
     *            the userAgent to set
     */
    public void setUserAgent(final String userAgent) {
        this.userAgent = userAgent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "HttpStateResolverConfig [url=" + url + ", userAgent=" + userAgent + "]";
    }

}
