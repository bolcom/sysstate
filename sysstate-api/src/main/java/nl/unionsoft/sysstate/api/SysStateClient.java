package nl.unionsoft.sysstate.api;

import nl.unionsoft.sysstate.common.Constants;

import org.apache.commons.lang.StringUtils;

import com.bol.feign.ClientProvider;
import com.bol.feign.HeaderRequestInterceptor;
import com.bol.feign.callback.JacksonCallback;
import com.bol.feign.provider.StaticUrlProvider;
import com.bol.feign.provider.UrlProvider;

import feign.Feign.Builder;
import feign.auth.BasicAuthRequestInterceptor;


public class SysStateClient {
    public static SysState getSysState(String endpoint) {
        return getSysState(new StaticUrlProvider(endpoint));
    }

    public static SysState getSysState(final UrlProvider urlProvider) {
        return getSysState(urlProvider, null);
    }

    public static SysState getSysState(String endpoint, String token) {
        return getSysState(new StaticUrlProvider(endpoint), token);
    }

    public static SysState getSysState(final UrlProvider urlProvider, final String token) {
        return new ClientProvider(urlProvider, new JacksonCallback() {
            @Override
            public void postConfigure(Builder builder) {
                if (StringUtils.isNotEmpty(token)) {
                    builder.requestInterceptor(new HeaderRequestInterceptor(Constants.SECURITY_TOKEN_HEADER, token));
                }
            }
        }).create(SysState.class);
    }
    
}
