package nl.unionsoft.sysstate.api;

import org.apache.commons.lang.StringUtils;

import com.bol.feign.ClientProvider;
import com.bol.feign.callback.JacksonCallback;
import com.bol.feign.provider.StaticUrlProvider;
import com.bol.feign.provider.UrlProvider;

import feign.Feign.Builder;
import feign.auth.BasicAuthRequestInterceptor;

/**
 * Example usage:
 * 
 * <pre>
 * public static void main(String[] args) {
 *     getSysState(&quot;http://localhost:8680&quot;, &quot;admin&quot;, &quot;password&quot;).getInstances();
 * }
 * </pre>
 */
public class SysStateClient {
    public static SysState getSysState(String endpoint) {
        return getSysState(new StaticUrlProvider(endpoint));
    }

    public static SysState getSysState(final UrlProvider urlProvider) {
        return getSysState(urlProvider, null, null);
    }

    public static SysState getSysState(String endpoint, String username, String password) {
        return getSysState(new StaticUrlProvider(endpoint), username, password);
    }

    public static SysState getSysState(final UrlProvider urlProvider, final String username, final String password) {
        return new ClientProvider(urlProvider, new JacksonCallback() {
            @Override
            public void postConfigure(Builder builder) {
                if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password)) {
                    builder.requestInterceptor(new BasicAuthRequestInterceptor(username, password));
                }

            }
        }).create(SysState.class);
    }

    public static void main(String[] args) {
        getSysState("http://localhost:8680", "admin", "password").getInstances();
    }
}
