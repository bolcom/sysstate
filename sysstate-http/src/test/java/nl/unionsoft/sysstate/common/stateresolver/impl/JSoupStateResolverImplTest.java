package nl.unionsoft.sysstate.common.stateresolver.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.plugins.http.JSoupStateResolverImpl;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Before;
import org.junit.Test;

public class JSoupStateResolverImplTest {

    private JSoupStateResolverImpl jSoupStateResolverPlugin;

    @Before
    public void before() {
        final HttpClient httpClient = new DefaultHttpClient();
        jSoupStateResolverPlugin = new JSoupStateResolverImpl();
        jSoupStateResolverPlugin.setHttpClient(httpClient);
    }

    @Test
    public void doTest() {
        final StateDto state = new StateDto();
        final InstanceDto instance = new InstanceDto();

        Map<String,String> properties = new HashMap<String, String>();
        properties.put("url", "http://bolnl-docs/reporting/dashboard/db_rel_version.html");
        properties.put("select", "table td:contains(APM) ~ td:eq(3)");
        instance.setConfiguration(properties);
        jSoupStateResolverPlugin.setState(instance, state);
    }
}
