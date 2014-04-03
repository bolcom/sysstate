package nl.unionsoft.sysstate.common.stateresolver.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import mockit.Mocked;
import mockit.NonStrictExpectations;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.logic.HttpClientLogic;
import nl.unionsoft.sysstate.plugins.http.JSoupStateResolverImpl;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class JSoupStateResolverImplTest {

    private JSoupStateResolverImpl jSoupStateResolverPlugin;


    @Mocked
    private HttpClientLogic httpClientLogic;
    
    @Mocked
    private DefaultHttpClient defaultHttpClient;
    
    @Before
    public void before() {
        jSoupStateResolverPlugin = new JSoupStateResolverImpl();
        jSoupStateResolverPlugin.setHttpClientLogic(httpClientLogic);
        //@formatter:off
        new NonStrictExpectations() {{
                httpClientLogic.getHttpClient("default");result = defaultHttpClient;
        }};
        //@formatter:on
    }

    @Test
    public void doTest() {
        final StateDto state = new StateDto();
        final InstanceDto instance = new InstanceDto();

        Map<String,String> properties = new HashMap<String, String>();
        properties.put("url", "http://someurl");
        properties.put("select", "table td:contains(TROL) ~ td:eq(3)");
        jSoupStateResolverPlugin.setState(instance, state, properties);
    }
}
