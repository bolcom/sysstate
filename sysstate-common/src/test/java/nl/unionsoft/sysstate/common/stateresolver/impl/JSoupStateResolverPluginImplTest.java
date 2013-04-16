package nl.unionsoft.sysstate.common.stateresolver.impl;

import java.util.Properties;

import nl.unionsoft.common.util.PropertiesUtil;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Before;
import org.junit.Test;

public class JSoupStateResolverPluginImplTest {

    private JSoupStateResolverPluginImpl jSoupStateResolverPlugin;

    @Before
    public void before() {
        final HttpClient httpClient = new DefaultHttpClient();
        jSoupStateResolverPlugin = new JSoupStateResolverPluginImpl();
        jSoupStateResolverPlugin.setHttpClient(httpClient);
    }

    @Test
    public void doTest() {
        final StateDto state = new StateDto();
        final InstanceDto instance = new InstanceDto();
        final Properties properties = new Properties();
        properties.setProperty("url", "http://bolnl-docs/reporting/dashboard/db_rel_version.html");
        properties.setProperty("select", "table td:contains(APM) ~ td:eq(3)");
        instance.setConfiguration(PropertiesUtil.propertiesToString(properties));
        jSoupStateResolverPlugin.setState(instance, state);

    }
}
