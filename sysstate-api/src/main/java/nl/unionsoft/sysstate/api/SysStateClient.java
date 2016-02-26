package nl.unionsoft.sysstate.api;

import org.apache.commons.lang.StringUtils;

import com.bol.feign.ClientProvider;
import com.bol.feign.HeaderRequestInterceptor;
import com.bol.feign.callback.BuilderCallback;
import com.bol.feign.provider.StaticUrlProvider;
import com.bol.feign.provider.UrlProvider;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

import feign.Feign.Builder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.jaxrs.JAXRSModule;
import nl.unionsoft.sysstate.common.Constants;

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

                // AnnotationIntrospector primary = new JacksonAnnotationIntrospector();

                // AnnotationIntrospector pair = new AnnotationIntrospector.Pair(primaryIntrospector, secondaryIntropsector);
                // AnnotationIntrospector pair = AnnotationIntrospector.pair(primary, primary);
                ObjectMapper mapper = new ObjectMapper();
                AnnotationIntrospector jaxbAnnotationIntrospector = new JaxbAnnotationIntrospector(mapper.getTypeFactory());
                //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                mapper.setAnnotationIntrospector(jaxbAnnotationIntrospector);
//                mapper.getDeserializationConfig().with(jaxbAnnotationIntrospector);
//                mapper.getSerializationConfig().with(jaxbAnnotationIntrospector);

                //@formatter:off
                builder.contract(new JAXRSModule.JAXRSContract())
                .decoder(new JacksonDecoder(mapper))
                .encoder(new JacksonEncoder(mapper))
                .requestInterceptor(new HeaderRequestInterceptor("Accept", "application/json"));
         
                if (StringUtils.isNotEmpty(token)) {
                    builder.requestInterceptor(new HeaderRequestInterceptor(Constants.SECURITY_TOKEN_HEADER, token));
                }

                
            }
        }).create(SysState.class);
    }
    
}
