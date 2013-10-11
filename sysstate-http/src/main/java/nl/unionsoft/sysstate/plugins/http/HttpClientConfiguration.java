package nl.unionsoft.sysstate.plugins.http;

import nl.unionsoft.sysstate.common.extending.GlobalConfiguration;

public class HttpClientConfiguration implements GlobalConfiguration {

    private String connectionTimeoutMillis;
    private String socketTimeoutMillis;

    /**
     * @return the connectionTimeoutMillis
     */
    public String getConnectionTimeoutMillis() {
        return connectionTimeoutMillis;
    }

    /**
     * @param connectionTimeoutMillis
     *            the connectionTimeoutMillis to set
     */
    public void setConnectionTimeoutMillis(final String connectionTimeoutMillis) {
        this.connectionTimeoutMillis = connectionTimeoutMillis;
    }

    /**
     * @return the socketTimeoutMillis
     */
    public String getSocketTimeoutMillis() {
        return socketTimeoutMillis;
    }

    /**
     * @param socketTimeoutMillis
     *            the socketTimeoutMillis to set
     */
    public void setSocketTimeoutMillis(final String socketTimeoutMillis) {
        this.socketTimeoutMillis = socketTimeoutMillis;
    }

}
