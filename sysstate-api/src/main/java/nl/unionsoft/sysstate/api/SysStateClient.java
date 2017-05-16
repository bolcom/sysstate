package nl.unionsoft.sysstate.api;

import org.apache.commons.lang.StringUtils;

import com.bol.feign.ClientProvider;
import com.bol.feign.HeaderRequestInterceptor;
import com.bol.feign.callback.BuilderCallback;
import com.bol.feign.provider.StaticUrlProvider;
import com.bol.feign.provider.UrlProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.Feign.Builder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.jaxrs.JAXRSContract;
import nl.unionsoft.sysstate.common.Constants;

/**
 * Example usage:
 * 
 * <pre>
 * 
 * public static void main(String[] args) {
 *     SysState sysState = getSysState("http://localhost:8680", "some-token");
 *     Instance instance = sysState.getInstance(1l);
 * }
 * </pre>
 * 
 * @author ckramer
 */
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
        return new ClientProvider(urlProvider, new BuilderCallback() {

            @Override
            public void configure(Builder builder) {

                ObjectMapper mapper = new ObjectMapper();

                //@formatter:off
                builder.contract(new JAXRSContract())
                .decoder(new JacksonDecoder(mapper))
                .encoder(new JacksonEncoder(mapper))
                .requestInterceptor(new HeaderRequestInterceptor("Accept", "application/json"))
                .requestInterceptor(new HeaderRequestInterceptor("Content-Type", "application/json"));
                //@formatter:on
                if (StringUtils.isNotEmpty(token)) {
                    builder.requestInterceptor(new HeaderRequestInterceptor(Constants.SECURITY_TOKEN_HEADER, token));
                }

            }
        }).create(SysState.class);
    }
}
